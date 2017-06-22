package br.com.unopay.api.service;

import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.repository.BatchClosingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BatchClosingService {

    private BatchClosingRepository repository;

    @Autowired
    public BatchClosingService(BatchClosingRepository repository) {
        this.repository = repository;
    }

    public BatchClosing save(BatchClosing batchClosing) {
        return repository.save(batchClosing);
    }

    public BatchClosing findById(String id) {
        return repository.findOne(id);
    }
}
