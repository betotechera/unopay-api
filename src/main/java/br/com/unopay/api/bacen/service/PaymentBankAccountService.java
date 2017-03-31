package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.bacen.repository.PaymentBankAccountRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_ACCOUNT_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_ACCOUNT_NOT_FOUND;

@Service
public class PaymentBankAccountService {

    @Autowired
    private PaymentBankAccountRepository repository;

    public PaymentBankAccount create(PaymentBankAccount paymentAccount) {
        return repository.save(paymentAccount);
    }

    public PaymentBankAccount findById(String id) {
        if(id == null) throw UnovationExceptions.notFound().withErrors(PAYMENT_ACCOUNT_ID_REQUIRED);
        PaymentBankAccount account = repository.findOne(id);
        if(account == null) throw UnovationExceptions.notFound().withErrors(PAYMENT_ACCOUNT_NOT_FOUND);
        return account;
    }
}
