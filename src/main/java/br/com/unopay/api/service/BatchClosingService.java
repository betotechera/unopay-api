package br.com.unopay.api.service;

import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.BatchClosingItem;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.BatchClosingFilter;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.repository.BatchClosingRepository;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Optional;
import java.util.Set;
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
    @Setter private NotificationService notificationService;

    @Autowired
    public BatchClosingService(BatchClosingRepository repository,
                               ServiceAuthorizeService serviceAuthorizeService,
                               NotificationService notificationService) {
        this.repository = repository;
        this.serviceAuthorizeService = serviceAuthorizeService;
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
            .forEach(this::updateBatchClosingItemSituation);
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

    private void updateBatchClosingItemSituation(ServiceAuthorize currentAuthorize){
        BatchClosing currentBatClosing = getCurrentBatchClosing(currentAuthorize);
        repository.save(currentBatClosing.defineSituation());
        notificationService.sendBatchClosingMail(currentBatClosing.establishmentBatchMail(), currentBatClosing);
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

}
