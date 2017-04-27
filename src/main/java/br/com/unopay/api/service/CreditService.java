package br.com.unopay.api.service;

import br.com.unopay.api.model.Credit;
import br.com.unopay.api.repository.CreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreditService {

    private CreditRepository repository;

    @Autowired
    public CreditService(CreditRepository repository) {
        this.repository = repository;
    }

    public Credit save(Credit credit) {
        credit.validate();
        credit.setupMyCreate();
        return repository.save(credit);
    }

    public Credit findById(String id) {
        return repository.findOne(id);
    }
}
