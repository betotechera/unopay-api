package br.com.unopay.api.payment.service;

import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import br.com.unopay.api.payment.model.RemittanceSituation;
import br.com.unopay.api.payment.repository.PaymentRemittanceItemRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentRemittanceItemService {

    private PaymentRemittanceItemRepository repository;

    @Autowired
    public PaymentRemittanceItemService(PaymentRemittanceItemRepository repository) {
        this.repository = repository;
    }

    public PaymentRemittanceItem save(PaymentRemittanceItem paymentRemittance) {
        return repository.save(paymentRemittance);
    }

    public Optional<PaymentRemittanceItem> findProcessingByEstablishment(String establishmentId){
        return repository.findByEstablishmentIdAndSituation(establishmentId, RemittanceSituation.PROCESSING);
    }

    public PaymentRemittanceItem findById(String id) {
        return repository.findOne(id);
    }
}
