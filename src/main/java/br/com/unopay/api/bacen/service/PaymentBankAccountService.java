package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.bacen.repository.PaymentBankAccountRepository;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_ACCOUNT_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_ACCOUNT_NOT_FOUND;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentBankAccountService {

    private PaymentBankAccountRepository repository;

    private BankAccountService bankAccountService;

    @Autowired
    public PaymentBankAccountService(PaymentBankAccountRepository repository, BankAccountService bankAccountService) {
        this.repository = repository;
        this.bankAccountService = bankAccountService;
    }

    public PaymentBankAccount create(PaymentBankAccount paymentAccount) {
        bankAccountService.create(paymentAccount.getBankAccount());
        return repository.save(paymentAccount);
    }

    public PaymentBankAccount findById(String id) {
        if(id == null){
            throw UnovationExceptions.notFound().withErrors(PAYMENT_ACCOUNT_ID_REQUIRED);
        }
        Optional<PaymentBankAccount> account = repository.findById(id);
        return account.orElseThrow(()-> UnovationExceptions.notFound().withErrors(PAYMENT_ACCOUNT_NOT_FOUND));
    }

    public void update(String id, PaymentBankAccount paymentAccount) {
        findById(id);
        bankAccountService.update(paymentAccount.getBankAccountId(),paymentAccount.getBankAccount());
        repository.save(paymentAccount);
    }
}
