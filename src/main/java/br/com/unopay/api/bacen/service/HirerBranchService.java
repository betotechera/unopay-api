package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.HirerBranch;
import br.com.unopay.api.bacen.model.filter.HirerBranchFilter;
import br.com.unopay.api.bacen.repository.HirerBranchRepository;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.uaa.exception.Errors;
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
public class HirerBranchService {

    private HirerBranchRepository repository;
    private PersonService personService;
    private HirerService hirerService;
    private BankAccountService bankAccountService;

    @Autowired
    public HirerBranchService(BankAccountService bankAccountService,
                              HirerService hirerService,
                              PersonService personService,
                              HirerBranchRepository repository){
        this.repository = repository;
        this.bankAccountService = bankAccountService;
        this.hirerService = hirerService;
        this.personService = personService;

    }

    public HirerBranch create(HirerBranch hirer) {
        try {
            validateHirer(hirer);
            bankAccountService.create(hirer.getBankAccount());
            personService.create(hirer.getPerson());
            return repository.save(hirer);
        } catch (DataIntegrityViolationException e){
            log.warn(String.format("Person hirer already exists %s", hirer.getPerson()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.PERSON_HIRER_BRANCH_ALREADY_EXISTS);

        }
    }

    public HirerBranch getById(String id) {
        Optional<HirerBranch> hirer = repository.findById(id);
        return hirer.orElseThrow(()->UnovationExceptions.notFound().withErrors(Errors.HIRER_BRANCH_NOT_FOUND));
    }

    public void update(String id, HirerBranch hirer) {
        HirerBranch current = repository.findOne(id);
        current.updateMe(hirer);
        validateHirer(hirer);
        personService.create(hirer.getPerson());
        repository.save(current);
    }

    private void validateHirer(HirerBranch hirer) {
        hirerService.getById(hirer.getHeadOffice().getId());
    }

    public void delete(String id) {
        getById(id);
        repository.delete(id);
    }

    public Page<HirerBranch> findByFilter(HirerBranchFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }


}
