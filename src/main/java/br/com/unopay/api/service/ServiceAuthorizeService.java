package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.service.EstablishmentService;
import br.com.unopay.api.bacen.service.EventService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.repository.ServiceAuthorizeRepository;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_DOCUMENT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.SERVICE_AUTHORIZE_NOT_FOUND;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ServiceAuthorizeService {

    private ServiceAuthorizeRepository repository;
    private ContractorInstrumentCreditService instrumentCreditService;
    private EventService eventService;
    private UserDetailService userDetailService;
    private EstablishmentService establishmentService;
    private ContractService contractService;

    @Autowired
    public ServiceAuthorizeService(ServiceAuthorizeRepository repository,
                                   ContractorInstrumentCreditService instrumentCreditService,
                                   EventService eventService,
                                   UserDetailService userDetailService,
                                   EstablishmentService establishmentService,
                                   ContractService contractService) {
        this.repository = repository;
        this.instrumentCreditService = instrumentCreditService;
        this.eventService = eventService;
        this.userDetailService = userDetailService;
        this.establishmentService = establishmentService;
        this.contractService = contractService;
    }

    public ServiceAuthorize create(String userEmail, ServiceAuthorize serviceAuthorize) {
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        validateEstablishment(serviceAuthorize, currentUser);
        defineEstablishment(serviceAuthorize, currentUser);
        defineContract(serviceAuthorize);

        serviceAuthorize.setUser(currentUser);
        ContractorInstrumentCredit instrumentCredit = instrumentCreditService
                                                    .findById(serviceAuthorize.getContractorInstrumentCredit().getId());
        serviceAuthorize.setContractorInstrumentCredit(instrumentCredit);
        serviceAuthorize.setContractor(instrumentCredit.getContract().getContractor());
        serviceAuthorize.setEvent(eventService.findById(serviceAuthorize.getEvent().getId()));

        return repository.save(serviceAuthorize);
    }


    private void validateEstablishment(ServiceAuthorize serviceAuthorize, UserDetail currentUser) {
        if (!currentUser.isEstablishmentType() && !serviceAuthorize.withEstablishmentDocument()) {
            throw UnovationExceptions.unprocessableEntity().withErrors(ESTABLISHMENT_DOCUMENT_REQUIRED);
        }
        String establishmentId = serviceAuthorize.establishmentId();
        if (currentUser.isEstablishmentType()) {
            establishmentId = currentUser.establishmentId();
        }
        Contract contract = contractService.findById(serviceAuthorize.getContract().getId());
        if (contract.withEstablishmentRestriction()) {
            findEstablishment(establishmentId, contract.getEstablishments())
                    .orElseThrow(() -> UnovationExceptions.unprocessableEntity()
                            .withErrors(ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT));
        }
    }

    private Optional<Establishment> findEstablishment(String establishmentId, List<Establishment> establishments) {
        return establishments.stream()
                .filter(e -> Objects.equals(e.getId(),establishmentId))
                .reduce((first, last) -> last);
    }

    private void defineEstablishment(ServiceAuthorize serviceAuthorize, UserDetail currentUser) {
        if(currentUser.isEstablishmentType()){
            serviceAuthorize.setEstablishment(currentUser.getEstablishment());
            return;
        }
        serviceAuthorize
                .setEstablishment(establishmentService
                        .findByDocumentNumber(serviceAuthorize.establishmentDocumentNumber()));
    }

    private void defineContract(ServiceAuthorize serviceAuthorize) {
        Contract contract = contractService.findById(serviceAuthorize.getContract().getId());
        serviceAuthorize.setContract(contract);
    }


    public ServiceAuthorize findById(String id) {
        Optional<ServiceAuthorize> serviceAuthorize =  repository.findById(id);
        return serviceAuthorize.orElseThrow(()-> UnovationExceptions.notFound().withErrors(SERVICE_AUTHORIZE_NOT_FOUND));
    }
}
