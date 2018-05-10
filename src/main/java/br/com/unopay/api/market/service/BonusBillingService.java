package br.com.unopay.api.market.service;

import br.com.unopay.api.market.model.BonusBilling;
import br.com.unopay.api.market.model.filter.BonusBillingFilter;
import br.com.unopay.api.market.repository.BonusBillingRepository;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public BonusBilling findById(String id) {
        Optional<BonusBilling> BonusBilling = repository.findById(id);
        return BonusBilling.orElseThrow(()-> UnovationExceptions.notFound().withErrors(
                Errors.BONUS_BILLING_NOT_FOUND));
    }

    public Page<BonusBilling> findByFilter(BonusBillingFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }
}
