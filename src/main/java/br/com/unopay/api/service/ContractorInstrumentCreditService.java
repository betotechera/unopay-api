package br.com.unopay.api.service;

import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.repository.ContractorInstrumentCreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public ContractorInstrumentCredit insert(ContractorInstrumentCredit instrumentCredit) {
        instrumentCredit.validateMe();
        validateReferences(instrumentCredit);
        instrumentCredit.setupMyCreate();
        return repository.save(instrumentCredit);
    }

    private void validateReferences(ContractorInstrumentCredit instrumentCredit) {
        contractService.findById(instrumentCredit.getContractId());
        paymentInstrumentService.findById(instrumentCredit.getPaymentInstrumentId());
        creditPaymentAccountService.findById(instrumentCredit.getCreditPaymentIdAccount());
    }
}
