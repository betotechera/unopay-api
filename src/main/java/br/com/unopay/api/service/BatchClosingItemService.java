package br.com.unopay.api.service;

import br.com.unopay.api.model.BatchClosingItem;
import br.com.unopay.api.repository.BatchClosingItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BatchClosingItemService {

    private BatchClosingItemRepository repository;

    @Autowired
    public BatchClosingItemService(BatchClosingItemRepository repository) {
        this.repository = repository;
    }

    public BatchClosingItem save(BatchClosingItem batchClosing) {
        return repository.save(batchClosing);
    }

    public BatchClosingItem findById(String id) {
        return repository.findOne(id);
    }
}
