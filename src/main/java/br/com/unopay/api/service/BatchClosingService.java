package br.com.unopay.api.service;

import br.com.unopay.api.config.Queues;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.BatchClosingItem;
import br.com.unopay.api.model.BatchClosingSituation;
import br.com.unopay.api.model.DocumentSituation;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.BatchClosingFilter;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.repository.BatchClosingRepository;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.api.util.Time;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.model.BatchClosingSituation.CANCELED;
import static br.com.unopay.api.model.BatchClosingSituation.DOCUMENT_RECEIVED;
import static br.com.unopay.api.model.BatchClosingSituation.PROCESSING_AUTOMATIC_BATCH;
import static br.com.unopay.api.uaa.exception.Errors.BATCH_ALREADY_RUNNING;
import static br.com.unopay.api.uaa.exception.Errors.BATCH_CLOSING_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_BATCH;
import static br.com.unopay.api.uaa.exception.Errors.INVOICE_NOT_REQUIRED_FOR_BATCH;
import static br.com.unopay.api.uaa.exception.Errors.SITUATION_NOT_ALLOWED;

@Slf4j
@Service
public class BatchClosingService {

    private BatchClosingRepository repository;
    private ServiceAuthorizeService authorizeService;
    private BatchClosingItemService batchClosingItemService;
    private UserDetailService userDetailService;
    @Setter private NotificationService notificationService;
    @Setter private Notifier notifier;


    public BatchClosingService(){}

    @Autowired
    public BatchClosingService(BatchClosingRepository repository,
                               ServiceAuthorizeService authorizeService,
                               BatchClosingItemService batchClosingItemService,
                               UserDetailService userDetailService,
                               NotificationService notificationService,
                               Notifier notifier) {
        this.repository = repository;
        this.authorizeService = authorizeService;
        this.batchClosingItemService = batchClosingItemService;
        this.userDetailService = userDetailService;
        this.notificationService = notificationService;
        this.notifier = notifier;
    }

    public BatchClosing save(BatchClosing batchClosing) {
        return repository.save(batchClosing);
    }

    public BatchClosing findById(String id) {
        Optional<BatchClosing> batchClosing = repository.findById(id);
        return batchClosing.orElseThrow(()-> UnovationExceptions.notFound().withErrors(BATCH_CLOSING_NOT_FOUND));
    }

    @Transactional
    public void process(String establishmentId){
        process(establishmentId, today());
    }

    @Transactional
    public BatchClosing process(String establishmentId, Date at) {
        try (Stream<ServiceAuthorize> stream = authorizeService.findByEstablishmentAndCreatedAt(establishmentId, at)){
            Set<BatchClosing> batchClosing = stream.map(BatchClosingItem::new)
                    .map(this::processBatchClosingItem).collect(Collectors.toSet());
            batchClosing.forEach(this::updateBatchClosingSituation);
            return batchClosing.stream().findFirst().orElse(null);
        }
    }

    public void create(BatchClosing batchClosing) {
        checkAlreadyRunning(batchClosing.establishmentId());
        notifier.notify(Queues.BATCH_CLOSING, batchClosing);
    }

    private void checkAlreadyRunning(String establishmentId) {
        Optional<BatchClosing> processingBatch = repository
                                        .findByEstablishmentIdAndSituation(establishmentId, PROCESSING_AUTOMATIC_BATCH);
        processingBatch.ifPresent((ThrowingConsumer)-> {
            log.warn("Attempt to run a job that is already running for establishment={}", establishmentId);
            throw UnovationExceptions.unprocessableEntity().withErrors(BATCH_ALREADY_RUNNING);
        });
    }

    @Transactional
    public void updateInvoiceInformation(String userEmail, List<BatchClosingItem> batchClosingItems) {
        Set<BatchClosing> batchClosings = updateBatchItems(batchClosingItems);
        UserDetail currentUser = getUserByEmail(userEmail);
        batchClosings.forEach(batchClosing -> checkUserQualifiedForBatch(currentUser, batchClosing));
        updateBatchesSituation(batchClosings);
    }

