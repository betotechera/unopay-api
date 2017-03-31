package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Provider;
import br.com.unopay.api.bacen.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProviderService {

    @Autowired
    private ProviderRepository repository;

    public Provider create(Provider provider) {
        return repository.save(provider);
    }
}
