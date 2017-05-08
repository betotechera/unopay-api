package br.com.unopay.api.service;

import br.com.unopay.api.model.Credit;
import br.com.unopay.api.model.PaymentAccount;
import br.com.unopay.api.repository.PaymentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentAccountService {

    private PaymentAccountRepository repository;

    @Autowired
    public PaymentAccountService(PaymentAccountRepository repository) {
        this.repository = repository;
    }

    public PaymentAccount save(PaymentAccount paymentAccount) {
        paymentAccount.setupMyCreate();
        return repository.save(paymentAccount);
    }

    public PaymentAccount findById(String id) {
        return repository.findOne(id);
    }

    public PaymentAccount create(Credit credit) {
        return null;
    }
}
