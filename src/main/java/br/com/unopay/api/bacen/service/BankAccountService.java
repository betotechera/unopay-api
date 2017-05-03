package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Bank;
import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.bacen.repository.BankAccountRepository;
import static br.com.unopay.api.uaa.exception.Errors.BANK_ACCOUNT_NOT_FOUND;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankAccountService {

    private BankAccountRepository repository;

    private BankService bankService;

    @Autowired
    public BankAccountService(BankAccountRepository repository, BankService bankService) {
        this.repository = repository;
        this.bankService = bankService;
    }

    public List<BankAccount> findAll(List<String> ids){
        List<BankAccount> bankAccounts = repository.findByIdIn(ids);
        List<String> founds = bankAccounts.stream().map(BankAccount::getId).collect(Collectors.toList());
        List<String> notFounds = ids.stream().filter(id -> !founds.contains(id)).collect(Collectors.toList());
        if(!notFounds.isEmpty()) {
            throw UnovationExceptions.notFound().withErrors(BANK_ACCOUNT_NOT_FOUND
                    .withArguments(notFounds));
        }
        return  bankAccounts;
    }

    public BankAccount create(BankAccount account) {
        account.validate();
        addKnownBank(account);
        return repository.save(account);
    }

    public BankAccount update(String id, BankAccount account) {
        account.validate();
        BankAccount current = findById(id);
        addKnownBank(account);
        current.updateMe(account);
        return  repository.save(current);
    }

    public BankAccount findById(String id) {
        BankAccount account = repository.findOne(id);
        if(account == null) {
            throw UnovationExceptions.notFound().withErrors(BANK_ACCOUNT_NOT_FOUND);
        }
        return account;
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    private void addKnownBank(BankAccount account) {
        Bank bank = bankService.findBacenCode(account.getBacenCode());
        account.setBank(bank);
    }
}
