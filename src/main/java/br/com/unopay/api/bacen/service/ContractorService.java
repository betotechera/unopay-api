package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.api.bacen.repository.ContractorRepository;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ContractorService {

    private ContractorRepository repository;
    private PersonService personService;
    private UserDetailRepository userDetailRepository;
    private BankAccountService bankAccountService;

    @Autowired
    public ContractorService(ContractorRepository repository, PersonService personService,
                             UserDetailRepository userDetailRepository, BankAccountService bankAccountService) {
        this.repository = repository;
        this.personService = personService;
        this.userDetailRepository = userDetailRepository;
        this.bankAccountService = bankAccountService;
    }

    public Contractor create(Contractor contractor) {
        try {
            bankAccountService.create(contractor.getBankAccount());
            personService.save(contractor.getPerson());
            return repository.save(contractor);
        } catch (DataIntegrityViolationException e){
            log.warn(String.format("Person contractor already exists %s", contractor.getPerson()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.PERSON_CONTRACTOR_ALREADY_EXISTS);

        }
    }

    public Contractor getById(String id) {
        Optional<Contractor> hirer = repository.findById(id);
        return hirer.orElseThrow(()->UnovationExceptions.notFound().withErrors(Errors.CONTRACTOR_NOT_FOUND));
    }

    public void update(String id, Contractor contractor) {
        Contractor current = repository.findOne(id);
        current.updateModel(contractor);
        personService.save(contractor.getPerson());
        repository.save(current);
    }

    public void delete(String id) {
        getById(id);
        if(hasUser(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.CONTRACTOR_WITH_USERS);
        }
        repository.delete(id);
    }

    private Boolean hasUser(String id) {
        return userDetailRepository.countByContractorId(id) > 0;
    }

    public Page<Contractor> findByFilter(ContractorFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }


}
