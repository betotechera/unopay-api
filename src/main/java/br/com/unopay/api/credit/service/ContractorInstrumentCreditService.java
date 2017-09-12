package br.com.unopay.api.credit.service;

import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.credit.model.InstrumentCreditSource;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.credit.model.ContractorCreditType;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.credit.model.CreditPaymentAccount;
import br.com.unopay.api.credit.model.CreditSituation;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.credit.model.filter.ContractorInstrumentCreditFilter;
import br.com.unopay.api.credit.repository.ContractorInstrumentCreditRepository;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.credit.model.CreditSituation.PROCESSING;
import static br.com.unopay.api.uaa.exception.Errors.CONTACT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACTOR_INSTRUMENT_CREDIT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_WITHOUT_CREDITS;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_HIRER;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_PRODUCT;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_SERVICE;
import static br.com.unopay.api.uaa.exception.Errors.FINAL_SUPPLY_CREDIT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_INSTRUMENT_NOT_VALID;

@Service
public class ContractorInstrumentCreditService {

    private ContractorInstrumentCreditRepository repository;
    private ContractService contractService;
    private PaymentInstrumentService paymentInstrumentService;
    private CreditPaymentAccountService creditPaymentAccountService;

    @Autowired
    public ContractorInstrumentCreditService(ContractorInstrumentCreditRepository repository,
                                             ContractService contractService,
                                             PaymentInstrumentService paymentInstrumentService,
                                             CreditPaymentAccountService creditPaymentAccountService) {
        this.repository = repository;
        this.contractService = contractService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.creditPaymentAccountService = creditPaymentAccountService;
    }

