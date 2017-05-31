package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.Contractor;
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

import javax.transaction.Transactional;

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

    @Transactional
    public ServiceAuthorize create(String userEmail, ServiceAuthorize serviceAuthorize) {
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        checkContractIntegrity(serviceAuthorize, currentUser);
        defineEstablishment(serviceAuthorize, currentUser);
        ContractorInstrumentCredit instrumentCredit = getValidContractorInstrumentCredit(serviceAuthorize);
        validateEvent(serviceAuthorize);
        serviceAuthorize.setReferences(currentUser, instrumentCredit);
        serviceAuthorize.setMeUp(instrumentCredit);
        instrumentCreditService.subtract(instrumentCredit.getId(), serviceAuthorize.getEventValue());
        return repository.save(serviceAuthorize);
    }

    private void checkContractIntegrity(ServiceAuthorize serviceAuthorize, UserDetail currentUser) {
        serviceAuthorize.validateServiceType();
        serviceAuthorize.checkEstablishmentDocumentWhenRequired(currentUser);
        Contract contract = getValidContract(serviceAuthorize.contractId(), serviceAuthorize.getContractor());
        String establishmentId = serviceAuthorize.getCurrentEstablishmentId(currentUser);
        checkEstablishmentRestriction(contract, establishmentId);
    }

    private void validateEvent(ServiceAuthorize serviceAuthorize) {
        Event event = getAcceptableEvent(serviceAuthorize);
        serviceAuthorize.validateEvent(event);
    }

    private Event getAcceptableEvent(ServiceAuthorize serviceAuthorize) {
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
        if(!contract.meetsRestrictions(establishmentId)){
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT);
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

    private Contract getValidContract(String contractId, Contractor contractor) {
        Contract contract = contractService.findById(contractId);
        contract.validateActive();
        contract.validateContractor(contractor);
        return contract;
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