    @Transactional
    public void cancel(String userEmail, String batchId) {
        UserDetail currentUser = getUserByEmail(userEmail);
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
        checkAllowedSituation(newSituation);
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

    private BatchClosing processBatchClosingItem(BatchClosingItem batchClosingItem) {
        ServiceAuthorize currentAuthorize = batchClosingItem.getServiceAuthorize();
        BatchClosing currentBatchClosing = getCurrentBatchClosing(currentAuthorize);
        currentBatchClosing.addItem(batchClosingItem);
        currentBatchClosing.updateValue(batchClosingItem.eventValue());
        authorizeService.save(currentAuthorize.defineBatchClosingDate());
        return repository.save(currentBatchClosing);
    }

    private void updateBatchClosingSituation(BatchClosing batchClosing){
        repository.save(batchClosing.defineSituation());
        notificationService.sendBatchClosedMail(batchClosing.establishmentBatchMail(), batchClosing);
    }

    private BatchClosing getCurrentBatchClosing(ServiceAuthorize currentAuthorize) {
        Optional<BatchClosing>  batchClosing = repository
                .findFirstByEstablishmentIdAndHirerIdAndSituation(currentAuthorize.establishmentId(),
                        currentAuthorize.hirerId(), PROCESSING_AUTOMATIC_BATCH);
        return batchClosing.orElse(new BatchClosing(currentAuthorize,getTotal()));
    }

    private Long getTotal() {
        return repository.count();
    }

    private void updateBatchesSituation(Set<BatchClosing> batchClosings) {
        batchClosings.forEach(batchClosing -> {
            updateBatchSituation(batchClosing,DOCUMENT_RECEIVED);
        });
    }

    public void updateBatchSituation(BatchClosing batchClosing, BatchClosingSituation situation) {
        validateBatchClosing(batchClosing);
        batchClosing.setSituation(situation);
        repository.save(batchClosing);
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
                    .withErrors(INVOICE_NOT_REQUIRED_FOR_BATCH.withOnlyArgument(batchClosing.getId()));
        }
    }

    private void checkUserQualifiedForBatch(UserDetail currentUser, BatchClosing batchClosing) {
        if(currentUser.isIssuerType() && !batchClosing.myIssuerIs(currentUser.getIssuer())){
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(Errors.ISSUER_NOT_QUALIFIED_FOR_THIS_BATCH.withOnlyArgument(currentUser.issuerId()));
        }
        else {
            if(!batchClosing.myEstablishmentIs(currentUser.getEstablishment())){
                throw UnovationExceptions.unprocessableEntity()
                        .withErrors(ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_BATCH
                                .withOnlyArgument(currentUser.establishmentId()));
            }
        }
    }

    private void checkAllowedSituation(BatchClosingSituation newSituation) {
        if(CANCELED.equals(newSituation)){
            throw UnovationExceptions.unprocessableEntity().withErrors(SITUATION_NOT_ALLOWED);
        }
    }
    private BatchClosingFilter buildFilterBy(BatchClosingFilter filter, UserDetail currentUser) {
        if(currentUser.isEstablishmentType()) {
            filter.setEstablishment(currentUser.establishmentId());
        }
        if(currentUser.isIssuerType()) {
            filter.setIssuer(currentUser.issuerId());
        }
        if(currentUser.isAccreditedNetworkType()) {
            filter.setAccreditedNetwork(currentUser.accreditedNetworkId());
        }
        if(currentUser.isHirerType()) {
            filter.setHirer(currentUser.hirerId());
        }
        return filter;
    }
    private Date today() {
        return Time.create();
    }

    public Page<BatchClosing> findMyByFilter(String userEmail, BatchClosingFilter filter,
                                             UnovationPageRequest pageable) {
        return findByFilter(buildFilterBy(filter,getUserByEmail(userEmail)),pageable);
    }

    public Set<BatchClosing> findFinalizedByIssuerAndPaymentBefore(String issuerId, Date at){
        return repository.findByIssuerIdAndSituationAndPaymentReleaseDateTimeBeforeOrderByEstablishment(issuerId,
                                                                            BatchClosingSituation.FINALIZED, at);
    }

    public Set<BatchClosing> findFinalizedByIssuerAndPaymentBeforeToday(String issuerId){
        return repository.findByIssuerIdAndSituationAndPaymentReleaseDateTimeBeforeOrderByEstablishment(issuerId,
                BatchClosingSituation.FINALIZED, today());
    }

    public List<BatchClosing> findAll(){
        return repository.findAll();
    }

    private UserDetail getUserByEmail(String userEmail) {
        return userDetailService.getByEmail(userEmail);
    }

}
