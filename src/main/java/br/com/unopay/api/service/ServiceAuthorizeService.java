package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.market.model.AuthorizedMember;
import br.com.unopay.api.network.model.Establishment;
import br.com.unopay.api.network.model.EstablishmentEvent;
import br.com.unopay.api.network.service.EstablishmentEventService;
import br.com.unopay.api.network.service.EstablishmentService;
import br.com.unopay.api.credit.service.InstrumentBalanceService;
import br.com.unopay.api.infra.NumberGenerator;
import br.com.unopay.api.infra.UnopayEncryptor;
import br.com.unopay.api.market.service.AuthorizedMemberService;
import br.com.unopay.api.market.service.ContractorBonusService;
import br.com.unopay.api.market.service.HirerNegotiationService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.ServiceAuthorizeFilter;
import br.com.unopay.api.repository.ServiceAuthorizeRepository;
import br.com.unopay.api.scheduling.model.Scheduling;
import br.com.unopay.api.scheduling.service.SchedulingService;
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
    private AuthorizedMemberService authorizedMemberService;
    private ContractorBonusService contractorBonusService;
    private SchedulingService schedulingService;

    @Autowired
    public ServiceAuthorizeService(ServiceAuthorizeRepository repository,
                                   EstablishmentService establishmentService,
                                   ContractService contractService,
                                   PaymentInstrumentService paymentInstrumentService,
                                   UnopayEncryptor encryptor,
                                   EstablishmentEventService establishmentEventService,
                                   InstrumentBalanceService instrumentBalanceService,
                                   HirerNegotiationService hirerNegotiationService,
                                   AuthorizedMemberService authorizedMemberService,
                                   ContractorBonusService contractorBonusService,
                                   SchedulingService schedulingService) {
        this.repository = repository;
        this.establishmentService = establishmentService;
        this.contractService = contractService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.encryptor = encryptor;
        this.establishmentEventService = establishmentEventService;
        this.instrumentBalanceService = instrumentBalanceService;
        this.numberGenerator = new NumberGenerator(repository);
        this.hirerNegotiationService = hirerNegotiationService;
        this.authorizedMemberService = authorizedMemberService;
        this.contractorBonusService = contractorBonusService;
        this.schedulingService = schedulingService;
    }

    @Transactional
    public ServiceAuthorize create(UserDetail currentUser, ServiceAuthorize authorize) {
        if (authorize.hasSchedulingToken()){
            authorize = loadFromScheduling(authorize);
        }
        authorize.validateMe();
        Contract contract = getValidContract(authorize, currentUser);
        defineEstablishment(authorize, currentUser);
        PaymentInstrument paymentInstrument = getValidContractorPaymentInstrument(authorize, contract);
        defineTypedPasswordWhenRequired(authorize);
        authorize.setReferences(currentUser, paymentInstrument, contract);
        validateAuthorizedMember(authorize);
        checkEventAndDefineValue(authorize);
        authorize.setMeUp(paymentInstrument);
        instrumentBalanceService.subtract(paymentInstrument.getId(), authorize.getPaid());
        authorize.setAuthorizationNumber(numberGenerator.createNumber());
        createBonusIfProductBonus(authorize);

        return repository.save(authorize);
    }

    private ServiceAuthorize loadFromScheduling(ServiceAuthorize current){
        ServiceAuthorize authorization = new ServiceAuthorize();
        authorization.updateMe(current);
        Scheduling scheduling = schedulingService.findByToken(current.getSchedulingToken());
        fillAuthorizationUsingScheduling(authorization, scheduling, current.getPaymentInstrument().getPassword());

        return authorization;
    }

    public void fillAuthorizationUsingScheduling(ServiceAuthorize authorization, Scheduling scheduling, String instrumentPassword){
        if(!authorization.hasScheduling()) {
            authorization.setScheduling(scheduling);
        }

        if(!authorization.hasContract()){
            Contract contract = new Contract();
            contract.setId(scheduling.getContract().getId());
            authorization.setContract(contract);
        }

        if(!authorization.hasContractor()){
            Contractor contractor = new Contractor();
            contractor.setId(scheduling.getContractor().getId());
            authorization.setContractor(contractor);
        }

        if(!authorization.hasPaymentInstrument()){
            PaymentInstrument paymentInstrument = new PaymentInstrument();
            paymentInstrument.setId(scheduling.getPaymentInstrument().getId());
            paymentInstrument.setPassword(instrumentPassword);
            authorization.setPaymentInstrument(paymentInstrument);
        }

        if(!authorization.withAuthorizedMember() && scheduling.hasAuthorizedMember()){
            AuthorizedMember authorizedMember = new AuthorizedMember();
            authorizedMember.setId(scheduling.getAuthorizedMember().getId());
            authorization.setAuthorizedMember(authorizedMember);
        }
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

    @Transactional
    public void rate(String id,Integer rating) {
        ServiceAuthorize current = findById(id);
        current.canBeRated();
        current.setRating(rating);
        repository.save(current);
    }

    private void validateAuthorizedMember(ServiceAuthorize authorize) {
        if(authorize.withAuthorizedMember()) {
            String id = authorize.authorizedMemberId();
            Contractor contractor = authorize.getContractor();
            authorize.setAuthorizedMember(authorizedMemberService.findByIdForContractor(id, contractor));
        }
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
        if(!contract.withIssuerAsHirer()) {
            hirerNegotiationService.findActiveByHirerAndProduct(contract.hirerId(), contract.productId());
        }
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
        validateContractorInstrument(contract, instrument);
        updateValidPasswordWhenRequired(serviceAuthorize, instrument, contract);
        if(!serviceAuthorize.hasExceptionalCircumstance()) {
            paymentInstrumentService.checkPassword(instrument.getId(), serviceAuthorize.instrumentPassword());
        }
        return instrument;
    }

    private void validateContractorInstrument(Contract contract, PaymentInstrument instrumentCredit){
        if (!instrumentCredit.productIs(contract.getProduct().getId())) {
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

    private void createBonusIfProductBonus(ServiceAuthorize authorize) {
        if (authorize.productHasBonus()) {
            contractorBonusService.createForServiceAuthorize(authorize);
        }
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
