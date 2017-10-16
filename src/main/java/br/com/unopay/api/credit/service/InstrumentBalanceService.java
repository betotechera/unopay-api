package br.com.unopay.api.credit.service;

import br.com.unopay.api.credit.model.InstrumentBalance;
import br.com.unopay.api.credit.repository.InstrumentBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class InstrumentBalanceService {

    private InstrumentBalanceRepository repository;

    @Autowired
    public InstrumentBalanceService(InstrumentBalanceRepository repository) {
        this.repository = repository;
    }

    public InstrumentBalance save(InstrumentBalance balance) {
        return repository.save(balance);
    }

    public InstrumentBalance findBydId(String id) {
        return repository.findOne(id);
    }
}
