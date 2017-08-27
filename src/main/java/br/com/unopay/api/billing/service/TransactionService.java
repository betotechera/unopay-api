package br.com.unopay.api.billing.service;

import br.com.unopay.api.billing.model.Transaction;
import br.com.unopay.api.billing.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private TransactionRepository repository;
    public TransactionService(){}

    @Autowired
    public TransactionService(TransactionRepository repository){
        this.repository = repository;
    }

    public Transaction create(Transaction transaction) {
        return repository.save(transaction);
    }

    public Transaction findById(String id) {
        return repository.findOne(id);
    }
}
