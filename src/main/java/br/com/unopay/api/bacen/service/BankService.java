package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Bank;
import br.com.unopay.api.bacen.repository.BankRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.BANK_NOT_FOUND;

@Service
public class BankService {

    private BankRepository repository;

    public BankService(){}

    @Autowired
    public BankService(BankRepository repository) {
        this.repository = repository;
    }

    public Bank findBacenCode(Integer bacenCode){
        Optional<Bank> bank = repository.findByBacenCode(bacenCode);
        return bank.orElseThrow(()->UnovationExceptions.notFound().withErrors(BANK_NOT_FOUND));
    }

    public List<Bank> findAll(String key){
        return repository.findAll();
    }
}
