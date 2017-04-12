package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Hired;
import br.com.unopay.api.bacen.model.filter.HiredFilter;
import br.com.unopay.api.bacen.repository.HiredRepository;
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

@Slf4j
@Service
public class HiredService {
    private HiredRepository repository;
    private PersonService personService;
    private UserDetailRepository userDetailRepository;
    private BankAccountService bankAccountService;

    @Autowired
    public HiredService(HiredRepository repository, PersonService personService,
                        UserDetailRepository userDetailRepository, BankAccountService bankAccountService) {
        this.repository = repository;
        this.personService = personService;
        this.userDetailRepository = userDetailRepository;
        this.bankAccountService = bankAccountService;
    }

    public Hired create(Hired hirer) {
        try {
            bankAccountService.create(hirer.getBankAccount());
            personService.save(hirer.getPerson());
            return repository.save(hirer);
        } catch (DataIntegrityViolationException e){
            log.warn(String.format("Person hired already exists %s", hirer.getPerson()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.PERSON_HIRED_ALREADY_EXISTS);

        }
    }

    public Hired getById(String id) {
        Hired hirer = repository.findOne(id);
        if(hirer == null) {
            throw UnovationExceptions.notFound().withErrors(Errors.HIRED_NOT_FOUND);
        }
        return hirer;

    }

    public void update(String id, Hired hirer) {
        Hired current = repository.findOne(id);
        current.updateModel(hirer);
        personService.save(hirer.getPerson());
        repository.save(current);
    }

    public void delete(String id) {
        getById(id);
        if(hasUser(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.HIRED_WITH_USERS);
        }
        repository.delete(id);
    }

    private Boolean hasUser(String id) {
        return userDetailRepository.countByHiredId(id) > 0;
    }

    public Page<Hired> findByFilter(HiredFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

}
