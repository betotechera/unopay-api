package br.com.unopay.api.service;

import br.com.unopay.api.model.BatchClosingItem;
import br.com.unopay.api.repository.BatchClosingItemRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.BATCH_CLOSING_ITEM_NOT_FOUND;

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
        Optional<BatchClosingItem> closingItem = repository.findById(id);
        return closingItem.orElseThrow(()-> UnovationExceptions.notFound().withErrors(BATCH_CLOSING_ITEM_NOT_FOUND));
    }
}
