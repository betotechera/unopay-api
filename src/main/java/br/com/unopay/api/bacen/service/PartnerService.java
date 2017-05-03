package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Partner;
import br.com.unopay.api.bacen.model.filter.PartnerFilter;
import br.com.unopay.api.bacen.repository.PartnerRepository;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class PartnerService {
    private PartnerRepository repository;
    private PersonService personService;
    private UserDetailRepository userDetailRepository;
    private BankAccountService bankAccountService;

    @Autowired
    public PartnerService(PartnerRepository repository, PersonService personService,
                          UserDetailRepository userDetailRepository, BankAccountService bankAccountService) {
        this.repository = repository;
        this.personService = personService;
        this.userDetailRepository = userDetailRepository;
        this.bankAccountService = bankAccountService;
    }

    public Partner create(Partner hirer) {
        try {
            if(hirer.getBankAccount() != null) {
                bankAccountService.create(hirer.getBankAccount());
            }
            personService.save(hirer.getPerson());
            return repository.save(hirer);
        } catch (DataIntegrityViolationException e){
            log.warn(String.format("Person partner already exists %s", hirer.getPerson()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.PERSON_PARTNER_ALREADY_EXISTS);

        }
    }

    public Partner getById(String id) {
        Optional<Partner> hirer = repository.findById(id);
        return hirer.orElseThrow(()->UnovationExceptions.notFound().withErrors(Errors.PARTNER_NOT_FOUND));
    }

    public void update(String id, Partner hirer) {
        Partner current = repository.findOne(id);
        current.updateModel(hirer);
        personService.save(hirer.getPerson());
        repository.save(current);
    }

    public void delete(String id) {
        getById(id);
        if(hasUser(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.PARTNER_WITH_USERS);
        }
        repository.delete(id);
    }

    private Boolean hasUser(String id) {
        return userDetailRepository.countByPartnerId(id) > 0;
    }

    public Page<Partner> findByFilter(PartnerFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

}
