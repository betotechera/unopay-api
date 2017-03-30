package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Bank;
import br.com.unopay.api.bacen.repository.BankRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.BANK_NOT_FOUND;

@Service
public class BankService {

    @Autowired
    private BankRepository repository;


    public Bank findBacenCode(Integer bacenCode){
        Bank bank = repository.findOne(bacenCode);
        if(bank == null) throw UnovationExceptions.notFound().withErrors(BANK_NOT_FOUND);
        return bank;
    }
}
