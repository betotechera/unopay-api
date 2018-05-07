package br.com.unopay.api.market.service;

import br.com.unopay.api.market.model.BonusBilling;
import br.com.unopay.api.market.repository.BonusBillingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BonusBillingService {
    private BonusBillingRepository repository;

    @Autowired
    public BonusBillingService(BonusBillingRepository repository) {
        this.repository = repository;
    }

    public BonusBilling save(BonusBilling bonusBilling) {
        return repository.save(bonusBilling);
    }
}
