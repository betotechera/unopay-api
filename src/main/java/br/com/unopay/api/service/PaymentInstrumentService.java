package br.com.unopay.api.service;

import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.repository.PaymentInstrumentRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_INSTRUMENT_NOT_FOUND;

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
        PaymentInstrument instrument = repository.findOne(id);
        if(instrument == null) throw UnovationExceptions.notFound().withErrors(PAYMENT_INSTRUMENT_NOT_FOUND);
        return instrument;
    }

    public void update(String id, PaymentInstrument instrument) {
        PaymentInstrument current = findById(id);
        current.updateMe(instrument);
        repository.save(current);
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }
}
