package br.com.unopay.api.credit.service;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.credit.model.ContractorInstrumentCredit;
import br.com.unopay.api.credit.model.CreditPaymentAccount;
import br.com.unopay.api.credit.model.filter.ContractorInstrumentCreditFilter;
import br.com.unopay.api.credit.repository.ContractorInstrumentCreditRepository;
import br.com.unopay.api.market.model.BonusBilling;
import br.com.unopay.api.market.model.ContractorBonus;
import br.com.unopay.api.market.service.BonusBillingService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.api.util.Time;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.credit.model.InstrumentCreditSource.CLIENT;
import static br.com.unopay.api.credit.model.InstrumentCreditSource.ESTABLISHMENT;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACTOR_INSTRUMENT_CREDIT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_WITHOUT_CREDITS;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_HIRER;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_PRODUCT;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_SERVICE;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_INSTRUMENT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_INSTRUMENT_NOT_VALID;

@Service
public class ContractorInstrumentCreditService {

    private ContractorInstrumentCreditRepository repository;
    private ContractService contractService;
    private PaymentInstrumentService paymentInstrumentService;
    private CreditPaymentAccountService creditPaymentAccountService;
    private UserDetailService userDetailService;
    private InstrumentBalanceService instrumentBalanceService;
    private BonusBillingService bonusBillingService;

    @Autowired
    public ContractorInstrumentCreditService(ContractorInstrumentCreditRepository repository,
                                             ContractService contractService,
                                             PaymentInstrumentService paymentInstrumentService,
                                             CreditPaymentAccountService creditPaymentAccountService,
                                             UserDetailService userDetailService,
                                             InstrumentBalanceService instrumentBalanceService,
                                             BonusBillingService bonusBillingService) {
        this.repository = repository;
        this.contractService = contractService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.creditPaymentAccountService = creditPaymentAccountService;
        this.userDetailService = userDetailService;
        this.instrumentBalanceService = instrumentBalanceService;
        this.bonusBillingService = bonusBillingService;
    }

