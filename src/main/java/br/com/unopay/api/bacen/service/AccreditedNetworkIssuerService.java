package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.AccreditedNetworkIssuer;
import br.com.unopay.api.bacen.repository.AccreditedNetworkIssuerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccreditedNetworkIssuerService {

    private AccreditedNetworkIssuerRepository repository;

    @Autowired
    public AccreditedNetworkIssuerService(AccreditedNetworkIssuerRepository repository) {
        this.repository = repository;
    }

    public AccreditedNetworkIssuer create(AccreditedNetworkIssuer networkIssuer) {
        return repository.save(networkIssuer);
    }

    public AccreditedNetworkIssuer findById(String id) {
        return repository.findOne(id);
    }
}
