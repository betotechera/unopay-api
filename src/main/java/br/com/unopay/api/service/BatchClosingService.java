package br.com.unopay.api.service;

import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.BatchClosingItem;
import br.com.unopay.api.model.BatchClosingSituation;
import br.com.unopay.api.model.DocumentSituation;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.BatchClosingFilter;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.repository.BatchClosingRepository;
import static br.com.unopay.api.uaa.exception.Errors.INVOICE_NOT_REQUIRED_FOR_BATCH;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BatchClosingService {

    private BatchClosingRepository repository;
    private ServiceAuthorizeService serviceAuthorizeService;
    private BatchClosingItemService batchClosingItemService;
    @Setter private NotificationService notificationService;

    @Autowired
    public BatchClosingService(BatchClosingRepository repository,
                               ServiceAuthorizeService serviceAuthorizeService,
                               BatchClosingItemService batchClosingItemService,
                               NotificationService notificationService) {
        this.repository = repository;
        this.serviceAuthorizeService = serviceAuthorizeService;
        this.batchClosingItemService = batchClosingItemService;
        this.notificationService = notificationService;
    }

    public BatchClosing save(BatchClosing batchClosing) {
        return repository.save(batchClosing);
    }

    public BatchClosing findById(String id) {
        return repository.findOne(id);
    }

    @Transactional
    public void create(String establishmentId) {
        try (Stream<ServiceAuthorize> stream = serviceAuthorizeService.findByEstablishment(establishmentId)){
            stream.map(BatchClosingItem::new)
            .map(this::processBatchClosingItem)
            .forEach(this::updateBatchClosingSituation);
        }
    }

    private ServiceAuthorize processBatchClosingItem(BatchClosingItem batchClosingItem) {
        ServiceAuthorize currentAuthorize = batchClosingItem.getServiceAuthorize();
        BatchClosing currentBatClosing = getCurrentBatchClosing(currentAuthorize);
        currentBatClosing.addItem(batchClosingItem);
        currentBatClosing.updateValue(batchClosingItem.eventValue());
        repository.save(currentBatClosing);
        return serviceAuthorizeService.save(currentAuthorize.buildBatchSlosingDate());
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

    public Set<BatchClosing> findByEstablishmentId(String establishmentId) {
        return repository.findByEstablishmentId(establishmentId);
    }

    public Page<BatchClosing> findByFilter(BatchClosingFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    @Transactional
    public void updateInvoiceInformation(List<BatchClosingItem> batchClosingItems) {
        Set<BatchClosing> batchClosingStream = updateBatchItems(batchClosingItems);
        updateBatch(batchClosingStream);
    }

    private void updateBatch(Set<BatchClosing> batchClosings) {
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
}
