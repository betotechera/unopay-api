package br.com.unopay.api.market.service;

import br.com.unopay.api.market.model.NegotiationBilling;
import br.com.unopay.api.market.repository.NegotiationBillingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NegotiationBillingService {

    private NegotiationBillingRepository repository;

    @Autowired
    public NegotiationBillingService(NegotiationBillingRepository repository) {
        this.repository = repository;
    }

    public NegotiationBilling save(NegotiationBilling billing) {
        return repository.save(billing);
    }

    public NegotiationBilling findById(String id) {
        return repository.findOne(id);
    }
}
