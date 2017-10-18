package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.EstablishmentEvent;
import br.com.unopay.api.bacen.service.EstablishmentEventService;
import br.com.unopay.api.bacen.service.EstablishmentService;
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService;
import br.com.unopay.api.credit.service.InstrumentBalanceService;
import br.com.unopay.api.infra.UnopayEncryptor;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.credit.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.ServiceAuthorizeFilter;
import br.com.unopay.api.repository.ServiceAuthorizeRepository;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACTOR_BIRTH_DATE_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_NOT_QUALIFIED_FOR_THIS_CONTRACT;
import static br.com.unopay.api.uaa.exception.Errors.INCORRECT_CONTRACTOR_BIRTH_DATE;
import static br.com.unopay.api.uaa.exception.Errors.INSTRUMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT;
import static br.com.unopay.api.uaa.exception.Errors.INSTRUMENT_PASSWORD_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.SERVICE_AUTHORIZE_NOT_FOUND;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class ServiceAuthorizeService {

    public static final int NUMBER_SIZE = 12;
    private ServiceAuthorizeRepository repository;
    private ContractorInstrumentCreditService instrumentCreditService;
    private UserDetailService userDetailService;
    private EstablishmentService establishmentService;
    private ContractService contractService;
    private PaymentInstrumentService paymentInstrumentService;
    private UnopayEncryptor encryptor;
    private EstablishmentEventService establishmentEventService;
    private InstrumentBalanceService instrumentBalanceService;

    @Autowired
    public ServiceAuthorizeService(ServiceAuthorizeRepository repository,
                                   ContractorInstrumentCreditService instrumentCreditService,
                                   UserDetailService userDetailService,
                                   EstablishmentService establishmentService,
                                   ContractService contractService,
                                   PaymentInstrumentService paymentInstrumentService,
                                   UnopayEncryptor encryptor,
                                   EstablishmentEventService establishmentEventService,
                                   InstrumentBalanceService instrumentBalanceService) {
        this.repository = repository;
        this.instrumentCreditService = instrumentCreditService;
        this.userDetailService = userDetailService;
        this.establishmentService = establishmentService;
        this.contractService = contractService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.encryptor = encryptor;
        this.establishmentEventService = establishmentEventService;
        this.instrumentBalanceService = instrumentBalanceService;
    }

    private UserDetail getUserByEmail(String userEmail) {
        return userDetailService.getByEmail(userEmail);
    }

    @Transactional
    public ServiceAuthorize create(String userEmail, ServiceAuthorize authorize) {
        UserDetail currentUser = getUserByEmail(userEmail);
        Contract contract = getValidContract(authorize, currentUser);
        defineEstablishment(authorize, currentUser);
        PaymentInstrument paymentInstrument = getValidContractorPaymentInstrument(authorize, contract);
        authorize.setTypedPassword(encryptor.encrypt(authorize.paymentInstrumentPasswordAsByte()));
        authorize.setReferences(currentUser, paymentInstrument, contract);
        checkEventAndDefineValue(authorize);
        authorize.setMeUp(paymentInstrument);
        instrumentBalanceService.subtract(paymentInstrument.getId(), authorize.getEventValue());
        authorize.setAuthorizationNumber(generateAuthorizationNumber(authorize));
        return repository.save(authorize);
    }

    private String generateAuthorizationNumber(ServiceAuthorize serviceAuthorize) {
        long count = repository.count();
        String authorizationNumber =
                String.valueOf(serviceAuthorize.getServiceType().ordinal()) + String.valueOf(count) +
                        String.valueOf(serviceAuthorize.getAuthorizationDateTime().getTime());
        return authorizationNumber.substring(0, Math.min(authorizationNumber.length(), NUMBER_SIZE));
    }

    private Contract getValidContract(final ServiceAuthorize serviceAuthorize, final UserDetail currentUser) {
        serviceAuthorize.validateServiceType();
        serviceAuthorize.checkEstablishmentIdWhenRequired(currentUser);
        Contract contract = contractService.findById(serviceAuthorize.getContract().getId());
        contract.checkValidFor(serviceAuthorize.getContractor());
        return contract;

    }

    private void checkEventAndDefineValue(ServiceAuthorize serviceAuthorize) {
        EstablishmentEvent establishmentEvent =
                establishmentEventService.findByEstablishmentIdAndId(serviceAuthorize.establishmentId(),
                        serviceAuthorize.establishmentEventId());
        serviceAuthorize.setEventValues(establishmentEvent);
    }

    private PaymentInstrument getValidContractorPaymentInstrument(ServiceAuthorize serviceAuthorize, Contract contract){
        PaymentInstrument instrument = paymentInstrumentService.findById(serviceAuthorize.instrumentId());
        validateContractorInstrument(serviceAuthorize, instrument);
        updateValidPasswordWhenRequired(serviceAuthorize, instrument, contract);
        paymentInstrumentService.checkPassword(instrument.getId(), serviceAuthorize.instrumentPassword());
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

    public Page<ServiceAuthorize> findMyByFilter(String userEmail, ServiceAuthorizeFilter filter,
                                                 UnovationPageRequest pageable) {
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
