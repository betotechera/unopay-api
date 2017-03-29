package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.repository.IssuerRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.ISSUER_NOT_FOUND;

@Service
public class IssuerService {

    @Autowired
    private IssuerRepository repository;

    public Issuer create(Issuer issuer) {
        return repository.save(issuer);
    }

    public Issuer findById(String id) {
        Issuer issuer = repository.findOne(id);
        if(issuer == null) throw UnovationExceptions.notFound().withErrors(ISSUER_NOT_FOUND);
        return  issuer;
    }

    public Issuer update(String id, Issuer updateIssuer) {
        Issuer issuer = findById(id);
        issuer.setTax(updateIssuer.getTax());
        return  repository.save(issuer);
    }
}
