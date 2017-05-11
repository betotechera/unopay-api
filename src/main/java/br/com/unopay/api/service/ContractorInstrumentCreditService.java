package br.com.unopay.api.service;

import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.repository.ContractorInstrumentCreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContractorInstrumentCreditService {

    private ContractorInstrumentCreditRepository repository;

    @Autowired
    public ContractorInstrumentCreditService(ContractorInstrumentCreditRepository repository) {
        this.repository = repository;
    }

    public ContractorInstrumentCredit findById(String id) {
        return  repository.findOne(id);
    }

    public ContractorInstrumentCredit insert(ContractorInstrumentCredit instrumentCredit) {
        instrumentCredit.setupMyCreate();
        return repository.save(instrumentCredit);
    }
}
