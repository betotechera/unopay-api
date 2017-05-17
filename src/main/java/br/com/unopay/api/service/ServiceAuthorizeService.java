package br.com.unopay.api.service;

import br.com.unopay.api.bacen.service.EstablishmentService;
import br.com.unopay.api.bacen.service.EventService;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.repository.ServiceAuthorizeRepository;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceAuthorizeService {

    private ServiceAuthorizeRepository repository;
    private ContractorInstrumentCreditService instrumentCreditService;
    private EventService eventService;
    private UserDetailRepository userDetailRepository;
    private EstablishmentService establishmentService;

    @Autowired
    public ServiceAuthorizeService(ServiceAuthorizeRepository repository,
                                   ContractorInstrumentCreditService instrumentCreditService,
                                   EventService eventService,
                                   UserDetailRepository userDetailRepository,
                                   EstablishmentService establishmentService) {
        this.repository = repository;
        this.instrumentCreditService = instrumentCreditService;
        this.eventService = eventService;
        this.userDetailRepository = userDetailRepository;
        this.establishmentService = establishmentService;
    }

    public ServiceAuthorize save(ServiceAuthorize serviceAuthorize) {
        ContractorInstrumentCredit instrumentCredit = instrumentCreditService
                                                    .findById(serviceAuthorize.getContractorInstrumentCredit().getId());
        serviceAuthorize.setContractorInstrumentCredit(instrumentCredit);
        serviceAuthorize.setContract(instrumentCredit.getContract());
        serviceAuthorize.setContractor(instrumentCredit.getContract().getContractor());
        serviceAuthorize.setEvent(eventService.findById(serviceAuthorize.getEvent().getId()));
        serviceAuthorize.setEstablishment(establishmentService.findById(serviceAuthorize.getEstablishment().getId()));
        serviceAuthorize.setUser(userDetailRepository.findById(serviceAuthorize.getUser().getId()));
        return repository.save(serviceAuthorize);
    }

    public ServiceAuthorize findById(String id) {
        return repository.findOne(id);
    }
}
