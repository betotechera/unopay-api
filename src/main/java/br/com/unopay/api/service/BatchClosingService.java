package br.com.unopay.api.service;

import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.BatchClosingItem;
import br.com.unopay.api.model.BatchClosingSituation;
import br.com.unopay.api.model.DocumentSituation;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.BatchClosingFilter;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.repository.BatchClosingRepository;
import static br.com.unopay.api.uaa.exception.Errors.BATCH_CLOSING_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_BATCH;
import static br.com.unopay.api.uaa.exception.Errors.INVOICE_NOT_REQUIRED_FOR_BATCH;
import static br.com.unopay.api.uaa.exception.Errors.SITUATION_NOT_ALLOWED;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BatchClosingService {

    private BatchClosingRepository repository;
    private ServiceAuthorizeService authorizeService;
    private BatchClosingItemService batchClosingItemService;
    private UserDetailService userDetailService;
    @Setter private NotificationService notificationService;

    @Autowired
    public BatchClosingService(BatchClosingRepository repository,
                               ServiceAuthorizeService authorizeService,
                               BatchClosingItemService batchClosingItemService,
                               UserDetailService userDetailService,
                               NotificationService notificationService) {
        this.repository = repository;
        this.authorizeService = authorizeService;
        this.batchClosingItemService = batchClosingItemService;
        this.userDetailService = userDetailService;
        this.notificationService = notificationService;
    }

    public BatchClosing save(BatchClosing batchClosing) {
        return repository.save(batchClosing);
    }

    public BatchClosing findById(String id) {
        Optional<BatchClosing> batchClosing = repository.findById(id);
        return batchClosing.orElseThrow(()-> UnovationExceptions.notFound().withErrors(BATCH_CLOSING_NOT_FOUND));
    }

    @Transactional
    public void create(String establishmentId){
        create(establishmentId, today());
    }

    @Transactional
    public void create(String establishmentId, Date at) {
        try (Stream<ServiceAuthorize> stream = authorizeService.findByEstablishmentAndCreatedAt(establishmentId, at)){
            stream.map(BatchClosingItem::new)
            .map(this::processBatchClosingItem)
            .forEach(this::updateBatchClosingSituation);
        }
    }

    @Transactional
    public void updateInvoiceInformation(String userEmail, List<BatchClosingItem> batchClosingItems) {
        Set<BatchClosing> batchClosings = updateBatchItems(batchClosingItems);
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        batchClosings.forEach(batchClosing -> checkUserQualifiedForBatch(currentUser, batchClosing));
        updateBatchSituation(batchClosings);
    }

    @Transactional
    public void cancel(String userEmail, String batchId) {
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        BatchClosing current = findById(batchId);
        checkUserQualifiedForBatch(currentUser, current);
        current.getBatchClosingItems().forEach(closingItem -> {
            authorizeService.save(closingItem.resetAuthorizeBatchClosingDate());
            batchClosingItemService.save(closingItem.cancelDocumentInvoice());
        });
        repository.save(current.cancel());
    }

    public void review(String batchId, BatchClosingSituation newSituation) {
        BatchClosing current = findById(batchId);
        if(BatchClosingSituation.CANCELED.equals(newSituation)){
            throw UnovationExceptions.unprocessableEntity().withErrors(SITUATION_NOT_ALLOWED);
        }
        current.checkCanBeChanged();
        current.setSituation(newSituation);
        repository.save(current);
    }

    public Set<BatchClosing> findByEstablishmentId(String establishmentId) {
        return repository.findByEstablishmentId(establishmentId);
    }

    public Page<BatchClosing> findByFilter(BatchClosingFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private ServiceAuthorize processBatchClosingItem(BatchClosingItem batchClosingItem) {
        ServiceAuthorize currentAuthorize = batchClosingItem.getServiceAuthorize();
        BatchClosing currentBatClosing = getCurrentBatchClosing(currentAuthorize);
        currentBatClosing.addItem(batchClosingItem);
        currentBatClosing.updateValue(batchClosingItem.eventValue());
        repository.save(currentBatClosing);
        return authorizeService.save(currentAuthorize.buildBatchClosingDate());
    }

    private void updateBatchClosingSituation(ServiceAuthorize currentAuthorize){
        BatchClosing currentBatchClosing = getCurrentBatchClosing(currentAuthorize);
        repository.save(currentBatchClosing.defineSituation());
        notificationService.sendBatchClosedMail(currentBatchClosing.establishmentBatchMail(), currentBatchClosing);
    }

    private BatchClosing getCurrentBatchClosing(ServiceAuthorize currentAuthorize) {
        Optional<BatchClosing> batchClosing = repository
                .findFirstByEstablishmentIdAndHirerId(currentAuthorize.establishmentId(), currentAuthorize.hirerId());
        return batchClosing.orElse(new BatchClosing(currentAuthorize));
    }

    private void updateBatchSituation(Set<BatchClosing> batchClosings) {
        batchClosings.forEach(batchClosing -> {
            validateBatchClosing(batchClosing);
            batchClosing.setSituation(BatchClosingSituation.DOCUMENT_RECEIVED);
            repository.save(batchClosing);
        });
    }

    private Set<BatchClosing> updateBatchItems(List<BatchClosingItem> batchClosingItems) {
        return batchClosingItems.stream().map(batchClosingItem -> {
            BatchClosingItem current = batchClosingItemService.findById(batchClosingItem.getId());
            current.updateOnly(batchClosingItem, "invoiceNumber", "invoiceDocumentUri");
            current.setInvoiceDocumentSituation(DocumentSituation.APPROVED);
            batchClosingItemService.save(current);
            return current.getBatchClosing();
        }).collect(Collectors.toSet());
    }

    private void validateBatchClosing(BatchClosing batchClosing) {
        if(!batchClosing.getIssueInvoice()){
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(INVOICE_NOT_REQUIRED_FOR_BATCH.withArguments(batchClosing.getId()));
        }
    }

    private void checkUserQualifiedForBatch(UserDetail currentUser, BatchClosing batchClosing) {
        if(!batchClosing.myEstablishmentIs(currentUser.getEstablishment())){
            throw UnovationExceptions.unprocessableEntity().withErrors(ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_BATCH);
        }
    }

    private Date today() {
        return new DateTime().withMillisOfDay(0).toDate();
    }
}
