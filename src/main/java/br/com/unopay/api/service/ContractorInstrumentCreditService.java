package br.com.unopay.api.service;

import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.CreditPaymentAccount;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.repository.ContractorInstrumentCreditRepository;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_HIRER;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_PRODUCT;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_SERVICE;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_INSTRUMENT_NOT_VALID;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return  repository.findOne(id);
    }

    public ContractorInstrumentCredit insert(String paymentInstrumentId, ContractorInstrumentCredit instrumentCredit) {
        Contract contract = getReliableContract(paymentInstrumentId, instrumentCredit);
        validateCreditPaymentAccount(instrumentCredit, contract);
        instrumentCredit.validateMe(contract);
        setReferences(instrumentCredit);
        instrumentCredit.validateValue();
        instrumentCredit.setupMyCreate(contract);
        incrementInstallmentNumber(instrumentCredit);
        return repository.save(instrumentCredit);
    }

    private Contract getReliableContract(String paymentInstrumentId, ContractorInstrumentCredit instrumentCredit) {
        PaymentInstrument paymentInstrument = paymentInstrumentService.findById(paymentInstrumentId);
        Contract contract = contractService.findByContractorId(paymentInstrument.contractorId());
        verifyInstrumentBelongsToContractor(paymentInstrument.contractorId(), instrumentCredit);
        return contract;
    }

    private void incrementInstallmentNumber(ContractorInstrumentCredit instrumentCredit) {
        ContractorInstrumentCredit last = repository.findFirstByOrderByCreatedDateTimeDesc();
        instrumentCredit.incrementInstallmentNumber(last);
    }

    private void validateCreditPaymentAccount(ContractorInstrumentCredit instrumentCredit, Contract contract) {
        if(isCreditPaymentAccountFromAnotherHirer(contract.getHirerDocumentNumber(), instrumentCredit)){
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

    public void cancel(String id) {

    }
}
