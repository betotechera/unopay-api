package br.com.unopay.api.service;

import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.model.Credit;
import br.com.unopay.api.repository.CreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreditService {

    private CreditRepository repository;
    private HirerService hirerService;

    @Autowired
    public CreditService(CreditRepository repository,
                         HirerService hirerService) {
        this.repository = repository;
        this.hirerService = hirerService;
    }

    public Credit insert(Credit credit) {
        credit.validate();
        credit.setupMyCreate();
        hirerService.findByDocumentNumber(credit.getHirerDocument());
        return repository.save(credit);
    }

    public Credit findById(String id) {
        return repository.findOne(id);
    }
}
