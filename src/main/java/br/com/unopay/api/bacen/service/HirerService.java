package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.filter.HirerFilter;
import br.com.unopay.api.bacen.repository.HirerRepository;
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

import static br.com.unopay.api.uaa.exception.Errors.HIRER_DOCUMENT_NOT_FOUND;

@Slf4j
@Service
public class HirerService {
    private HirerRepository repository;
    private PersonService personService;
    private UserDetailRepository userDetailRepository;
    private BankAccountService bankAccountService;

    @Autowired
    public HirerService(HirerRepository repository, PersonService personService,
                        UserDetailRepository userDetailRepository, BankAccountService bankAccountService) {
        this.repository = repository;
        this.personService = personService;
        this.userDetailRepository = userDetailRepository;
        this.bankAccountService = bankAccountService;
    }

    public Hirer create(Hirer hirer) {
        try {
            bankAccountService.create(hirer.getBankAccount());
            personService.save(hirer.getPerson());
            return repository.save(hirer);
        } catch (DataIntegrityViolationException e){
            log.warn(String.format("Person hirer already exists %s", hirer.getPerson()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.PERSON_HIRER_ALREADY_EXISTS);

        }
    }

    public Hirer getById(String id) {
        Hirer hirer = repository.findOne(id);
        if(hirer == null) {
            throw UnovationExceptions.notFound().withErrors(Errors.HIRER_NOT_FOUND);
        }
        return hirer;

    }

    public void update(String id, Hirer hirer) {
        Hirer current = repository.findOne(id);
        current.updateModel(hirer);
        personService.save(hirer.getPerson());
        repository.save(current);
    }

    public void delete(String id) {
        getById(id);
        if(hasUser(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.HIRER_WITH_USERS);
        }
        repository.delete(id);
    }

    public Hirer findByDocumentNumber(String documentNumber){
        Hirer hirer = repository.findByPersonDocumentNumber(documentNumber);
        if(hirer == null){
            throw UnovationExceptions.notFound().withErrors(HIRER_DOCUMENT_NOT_FOUND);
        }
        return repository.findByPersonDocumentNumber(documentNumber);
    }

    private Boolean hasUser(String id) {
        return userDetailRepository.countByHirerId(id) > 0;
    }

    public Page<Hirer> findByFilter(HirerFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

}
