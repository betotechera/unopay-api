package br.com.unopay.api.credit.service;

import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.credit.model.ContractorCreditRecurrence;
import br.com.unopay.api.credit.repository.ContractorCreditRecurrenceRepository;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.HIRER_BELONG_TO_OTHER_CONTRACT;
import static br.com.unopay.api.uaa.exception.Errors.INVALID_VALUE;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_INSTRUMENT_NOT_FOUND;

@Service
public class ContractorCreditRecurrenceService {

    private ContractorCreditRecurrenceRepository repository;
    private HirerService hirerService;
    private ContractService contractService;
    private PaymentInstrumentService paymentInstrumentService;

    @Autowired
    public ContractorCreditRecurrenceService(ContractorCreditRecurrenceRepository repository,
                                             HirerService hirerService,
                                             ContractService contractService,
                                             PaymentInstrumentService paymentInstrumentService) {
        this.repository = repository;
        this.hirerService = hirerService;
        this.contractService = contractService;
        this.paymentInstrumentService = paymentInstrumentService;
    }

    public ContractorCreditRecurrence save(ContractorCreditRecurrence creditRecurrence) {
        return repository.save(creditRecurrence);
    }

    public ContractorCreditRecurrence findById(String id) {
        return repository.findOne(id);
    }

    public ContractorCreditRecurrence create(ContractorCreditRecurrence creditRecurrence) {
        creditRecurrence.validateMe();
        creditRecurrence.setCreatedDateTime(new Date());
        defineValidReferences(creditRecurrence);
        defineDefaultInstrumentWheRequired(creditRecurrence);
        return save(creditRecurrence);
    }



    private void defineDefaultInstrumentWheRequired(ContractorCreditRecurrence creditRecurrence) {
        if(!creditRecurrence.withPaymentInstrument()) {
            Optional<PaymentInstrument> digitalWallet = paymentInstrumentService
                    .findDigitalWalletByContractorDocument(creditRecurrence.contractorDocument());
            creditRecurrence.setPaymentInstrument(digitalWallet.orElseThrow(() ->
                    UnovationExceptions.notFound()
                            .withErrors(PAYMENT_INSTRUMENT_NOT_FOUND.withOnlyArgument("Digital Wallet"))));
        }
    }

    private void defineValidReferences(ContractorCreditRecurrence creditRecurrence) {
        creditRecurrence.setHirer(hirerService.getById(creditRecurrence.hirerId()));
        creditRecurrence.setContract(contractService.findById(creditRecurrence.contractId()));
        if(creditRecurrence.withPaymentInstrument()) {
            creditRecurrence.setPaymentInstrument(paymentInstrumentService.findById(creditRecurrence.instrumentId()));
        }
    }
}
