package br.com.unopay.api.payment.service;

import br.com.unopay.api.payment.model.PaymentRemittance;
import br.com.unopay.api.payment.repository.PaymentRemittanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentRemittanceService {

    private PaymentRemittanceRepository repository;

    @Autowired
    public PaymentRemittanceService(PaymentRemittanceRepository repository) {
        this.repository = repository;
    }

    public PaymentRemittance create(PaymentRemittance paymentRemittance) {
        return repository.save(paymentRemittance);
    }

    public PaymentRemittance findById(String id) {
        return repository.findOne(id);
    }
}
