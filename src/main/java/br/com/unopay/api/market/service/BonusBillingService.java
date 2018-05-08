package br.com.unopay.api.market.service;

import br.com.unopay.api.market.model.BonusBilling;
import br.com.unopay.api.market.repository.BonusBillingRepository;
import br.com.unopay.api.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BonusBillingService {
    private BonusBillingRepository repository;
    private PersonService personService;

    @Autowired
    public BonusBillingService(BonusBillingRepository repository,
                               PersonService personService) {
        this.repository = repository;
        this.personService = personService;
    }

    public BonusBilling create(BonusBilling bonusBilling) {
        bonusBilling.validateMe();
        validateReferences(bonusBilling);
        return save(bonusBilling);
    }

    public BonusBilling save(BonusBilling bonusBilling) {
        return repository.save(bonusBilling);
    }

    private void validateReferences(BonusBilling bonusBilling) {
        bonusBilling.setPerson(personService.findById(bonusBilling.personId()));
    }
}
