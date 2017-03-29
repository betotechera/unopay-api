package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.repository.IssuerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IssuerService {

    @Autowired
    private IssuerRepository repository;

    public Issuer create(Issuer issuer) {
        return repository.save(issuer);
    }

    public Issuer findById(String id) {
        return  repository.findOne(id);
    }
}
