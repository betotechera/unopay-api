package br.com.unopay.api.service;

import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.BatchClosingItem;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.repository.BatchClosingRepository;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BatchClosingService {

    private BatchClosingRepository repository;
    private ServiceAuthorizeService serviceAuthorizeService;

    @Autowired
    public BatchClosingService(BatchClosingRepository repository,
                               ServiceAuthorizeService serviceAuthorizeService) {
        this.repository = repository;
        this.serviceAuthorizeService = serviceAuthorizeService;
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
            .forEachOrdered(batchClosingItem -> {
                ServiceAuthorize currentAuthorize = batchClosingItem.getServiceAuthorize();
                BatchClosing currentBatClosing = getCurrentBatchClosing(currentAuthorize);
                currentBatClosing.addItem(batchClosingItem);
                currentBatClosing.updateValue(batchClosingItem.eventValue());
                repository.save(currentBatClosing);
            });
        }
    }

    private BatchClosing getCurrentBatchClosing(ServiceAuthorize currentAuthorize) {
        Optional<BatchClosing> batchClosing = repository
                .findFirstByEstablishmentIdAndHirerId(currentAuthorize.establishmentId(), currentAuthorize.hirerId());
        return batchClosing.orElse(new BatchClosing(currentAuthorize));
    }

    public Set<BatchClosing> findByEstablishmentId(String establishmentId) {
        return repository.findByEstablishmentId(establishmentId);
    }
}
