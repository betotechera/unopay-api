package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.filter.HirerFilter;
import br.com.unopay.api.bacen.repository.HirerRepository;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.HIRER_DOCUMENT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.INVALID_USER_TYPE;

@Slf4j
@Service
public class HirerService {
    private HirerRepository repository;
    private PersonService personService;
    private UserDetailService userDetailService;
    private BankAccountService bankAccountService;

    @Autowired
    public HirerService(HirerRepository repository, PersonService personService,
                        UserDetailService userDetailService, BankAccountService bankAccountService) {
        this.repository = repository;
        this.personService = personService;
        this.userDetailService = userDetailService;
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

    public Hirer getMe(String email) {
        Hirer userHirer = getUserHirer(email);
        return getById(userHirer.getId());
    }

    public Hirer getById(String id) {
        Optional<Hirer> hirer = repository.findById(id);
        return hirer.orElseThrow(()->UnovationExceptions.notFound().withErrors(Errors.HIRER_NOT_FOUND));
    }

    public void updateMe(String email, Hirer hirer) {
        Hirer userHirer = getUserHirer(email);
        update(userHirer.getId(), hirer);
    }

    public void update(String id, Hirer hirer) {
        Hirer current = repository.findOne(id);
        current.updateModel(hirer);
        personService.save(hirer.getPerson());
        repository.save(current);
    }

    public void delete(String id) {
        getById(id);
        if(userDetailService.hasHirer(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.HIRER_WITH_USERS.withOnlyArgument(id));
        }
        repository.delete(id);
    }

    public Hirer findByDocumentNumber(String documentNumber){
        Optional<Hirer> hirer = repository.findByPersonDocumentNumber(documentNumber);
        return hirer.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(HIRER_DOCUMENT_NOT_FOUND.withOnlyArgument(documentNumber)));
    }

    public Page<Hirer> findMeByFilter(String email, HirerFilter filter, UnovationPageRequest pageable) {
        Hirer userHirer = getUserHirer(email);
        filter.setDocumentNumber(userHirer.getDocumentNumber());
        return findByFilter(filter, pageable);
    }
    public Page<Hirer> findByFilter(HirerFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private Hirer getUserHirer(String email) {
        UserDetail currentUser = userDetailService.getByEmail(email);
        return currentUser.myHirer()
                .orElseThrow(()-> UnovationExceptions.forbidden().withErrors(INVALID_USER_TYPE));
    }

}
