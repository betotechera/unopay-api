package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.EstablishmentEvent;
import br.com.unopay.api.bacen.service.EstablishmentEventService;
import br.com.unopay.api.bacen.service.EstablishmentService;
import br.com.unopay.api.credit.service.InstrumentBalanceService;
import br.com.unopay.api.infra.NumberGenerator;
import br.com.unopay.api.infra.UnopayEncryptor;
import br.com.unopay.api.market.service.HirerNegotiationService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.ServiceAuthorizeFilter;
import br.com.unopay.api.repository.ServiceAuthorizeRepository;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.CONTRACTOR_BIRTH_DATE_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.EVENTS_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.EVENT_QUANTITY_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.INCORRECT_CONTRACTOR_BIRTH_DATE;
import static br.com.unopay.api.uaa.exception.Errors.INSTRUMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT;
import static br.com.unopay.api.uaa.exception.Errors.INSTRUMENT_PASSWORD_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.SERVICE_AUTHORIZE_NOT_FOUND;

@Slf4j
@Service
public class ServiceAuthorizeService {

    private ServiceAuthorizeRepository repository;
    private EstablishmentService establishmentService;
    private ContractService contractService;
    private PaymentInstrumentService paymentInstrumentService;
    private UnopayEncryptor encryptor;
    private EstablishmentEventService establishmentEventService;
    private InstrumentBalanceService instrumentBalanceService;
    private HirerNegotiationService hirerNegotiationService;
    private NumberGenerator numberGenerator;

    @Autowired
    public ServiceAuthorizeService(ServiceAuthorizeRepository repository,
                                   EstablishmentService establishmentService,
                                   ContractService contractService,
                                   PaymentInstrumentService paymentInstrumentService,
                                   UnopayEncryptor encryptor,
                                   EstablishmentEventService establishmentEventService,
                                   InstrumentBalanceService instrumentBalanceService,
                                   HirerNegotiationService hirerNegotiationService) {
        this.repository = repository;
        this.establishmentService = establishmentService;
        this.contractService = contractService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.encryptor = encryptor;
        this.establishmentEventService = establishmentEventService;
        this.instrumentBalanceService = instrumentBalanceService;
        this.numberGenerator = new NumberGenerator(repository);
        this.hirerNegotiationService = hirerNegotiationService;
    }

    @Transactional
    public ServiceAuthorize create(UserDetail currentUser, ServiceAuthorize authorize) {
        Contract contract = getValidContract(authorize, currentUser);
        defineEstablishment(authorize, currentUser);
        PaymentInstrument paymentInstrument = getValidContractorPaymentInstrument(authorize, contract);
        defineTypedPasswordWhenRequired(authorize);
        authorize.setReferences(currentUser, paymentInstrument, contract);
        checkEventAndDefineValue(authorize);
        authorize.setMeUp(paymentInstrument);
        instrumentBalanceService.subtract(paymentInstrument.getId(), authorize.getPaid());
        authorize.setAuthorizationNumber(numberGenerator.createNumber());
        return repository.save(authorize);
    }

    @Transactional
    public void cancelForEstablishment(String id, Establishment establishment) {
        ServiceAuthorize current = findByIdForEstablishment(id, establishment);
        cancel(current);
    }

    @Transactional
    public void cancel(String id) {
        ServiceAuthorize current = findById(id);
        cancel(current);
    }

    private void cancel(ServiceAuthorize current) {
        current.validateCancellation();
        current.setupCancellation();
        instrumentBalanceService.giveBack(current.instrumentId(),current.getPaid());
        repository.save(current);
    }

    private void defineTypedPasswordWhenRequired(ServiceAuthorize authorize) {
        if(!authorize.hasExceptionalCircumstance()) {
            authorize.setTypedPassword(encryptor.encrypt(authorize.paymentInstrumentPasswordAsByte()));
        }
    }

