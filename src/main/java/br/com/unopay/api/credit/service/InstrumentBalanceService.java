package br.com.unopay.api.credit.service;

import br.com.unopay.api.credit.model.InstrumentBalance;
import br.com.unopay.api.credit.repository.InstrumentBalanceRepository;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class InstrumentBalanceService {

    private PaymentInstrumentService paymentInstrumentService;
    private InstrumentBalanceRepository repository;

    @Autowired
    public InstrumentBalanceService(PaymentInstrumentService paymentInstrumentService,
                                    InstrumentBalanceRepository repository) {
        this.paymentInstrumentService = paymentInstrumentService;
        this.repository = repository;
    }

    public InstrumentBalance save(InstrumentBalance balance) {
        return repository.save(balance);
    }

    public InstrumentBalance findBydId(String id) {
        return repository.findOne(id);
    }

    public void add(String instrumentId, BigDecimal value) {
        PaymentInstrument instrument = paymentInstrumentService.findById(instrumentId);
        Optional<InstrumentBalance> current = repository.findByPaymentInstrumentId(instrumentId);
        current.ifPresent(balance -> {
            balance.add(value);
            save(balance);
        });
        if(!current.isPresent()){
            save(new InstrumentBalance(instrument, value));
        }
    }

    public InstrumentBalance findByInstrumentId(String instrumentId) {
        Optional<InstrumentBalance> instrumentBalance = repository.findByPaymentInstrumentId(instrumentId);
        return instrumentBalance.orElseThrow(() -> UnovationExceptions.notFound()
                                                            .withErrors(Errors.INSTRUMENT_BALANCE_NOT_FOUND));
    }

    public void subtract(String instrumentId, BigDecimal value) {
        InstrumentBalance current = findByInstrumentId(instrumentId);
        current.subtract(value);
        save(current);
    }
}