    public ContractorInstrumentCredit findById(String id) {
        Optional<ContractorInstrumentCredit> instrumentCredit = repository.findById(id);
        return instrumentCredit.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(CONTRACTOR_INSTRUMENT_CREDIT_NOT_FOUND));
    }

    public ContractorInstrumentCredit processOrder(Order order) {
        Contract contract = getContract(order);
        PaymentInstrument paymentInstrument = getContractorPaymentInstrument(order);
        CreditPaymentAccount creditPaymentAccount = getCreditPaymentAccount(contract);
        ContractorInstrumentCredit credit = createInstrumentCredit(contract,paymentInstrument,creditPaymentAccount);
        credit.setValue(order.getValue());
        return insert(paymentInstrument.getId(), credit);
    }


    private PaymentInstrument getContractorPaymentInstrument(Order order) {
        Optional<PaymentInstrument> instrument = paymentInstrumentService.getById(order.instrumentId());
        return instrument.orElseGet(() -> paymentInstrumentService
                .findDigitalWalletByContractorDocument(order.documentNumber()).orElse(null));
    }

    public ContractorInstrumentCredit insert(String paymentInstrumentId, ContractorInstrumentCredit instrumentCredit) {
        Contract contract = getReliableContract(paymentInstrumentId, instrumentCredit);
        instrumentCredit.validateMe(contract);
        setReferences(instrumentCredit);
        validateCreditPaymentAccount(instrumentCredit, contract);
        instrumentCredit.setupMyCreate(contract);
        instrumentCredit.validateValue();
        incrementInstallmentNumber(instrumentCredit);
        if(instrumentCredit.creditSourceIsHirer()) {
            subtractPaymentAccountBalance(instrumentCredit);
        }
        return repository.save(instrumentCredit);
    }

    @Transactional
    public void cancel(String instrumentId, String id) {
        ContractorInstrumentCredit instrumentCredit = findById(id);
        cancelInstrumentCredit(instrumentCredit);
    }

    @Transactional
    public void cancel(String contractId) {
        contractService.findById(contractId);
        Set<ContractorInstrumentCredit> contractorInstrumentCredits = repository.findByContractId(contractId);
        if(contractorInstrumentCredits.isEmpty()){
            throw UnovationExceptions.unprocessableEntity().withErrors(CONTRACT_WITHOUT_CREDITS);
        }
        contractorInstrumentCredits.forEach(this::cancelInstrumentCredit);
    }

    public void subtract(String id, BigDecimal value) {
        ContractorInstrumentCredit instrumentCredit = findById(id);
        instrumentCredit.subtract(value);
        if(instrumentCredit.isDepleted()){
            instrumentCredit.setSituation(PROCESSING);
        }
        else {
            instrumentCredit.subtractValue(value);
            createProcessingCredit(value, instrumentCredit);
        }
        repository.save(instrumentCredit);
    }

    private Contract getReliableContract(String paymentInstrumentId, ContractorInstrumentCredit instrumentCredit) {
        PaymentInstrument paymentInstrument = paymentInstrumentService.findById(paymentInstrumentId);
        Contract contract = contractService
                              .getByIdAndContractorId(instrumentCredit.contractId(), paymentInstrument.contractorId());
        verifyInstrumentBelongsToContractor(paymentInstrument.contractorId(), instrumentCredit);
        return contract;
    }

    private void incrementInstallmentNumber(ContractorInstrumentCredit instrumentCredit) {
        ContractorInstrumentCredit last = repository
                .findFirstByServiceTypeAndContractIdOrderByCreatedDateTimeDesc(instrumentCredit.getServiceType(),
                                                                                        instrumentCredit.contractId());
        instrumentCredit.incrementInstallmentNumber(last);
    }

    private void validateCreditPaymentAccount(ContractorInstrumentCredit instrumentCredit, Contract contract) {
        if(isCreditPaymentAccountFromAnotherHirer(contract.hirerDocumentNumber(), instrumentCredit)){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_HIRER);
        }
        if(!contract.isProductCodeEquals(instrumentCredit.getCreditPaymentAccountProductCode())){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_PRODUCT);
        }
        if(!contract.containsService(instrumentCredit.getCreditPaymentAccountServiceType())){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_SERVICE);
        }
    }

    private boolean isCreditPaymentAccountFromAnotherHirer(String hirerDocument,
                                                           ContractorInstrumentCredit instrumentCredit) {
        List<CreditPaymentAccount> hirerCreditPaymentAccounts = creditPaymentAccountService
                                                                                    .findByHirerDocument(hirerDocument);
        return !instrumentCredit.myCreditPaymentAccountIn(hirerCreditPaymentAccounts);
    }

    private void verifyInstrumentBelongsToContractor(String contractorId, ContractorInstrumentCredit instrumentCredit) {
        List<PaymentInstrument> contractorPaymentInstruments= paymentInstrumentService.findByContractorId(contractorId);
        if(!instrumentCredit.myPaymentInstrumentIn(contractorPaymentInstruments)) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_INSTRUMENT_NOT_VALID);
        }
    }

    private void setReferences(ContractorInstrumentCredit instrumentCredit) {
        PaymentInstrument instrument = paymentInstrumentService.findById(instrumentCredit.getPaymentInstrumentId());
        CreditPaymentAccount account=creditPaymentAccountService.findById(instrumentCredit.getCreditPaymentAccountId());
        instrumentCredit.setPaymentInstrument(instrument);
        instrumentCredit.setCreditPaymentAccount(account);
    }

    private void createProcessingCredit(BigDecimal value, ContractorInstrumentCredit instrumentCredit) {
        repository.save(instrumentCredit.createProcessingCredit(value));
    }

    private void cancelInstrumentCredit(ContractorInstrumentCredit instrumentCredit) {
        instrumentCredit.cancel();
        giveBackPaymentAccountBalance(instrumentCredit);
        repository.save(instrumentCredit);
    }


    public Page<ContractorInstrumentCredit> findByFilter(ContractorInstrumentCreditFilter filter,
                                                         UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private void subtractPaymentAccountBalance(ContractorInstrumentCredit instrumentCredit) {
        creditPaymentAccountService
                .subtract(instrumentCredit.getCreditPaymentAccountId(), instrumentCredit.getAvailableBalance());
    }

    private void giveBackPaymentAccountBalance(ContractorInstrumentCredit instrumentCredit) {
        creditPaymentAccountService
                .giveBack(instrumentCredit.getCreditPaymentAccountId(), instrumentCredit.getAvailableBalance());
    }

    public ContractorInstrumentCredit findByContractIdAndServiceType(String contractId, ServiceType serviceType){
        Optional<ContractorInstrumentCredit> credit =  repository
                .findFirstByContractIdAndServiceTypeAndSituationAndCreditType(contractId, serviceType,
                                                                            CreditSituation.AVAILABLE,
                                                                                ContractorCreditType.FINAL_PAYMENT);
        return credit.orElseThrow(()->UnovationExceptions.notFound().withErrors(FINAL_SUPPLY_CREDIT_NOT_FOUND));
    }

    public Page<ContractorInstrumentCredit> findContractorCredits(String contractId, String contractorDocument,
                                                                                    UnovationPageRequest pageable) {
        return repository.findByContractIdAndContractContractorPersonDocumentNumber(contractId, contractorDocument,
                new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private ContractorInstrumentCredit createInstrumentCredit(Contract contract, PaymentInstrument paymentInstrument,
                                                              CreditPaymentAccount creditPaymentAccount) {
        ContractorInstrumentCredit instrumentCredit = new ContractorInstrumentCredit();
        instrumentCredit.setPaymentInstrument(paymentInstrument);
        instrumentCredit.setCreditPaymentAccount(creditPaymentAccount);
        instrumentCredit.setContract(contract);
        instrumentCredit.setCreditType(ContractorCreditType.FINAL_PAYMENT);
        instrumentCredit.setCreditSource(InstrumentCreditSource.CLIENT);
        instrumentCredit.setExpirationDateTime(new DateTime().plusYears(5).toDate());
        return instrumentCredit;
    }

    private Contract getContract(Order order) {
        Optional<Contract> existing = contractService.findByContractorAndProduct(order.documentNumber(),
                                                                                 order.productId());
        return existing.orElseThrow(()-> UnovationExceptions.notFound().withErrors(CONTRACT_NOT_FOUND));
    }

    private CreditPaymentAccount getCreditPaymentAccount(Contract contract) {
        List<CreditPaymentAccount> creditPaymentAccounts = creditPaymentAccountService
                .findByHirerDocument(contract.hirerDocumentNumber());
        return creditPaymentAccounts.stream().findFirst().orElse(null);
    }

}
