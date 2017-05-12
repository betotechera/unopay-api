package br.com.unopay.api.service;

import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.CreditPaymentAccount;
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

    public ContractorInstrumentCredit insert(String contractorId, ContractorInstrumentCredit instrumentCredit) {
        Contract contract = contractService.findByContractorId(contractorId);
        verifyInstrumentBelongsToContractor(contractorId, instrumentCredit);
        validateCreditPaymentAccount(instrumentCredit, contract);
        instrumentCredit.setupMyCreate(contract);
        instrumentCredit.validateMe(contract);
        return repository.save(instrumentCredit);
    }

    private void validateCreditPaymentAccount(ContractorInstrumentCredit instrumentCredit, Contract contract) {
        List<CreditPaymentAccount> creditPaymentAccounts = creditPaymentAccountService
                                                                .findByHirerDocument(contract.getHirerDocumentNumber());
        if(creditPaymentAccounts.stream().noneMatch(c -> c.getId() == instrumentCredit.getCreditPaymentIdAccount())){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_HIRER);
        }
        if(instrumentCredit.getCreditPaymentAccountProductCode() != contract.getProductCode()){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_PRODUCT);
        }
        if(!contract.containsService(instrumentCredit.getCreditPaymentAccountServiceType())){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_SERVICE);
        }
    }

    private void verifyInstrumentBelongsToContractor(String contractorId, ContractorInstrumentCredit instrumentCredit) {
        boolean instrumentBelongsToContractor = paymentInstrumentService.findByContractorId(contractorId).stream()
                .anyMatch(p-> p.getId() == instrumentCredit.getPaymentInstrumentId());
        if(!instrumentBelongsToContractor) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_INSTRUMENT_NOT_VALID);
        }
    }

}
