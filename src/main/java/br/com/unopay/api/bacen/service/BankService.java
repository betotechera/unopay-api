package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Bank;
import br.com.unopay.api.bacen.repository.BankRepository;
import static br.com.unopay.api.config.CacheConfig.BANKS;
import static br.com.unopay.api.uaa.exception.Errors.BANK_NOT_FOUND;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankService {

    private BankRepository repository;

    public BankService(){}

    @Autowired
    public BankService(BankRepository repository) {
        this.repository = repository;
    }

    public Bank findBacenCode(Integer bacenCode){
        Bank bank = repository.findOne(bacenCode);
        if(bank == null) {
            throw UnovationExceptions.notFound().withErrors(BANK_NOT_FOUND);
        }
        return bank;
    }

    @Cacheable(value = BANKS,key="#key")
    public List<Bank> findAll(String key){
        return repository.findAll();
    }
}
