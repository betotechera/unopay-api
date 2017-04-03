package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Bank;
import br.com.unopay.api.bacen.repository.BankRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Bank> findAll(){
        return repository.findAll();
    }
}
