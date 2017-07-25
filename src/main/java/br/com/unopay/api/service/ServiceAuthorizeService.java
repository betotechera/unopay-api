package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.bacen.service.EstablishmentService;
import br.com.unopay.api.bacen.service.EventService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.infra.UnopayEncryptor;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.ServiceAuthorizeFilter;
import br.com.unopay.api.repository.ServiceAuthorizeRepository;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.CONTRACTOR_BIRTH_DATE_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_NOT_QUALIFIED_FOR_THIS_CONTRACT;
import static br.com.unopay.api.uaa.exception.Errors.EVENT_NOT_ACCEPTED;
import static br.com.unopay.api.uaa.exception.Errors.INCORRECT_CONTRACTOR_BIRTH_DATE;
import static br.com.unopay.api.uaa.exception.Errors.INSTRUMENT_PASSWORD_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.SERVICE_AUTHORIZE_NOT_FOUND;

@Slf4j
@Service
public class ServiceAuthorizeService {

    private ServiceAuthorizeRepository repository;
    private ContractorInstrumentCreditService instrumentCreditService;
    private EventService eventService;
    private UserDetailService userDetailService;
    private EstablishmentService establishmentService;
    private ContractService contractService;
    private PaymentInstrumentService paymentInstrumentService;
    private UnopayEncryptor encryptor;
    @Setter
    private Notifier notifier;


    @Autowired
    public ServiceAuthorizeService(ServiceAuthorizeRepository repository,
                                   ContractorInstrumentCreditService instrumentCreditService,
                                   EventService eventService,
                                   UserDetailService userDetailService,
                                   EstablishmentService establishmentService,
                                   ContractService contractService,
                                   PaymentInstrumentService paymentInstrumentService,
                                   UnopayEncryptor encryptor, Notifier notifier) {
        this.repository = repository;
        this.instrumentCreditService = instrumentCreditService;
        this.eventService = eventService;
        this.userDetailService = userDetailService;
        this.establishmentService = establishmentService;
        this.contractService = contractService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.encryptor = encryptor;
        this.notifier = notifier;
    }

    @Transactional
    public ServiceAuthorize create(String userEmail, ServiceAuthorize authorize) {
        UserDetail currentUser = getUserByEmail(userEmail);
        checkContract(authorize, currentUser);
        defineEstablishment(authorize, currentUser);
        ContractorInstrumentCredit instrumentCredit = getValidContractorInstrumentCredit(authorize);
        authorize.setTypedPassword(encryptor.encrypt(authorize.paymentInstrumentPasswordAsByte()));
        authorize.setReferences(currentUser, instrumentCredit);
        validateEvent(authorize);
        authorize.setMeUp(instrumentCredit);
        instrumentCreditService.subtract(instrumentCredit.getId(), authorize.getEventValue());
        authorize.setAuthorizationNumber(generateAuthorizationNumber(authorize));
        ServiceAuthorize authorized = repository.save(authorize);
        notifySupplyWhenRequired(authorize, authorized);
        return authorized;
    }

    private UserDetail getUserByEmail(String userEmail) {
        return userDetailService.getByEmail(userEmail);
    }

    private void notifySupplyWhenRequired(ServiceAuthorize serviceAuthorize, ServiceAuthorize authorized) {
        if(ServiceType.FUEL_ALLOWANCE.equals(serviceAuthorize.getServiceType())) {
            notifier.notify(Queues.PAMCARY_AUTHORIZATION_SUPPLY, authorized);
        }
    }

    private String generateAuthorizationNumber(ServiceAuthorize serviceAuthorize) {
        long count = repository.count();
        String authorizationNumber =
                String.valueOf(serviceAuthorize.getServiceType().ordinal()) + String.valueOf(count) +
                        String.valueOf(serviceAuthorize.getAuthorizationDateTime().getTime());
        return authorizationNumber.substring(0, Math.min(authorizationNumber.length(), 12));
    }

    private void checkContract(final ServiceAuthorize serviceAuthorize, final UserDetail currentUser) {
        serviceAuthorize.validateServiceType();
        serviceAuthorize.checkEstablishmentIdWhenRequired(currentUser);
        Establishment establishment = currentUser.myEstablishment().orElse(serviceAuthorize.getEstablishment());
        Contract contract = contractService.findById(serviceAuthorize.getContract().getId());
        contract.checkValidFor(serviceAuthorize.getContractor(), establishment);

    }

    private void validateEvent(ServiceAuthorize serviceAuthorize) {
        Event event = getAcceptableEvent(serviceAuthorize);
        serviceAuthorize.validateEvent(event);
        serviceAuthorize.setValueFee(event.getService().getFeeVal());
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
        if (contract.getContractor().physicalPerson() && !hasEqualsBirthDate(serviceAuthorize, contract)) {
            throw UnovationExceptions.unprocessableEntity().withErrors(INCORRECT_CONTRACTOR_BIRTH_DATE);
        }
        if (StringUtils.isEmpty(serviceAuthorize.instrumentPassword())) {
            throw UnovationExceptions.unprocessableEntity().withErrors(INSTRUMENT_PASSWORD_REQUIRED);
        }
    }

    private boolean hasEqualsBirthDate(ServiceAuthorize serviceAuthorize, Contract contract) {
        return DateTimeComparator.getDateOnlyInstance()
                .compare(serviceAuthorize.getContractor().getBirthDate(), contract.getContractor().getBirthDate()) == 0;
    }

    private void defineEstablishment(ServiceAuthorize serviceAuthorize, UserDetail currentUser) {
        if(currentUser.isEstablishmentType()){
            serviceAuthorize.setEstablishment(currentUser.getEstablishment());
            return;
        }
        serviceAuthorize
                .setEstablishment(establishmentService
                        .findById(serviceAuthorize.establishmentId()));
    }

    public ServiceAuthorize findById(String id) {
        Optional<ServiceAuthorize> serviceAuthorize =  repository.findById(id);
        return serviceAuthorize.orElseThrow(()->UnovationExceptions.notFound().withErrors(SERVICE_AUTHORIZE_NOT_FOUND));
    }

    public Stream<ServiceAuthorize> findByEstablishmentAndCreatedAt(String establishmentId, Date at){
        return repository
                .findByEstablishmentIdForProcessBatchClosing(establishmentId, at);
    }

    public List<ServiceAuthorize> findAll(){
        return repository.findAll();
    }

    public ServiceAuthorize save(ServiceAuthorize serviceAuthorize){
        return repository.save(serviceAuthorize);
    }

    public Page<ServiceAuthorize> findByFilter(ServiceAuthorizeFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter,new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public Page<ServiceAuthorize> findMyByFilter(String userEmail, ServiceAuthorizeFilter filter, UnovationPageRequest pageable) {
        return findByFilter(buildFilterBy(filter,getUserByEmail(userEmail)),pageable);
    }
    private ServiceAuthorizeFilter buildFilterBy(ServiceAuthorizeFilter filter, UserDetail currentUser) {
        if(currentUser.isEstablishmentType()) {
            filter.setEstablishment(currentUser.establishmentId());
        }
        if(currentUser.isContractorType()) {
            filter.setContractor(currentUser.contractorId());
        }
        if(currentUser.isAccreditedNetworkType()) {
            filter.setNetwork(currentUser.accreditedNetworkId());
        }
        if(currentUser.isHirerType()) {
            filter.setHirer(currentUser.hirerId());
        }
        return filter;
    }

}