    private Contract getValidContract(final ServiceAuthorize serviceAuthorize, final UserDetail currentUser) {
        serviceAuthorize.checkEstablishmentIdWhenRequired(currentUser);
        Contract contract = contractService.findById(serviceAuthorize.getContract().getId());
        contract.checkValidFor(serviceAuthorize.getContractor());
        hirerNegotiationService.findActiveByHirerAndProduct(contract.hirerId(), contract.productId());
        return contract;

    }

    private void checkEventAndDefineValue(ServiceAuthorize serviceAuthorize) {
        checkEvents(serviceAuthorize);
        serviceAuthorize.resetTotal();
        serviceAuthorize.getAuthorizeEvents().forEach(serviceAuthorizeEvent -> {
            EstablishmentEvent establishmentEvent =
                    establishmentEventService.findByEstablishmentIdAndId(serviceAuthorize.establishmentId(),
                            serviceAuthorizeEvent.establishmentEventId());
            serviceAuthorizeEvent.defineValidEventValues(establishmentEvent);
            serviceAuthorize.addEventValueToTotal(serviceAuthorizeEvent.eventValueByQuantity());
        });
        serviceAuthorize.checkValueWhenRequired();
        serviceAuthorize.definePaidValue();
    }

    private void checkEvents(ServiceAuthorize serviceAuthorize) {
        if(!serviceAuthorize.hasEvents()){
            throw UnovationExceptions.unprocessableEntity().withErrors(EVENTS_REQUIRED);
        }
        if(serviceAuthorize.withoutEventQuantityWheRequired()){
            throw UnovationExceptions.unprocessableEntity().withErrors(EVENT_QUANTITY_REQUIRED);
        }
    }

    private PaymentInstrument getValidContractorPaymentInstrument(ServiceAuthorize serviceAuthorize, Contract contract){
        PaymentInstrument instrument = paymentInstrumentService.findById(serviceAuthorize.instrumentId());
        validateContractorInstrument(serviceAuthorize, instrument);
        updateValidPasswordWhenRequired(serviceAuthorize, instrument, contract);
        if(!serviceAuthorize.hasExceptionalCircumstance()) {
            paymentInstrumentService.checkPassword(instrument.getId(), serviceAuthorize.instrumentPassword());
        }
        return instrument;
    }

    private void validateContractorInstrument(ServiceAuthorize serviceAuthorize, PaymentInstrument instrumentCredit){
        if (!instrumentCredit.productIs(serviceAuthorize.getContract().getProduct().getId())) {
            throw UnovationExceptions.unprocessableEntity().withErrors(INSTRUMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT);
        }
    }

    private void updateValidPasswordWhenRequired(ServiceAuthorize serviceAuthorize,
                                                 PaymentInstrument paymentInstrument, Contract contract) {
        if (!paymentInstrument.hasPassword()) {
            validateRequiredPasswordInformation(serviceAuthorize, contract);
            paymentInstrumentService.changePassword(paymentInstrument.getId(), serviceAuthorize.instrumentPassword());
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
        Establishment establishment = currentUser.myEstablishment()
                                            .orElse(establishmentService.findById(serviceAuthorize.establishmentId()));
        serviceAuthorize.setEstablishment(establishment);
    }

    public ServiceAuthorize findByIdForEstablishment(String id, Establishment establishment) {
        Optional<ServiceAuthorize> serviceAuthorize =  repository.findByIdAndEstablishmentId(id, establishment.getId());
        return serviceAuthorize.orElseThrow(()->UnovationExceptions.notFound().withErrors(SERVICE_AUTHORIZE_NOT_FOUND));
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

    public Page<ServiceAuthorize> findMyByFilter(UserDetail currentUser, ServiceAuthorizeFilter filter,
                                                 UnovationPageRequest pageable) {
        return findByFilter(buildFilterBy(filter,currentUser),pageable);
    }
    private ServiceAuthorizeFilter buildFilterBy(ServiceAuthorizeFilter filter, UserDetail currentUser) {
        filter.setEstablishment(currentUser.myEstablishmentId());
        filter.setContractor(currentUser.myContractorId());
        filter.setNetwork(currentUser.myNetworkId());
        filter.setHirer(currentUser.myHirerId());
        return filter;
    }

}
