package br.com.unopay.api.billing.service;

import br.com.unopay.api.billing.model.Gateway;
import br.com.unopay.api.billing.model.Transaction;
import br.com.unopay.api.billing.repository.TransactionRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionService {

    private TransactionRepository repository;
    @Setter private Gateway gateway;

    public TransactionService(){}

    @Autowired
    public TransactionService(TransactionRepository repository, Gateway gateway){
        this.repository = repository;
        this.gateway = gateway;
    }

    public Transaction save(Transaction transaction) {
        log.info("CREDIT CARD NUMBER={}",transaction.getCreditCard().getNumber());
        return repository.save(transaction);
    }

    public Transaction findById(String id) {
        return repository.findOne(id);
    }

    public Transaction create(Transaction transaction) {
        Transaction created = save(transaction);
        gateway.createTransaction(transaction);
        return created;
    }
}
