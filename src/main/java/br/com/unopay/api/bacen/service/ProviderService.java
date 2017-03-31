package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Provider;
import br.com.unopay.api.bacen.repository.ProviderRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.PROVIDER_NOT_FOUND;

@Service
public class ProviderService {

    @Autowired
    private ProviderRepository repository;

    public Provider create(Provider provider) {
        return repository.save(provider);
    }

    public void update(String id, Provider event) {
        Provider current = findById(id);
        current.setName(event.getName());
        repository.save(current);

    }

    public Provider findById(String id) {
        Provider provider = repository.findOne(id);
        if(provider == null) throw UnovationExceptions.notFound().withErrors(PROVIDER_NOT_FOUND);
        return provider;
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }
}
