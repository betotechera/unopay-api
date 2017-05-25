package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.service.EstablishmentService;
import br.com.unopay.api.bacen.service.EventService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.repository.ServiceAuthorizeRepository;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.unopay.api.uaa.exception.Errors.*;

@Service
public class ServiceAuthorizeService {

    private ServiceAuthorizeRepository repository;
    private ContractorInstrumentCreditService instrumentCreditService;
    private EventService eventService;
    private UserDetailService userDetailService;
    private EstablishmentService establishmentService;
    private ContractService contractService;
    private PaymentInstrumentService paymentInstrumentService;


    @Autowired
    public ServiceAuthorizeService(ServiceAuthorizeRepository repository,
                                   ContractorInstrumentCreditService instrumentCreditService,
                                   EventService eventService,
                                   UserDetailService userDetailService,
                                   EstablishmentService establishmentService,
                                   ContractService contractService,
                                   PaymentInstrumentService paymentInstrumentService) {
        this.repository = repository;
        this.instrumentCreditService = instrumentCreditService;
        this.eventService = eventService;
        this.userDetailService = userDetailService;
        this.establishmentService = establishmentService;
        this.contractService = contractService;
        this.paymentInstrumentService = paymentInstrumentService;
    }

    public ServiceAuthorize create(String userEmail, ServiceAuthorize serviceAuthorize) {
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        serviceAuthorize.validateServiceType();
        serviceAuthorize.checkEstablishmentDocumentWhenRequired(currentUser);
        Contract contract = getValidContract(serviceAuthorize);
        String establishmentId = serviceAuthorize.getCurrentEstablishmentId(currentUser);
        checkEstablishmentRestriction(contract, establishmentId);
        defineEstablishment(serviceAuthorize, currentUser);
        ContractorInstrumentCredit instrumentCredit = getValidContractorInstrumentCredit(serviceAuthorize);
        serviceAuthorize.setReferences(currentUser, instrumentCredit);
        defineValidEvent(serviceAuthorize);
        return repository.save(serviceAuthorize);
    }

    private void defineValidEvent(ServiceAuthorize serviceAuthorize) {
        Event event = getValidEvent(serviceAuthorize);
        serviceAuthorize.validateEvent();
        serviceAuthorize.setEvent(event);
    }

    private Event getValidEvent(ServiceAuthorize serviceAuthorize) {
        Event event = eventService.findById(serviceAuthorize.getEvent().getId());
        if(!event.toServiceType(serviceAuthorize.getServiceType())){
            throw UnovationExceptions.unprocessableEntity().withErrors(EVENT_NOT_ACCEPTED);
        }
        return event;
    }

    private ContractorInstrumentCredit getValidContractorInstrumentCredit(ServiceAuthorize serviceAuthorize) {
        ContractorInstrumentCredit instrumentCredit = instrumentCreditService
                                                            .findById(serviceAuthorize.contractorInstrumentCreditId());
        validateContractorPaymentCredit(serviceAuthorize, instrumentCredit);
        instrumentCredit.validate();
        updateValidPasswordWhenRequired(serviceAuthorize, instrumentCredit);

        paymentInstrumentService
                .checkPassword(instrumentCredit.getPaymentInstrumentId(), serviceAuthorize.instrumentPassword());
        return instrumentCredit;
    }

    private void checkEstablishmentRestriction(Contract contract, String establishmentId) {
        if (contract.withEstablishmentRestriction()) {
            checkEstablishmentIn(establishmentId, contract.getEstablishments());
        }
    }

    private void validateContractorPaymentCredit(ServiceAuthorize serviceAuthorize,
                                                 ContractorInstrumentCredit instrumentCredit) {
        if (!instrumentCredit.contractIs(serviceAuthorize.contractId())) {
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_NOT_QUALIFIED_FOR_THIS_CONTRACT);
        }
    }

    private void updateValidPasswordWhenRequired(ServiceAuthorize serviceAuthorize,
                                                 ContractorInstrumentCredit instrumentCredit) {
        if (!instrumentCredit.paymentInstrumentWithPassword()) {
            validateRequiredPasswordInformation(serviceAuthorize, instrumentCredit.getContract());
            paymentInstrumentService.changePassword(instrumentCredit
                    .getPaymentInstrumentId(), serviceAuthorize.instrumentPassword());
        }
    }

    private void validateRequiredPasswordInformation(ServiceAuthorize serviceAuthorize, Contract contract) {
        if (contract.getContractor().physicalPerson() && serviceAuthorize.getContractor().getBirthDate() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(CONTRACTOR_BIRTH_DATE_REQUIRED);
        }
        if (contract.getContractor().physicalPerson() && !serviceAuthorize.getContractor().getBirthDate()
                .equals(contract.getContractor().getBirthDate())) {
            throw UnovationExceptions.unprocessableEntity().withErrors(INCORRECT_CONTRACTOR_BIRTH_DATE);
        }
        if (StringUtils.isEmpty(serviceAuthorize.instrumentPassword())) {
            throw UnovationExceptions.unprocessableEntity().withErrors(INSTRUMENT_PASSWORD_REQUIRED);
        }
    }

    private Contract getValidContract(ServiceAuthorize serviceAuthorize) {
        Contract contract = contractService.findById(serviceAuthorize.contractId());
        contract.validateActive();
        contract.validateContractor(serviceAuthorize.getContractor());
        return contract;
    }

    private void checkEstablishmentIn(String establishmentId, List<Establishment> establishments) {
        findEstablishment(establishmentId, establishments)
                .orElseThrow(() -> UnovationExceptions.unprocessableEntity()
                        .withErrors(ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT));
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

    public ServiceAuthorize findById(String id) {
        Optional<ServiceAuthorize> serviceAuthorize =  repository.findById(id);
        return serviceAuthorize.orElseThrow(()->UnovationExceptions.notFound().withErrors(SERVICE_AUTHORIZE_NOT_FOUND));
    }
}
