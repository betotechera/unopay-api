package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.bacen.repository.BankAccountRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.unopay.api.uaa.exception.Errors.BANK_ACCOUNT_NOT_FOUND;

@Service
public class BankAccountService {

    @Autowired
    private BankAccountRepository repository;

    public List<BankAccount> findAll(List<String> ids){
        List<BankAccount> bankAccounts = repository.findByIdIn(ids);
        List<String> founds = bankAccounts.stream().map(BankAccount::getId).collect(Collectors.toList());
        List<String> notFounds = ids.stream().filter(id -> !founds.contains(id)).collect(Collectors.toList());
        if(!notFounds.isEmpty()) throw UnovationExceptions.notFound().withErrors(BANK_ACCOUNT_NOT_FOUND.withArguments(notFounds));
        return  bankAccounts;
    }
}
