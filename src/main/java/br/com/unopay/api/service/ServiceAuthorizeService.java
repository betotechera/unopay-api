package br.com.unopay.api.service;

import br.com.unopay.api.bacen.service.EstablishmentService;
import br.com.unopay.api.bacen.service.EventService;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.repository.ServiceAuthorizeRepository;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_DOCUMENT_REQUIRED;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceAuthorizeService {

    private ServiceAuthorizeRepository repository;
    private ContractorInstrumentCreditService instrumentCreditService;
    private EventService eventService;
    private UserDetailService userDetailService;
    private EstablishmentService establishmentService;

    @Autowired
    public ServiceAuthorizeService(ServiceAuthorizeRepository repository,
                                   ContractorInstrumentCreditService instrumentCreditService,
                                   EventService eventService,
                                   UserDetailService userDetailService,
                                   EstablishmentService establishmentService) {
        this.repository = repository;
        this.instrumentCreditService = instrumentCreditService;
        this.eventService = eventService;
        this.userDetailService = userDetailService;
        this.establishmentService = establishmentService;
    }

    public ServiceAuthorize create(String userEmail, ServiceAuthorize serviceAuthorize) {
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        validateEstablishment(serviceAuthorize, currentUser);
        defineEstablishment(serviceAuthorize, currentUser);
        serviceAuthorize.setUser(currentUser);

        ContractorInstrumentCredit instrumentCredit = instrumentCreditService
                                                    .findById(serviceAuthorize.getContractorInstrumentCredit().getId());
        serviceAuthorize.setContractorInstrumentCredit(instrumentCredit);
        serviceAuthorize.setContract(instrumentCredit.getContract());
        serviceAuthorize.setContractor(instrumentCredit.getContract().getContractor());
        serviceAuthorize.setEvent(eventService.findById(serviceAuthorize.getEvent().getId()));

        return repository.save(serviceAuthorize);
    }

    private void validateEstablishment(ServiceAuthorize serviceAuthorize, UserDetail currentUser) {
        if(!currentUser.isEstablishmentType() && !serviceAuthorize.withEstablishmentDocument()){
            throw UnovationExceptions.unprocessableEntity().withErrors(ESTABLISHMENT_DOCUMENT_REQUIRED);
        }
    }

    private void defineEstablishment(ServiceAuthorize serviceAuthorize, UserDetail currentUser) {
        if(currentUser.isEstablishmentType()){
            serviceAuthorize.setEstablishment(currentUser.getEstablishment());
            return;
        }
        serviceAuthorize
                .setEstablishment(establishmentService
                        .findByDocumentNumber(serviceAuthorize.getEstablishmentDocumentNumber()));
    }

    public ServiceAuthorize findById(String id) {
        return repository.findOne(id);
    }
}
