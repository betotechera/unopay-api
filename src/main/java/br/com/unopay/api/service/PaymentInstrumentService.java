package br.com.unopay.api.service;

import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.repository.PaymentInstrumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentInstrumentService {

    private PaymentInstrumentRepository repository;

    @Autowired
    public PaymentInstrumentService(PaymentInstrumentRepository repository) {
        this.repository = repository;
    }

    public PaymentInstrument save(PaymentInstrument instrument) {
        return repository.save(instrument);
    }

    public PaymentInstrument findById(String id) {
        return repository.findOne(id);
    }
}
