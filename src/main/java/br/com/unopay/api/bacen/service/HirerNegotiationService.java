package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.HirerNegotiation;
import br.com.unopay.api.bacen.repository.HirerNegotiationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HirerNegotiationService {

    private HirerNegotiationRepository repository;

    @Autowired
    public HirerNegotiationService(HirerNegotiationRepository repository) {
        this.repository = repository;
    }

    public HirerNegotiation save(HirerNegotiation negotiation) {
        return repository.save(negotiation);
    }

    public HirerNegotiation findById(String id) {
        return repository.findOne(id);
    }
}
