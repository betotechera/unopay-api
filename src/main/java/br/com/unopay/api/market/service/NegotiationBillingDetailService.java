package br.com.unopay.api.market.service;

import br.com.unopay.api.market.model.NegotiationBillingDetail;
import br.com.unopay.api.market.repository.NegotiationBillingDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NegotiationBillingDetailService {

    private NegotiationBillingDetailRepository repository;

    @Autowired
    public NegotiationBillingDetailService(NegotiationBillingDetailRepository repository) {
        this.repository = repository;
    }

    public NegotiationBillingDetail save(NegotiationBillingDetail billing) {
        return repository.save(billing);
    }

    public NegotiationBillingDetail findById(String id) {
        return repository.findOne(id);
    }
}
