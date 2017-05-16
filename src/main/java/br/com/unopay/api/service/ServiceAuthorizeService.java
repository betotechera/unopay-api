package br.com.unopay.api.service;

import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.repository.ServiceAuthorizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceAuthorizeService {

    private ServiceAuthorizeRepository repository;
    private ContractorInstrumentCreditService instrumentCreditService;

    @Autowired
    public ServiceAuthorizeService(ServiceAuthorizeRepository repository,
                                   ContractorInstrumentCreditService instrumentCreditService) {
        this.repository = repository;
        this.instrumentCreditService = instrumentCreditService;
    }

    public ServiceAuthorize save(ServiceAuthorize serviceAuthorize) {
        serviceAuthorize
                .setContractorInstrumentCredit(instrumentCreditService
                        .findById(serviceAuthorize.getContractorInstrumentCredit().getId()));
        return repository.save(serviceAuthorize);
    }

    public ServiceAuthorize findById(String id) {
        return repository.findOne(id);
    }
}
