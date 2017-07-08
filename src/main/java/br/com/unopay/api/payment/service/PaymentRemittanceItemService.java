package br.com.unopay.api.payment.service;

import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import br.com.unopay.api.payment.repository.PaymentRemittanceItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentRemittanceItemService {

    private PaymentRemittanceItemRepository repository;

    @Autowired
    public PaymentRemittanceItemService(PaymentRemittanceItemRepository repository) {
        this.repository = repository;
    }

    public PaymentRemittanceItem create(PaymentRemittanceItem paymentRemittance) {
        return repository.save(paymentRemittance);
    }

    public PaymentRemittanceItem findById(String id) {
        return repository.findOne(id);
    }
}