    public ContractorInstrumentCredit findById(String id) {
        Optional<ContractorInstrumentCredit> instrumentCredit = repository.findById(id);
        return instrumentCredit.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(CONTRACTOR_INSTRUMENT_CREDIT_NOT_FOUND));
    }

    public ContractorInstrumentCredit findByIdAndHirer(String id, Hirer hirer) {
        Optional<ContractorInstrumentCredit> instrumentCredit = repository.findByIdAndContractHirerId(id,hirer.getId());
        return instrumentCredit.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(CONTRACTOR_INSTRUMENT_CREDIT_NOT_FOUND));
    }

    @Transactional
    public ContractorInstrumentCredit processOrder(Order order) {
        Contract contract = getContract(order.getDocumentNumber(), order.getProductCode());
        PaymentInstrument paymentInstrument = getContractorPaymentInstrument(order);
        CreditPaymentAccount creditPaymentAccount = getCreditPaymentAccount(contract, new CreditPaymentAccount(order));
        ContractorInstrumentCredit credit = createInstrumentCredit(contract, paymentInstrument, creditPaymentAccount);
        credit.setCreditSource(CLIENT);
        credit.setValue(order.getValue());
        return insert(paymentInstrument.getId(), credit);
    }

    public void processBonusBilling(BonusBilling bonusBilling) {
        Set<ContractorBonus> contractorBonuses = bonusBillingService.getContractorBonusesByBonusBillingId(bonusBilling.getId());
        contractorBonuses.forEach(bonus -> processBonus(bonusBilling.getIssuer(), bonus));
    }

    private void processBonus(Issuer issuer, ContractorBonus contractorBonus) {
        String contractorDocument = contractorBonus.getContractor().getDocumentNumber();
        Contract contract = getContract(contractorDocument, contractorBonus.productCode());
        PaymentInstrument paymentInstrument = findContractorDigitalWallet(contractorDocument);
        CreditPaymentAccount paymentAccount = new CreditPaymentAccount(contract.hirerDocumentNumber(), contractorBonus, issuer);
        CreditPaymentAccount creditPaymentAccount = getCreditPaymentAccount(contract, paymentAccount);
        ContractorInstrumentCredit credit = createCreditForBonus(contract, paymentInstrument, creditPaymentAccount);
        credit.setValue(contractorBonus.getEarnedBonus());
        insert(paymentInstrument.getId(), credit);
    }

    private ContractorInstrumentCredit createCreditForBonus(Contract contract, PaymentInstrument paymentInstrument,
                                                            CreditPaymentAccount creditPaymentAccount) {
        ContractorInstrumentCredit credit = createInstrumentCredit(contract, paymentInstrument, creditPaymentAccount);
        credit.setCreditSource(ESTABLISHMENT);
        Product product = contract.getProduct();
        if(product.hasBonusExpiration()) {
            credit.setExpirationDateTime(new DateTime().plusMonths(product.getMonthsToExpireBonus()).toDate());
        }
        return credit;
    }

    private PaymentInstrument findContractorDigitalWallet(String contractorDocumentNumber) {
        return paymentInstrumentService
                .findDigitalWalletByContractorDocument(contractorDocumentNumber).orElseThrow(() ->
                        UnovationExceptions.notFound()
                                .withErrors(PAYMENT_INSTRUMENT_NOT_FOUND.withOnlyArgument("Digital Wallet")));
    }

    private CreditPaymentAccount getCreditPaymentAccount(Contract contract, CreditPaymentAccount newPaymentAccount) {
        List<CreditPaymentAccount> creditPaymentAccounts = creditPaymentAccountService
                .findByHirerDocument(contract.hirerDocumentNumber());
        Optional<CreditPaymentAccount> current = creditPaymentAccounts.stream().filter(account ->
                contract.isProductCodeEquals(account.getProductCode()))
                .findFirst();
        return current.orElseGet(()-> creditPaymentAccountService.save(newPaymentAccount));

    }

    private PaymentInstrument getContractorPaymentInstrument(Order order) {
        Optional<PaymentInstrument> instrument = paymentInstrumentService.getById(order.instrumentId());
        return instrument.orElseGet(() -> paymentInstrumentService
                .findDigitalWalletByContractorDocument(order.getDocumentNumber()).orElse(null));
    }



    @Transactional
    public ContractorInstrumentCredit insert(String paymentInstrumentId, ContractorInstrumentCredit instrumentCredit) {
        Contract contract = getValidContractorContract(paymentInstrumentId, instrumentCredit);
        instrumentCredit.validateMe(contract);
        setReferences(instrumentCredit);
        validateCreditPaymentAccount(instrumentCredit, contract);
        instrumentCredit.setupMyCreate(contract);
        instrumentCredit.validateValue();
        if(instrumentCredit.creditSourceIsHirer()) {
            subtractPaymentAccountBalance(instrumentCredit);
        }
        instrumentBalanceService.add(paymentInstrumentId, instrumentCredit.getValue());
        return repository.save(instrumentCredit);
    }

    @Transactional
    public void cancel(String instrumentId, String id) {
        ContractorInstrumentCredit instrumentCredit = findById(id);
        cancelInstrumentCredit(instrumentCredit);
    }

    @Transactional
    public void cancelForHirer(String id, Hirer hirer) {
        ContractorInstrumentCredit instrumentCredit = findByIdAndHirer(id, hirer);
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


    private Contract getValidContractorContract(String paymentInstrumentId, ContractorInstrumentCredit instrumentCredit) {
        PaymentInstrument paymentInstrument = paymentInstrumentService.findById(paymentInstrumentId);
        Contract contract = contractService
                              .getByIdAndContractorId(instrumentCredit.contractId(), paymentInstrument.getContractor());
        verifyInstrumentBelongsToContractor(paymentInstrument.contractorId(), instrumentCredit);
        return contract;
    }

    private void validateCreditPaymentAccount(ContractorInstrumentCredit instrumentCredit, Contract contract) {
        if(isCreditPaymentAccountFromAnotherHirer(contract.hirerDocumentNumber(), instrumentCredit)){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_HIRER);
        }
        if(instrumentCredit.getCreditPaymentAccountProductCode()!= null &&
                !contract.isProductCodeEquals(instrumentCredit.getCreditPaymentAccountProductCode())){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_PRODUCT);
        }
        if(instrumentCredit.getCreditPaymentAccountServiceType()!= null &&
                !contract.containsService(instrumentCredit.getCreditPaymentAccountServiceType())){
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

    private void cancelInstrumentCredit(ContractorInstrumentCredit instrumentCredit) {
        instrumentCredit.cancel();
        giveBackPaymentAccountBalance(instrumentCredit);
        instrumentBalanceService.subtract(instrumentCredit.getPaymentInstrumentId(),
                                                                    instrumentCredit.getValue());
        repository.save(instrumentCredit);
    }

    public ContractorInstrumentCredit findByContractorId(String contractorId){
        Optional<ContractorInstrumentCredit> credit = repository.findByPaymentInstrumentContractorId(contractorId);
        return credit.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(CONTRACTOR_INSTRUMENT_CREDIT_NOT_FOUND));
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

    public Page<ContractorInstrumentCredit> findLogedContractorCredits(String contractId, String userEmail,
                                                                  UnovationPageRequest pageable) {
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        return findContractorCredits(contractId, currentUser.contractorDocument(),pageable);
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
        instrumentCredit.setExpirationDateTime(Time.createDateTime().plusYears(5).toDate());
        return  instrumentCredit;
    }

    private Contract getContract(String contractorDocument, String productCode) {
        Optional<Contract> existing = contractService.findByContractorAndProductCode(contractorDocument,
                productCode);
        return existing.orElseThrow(()-> UnovationExceptions.notFound().withErrors(CONTRACT_NOT_FOUND));
    }
}
