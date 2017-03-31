package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.AccreditedNetworkFilter;
import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.repository.InstitutionRepository;
import br.com.unopay.api.bacen.repository.AccreditedNetworkRepository;
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

import java.util.List;
import java.util.stream.Collectors;

import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_RULE_GROUP_CODE_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_RULE_GROUP_NOT_FOUND;

@Slf4j
@Service
public class AccreditedNetworkService {

    private AccreditedNetworkRepository repository;

    private UserDetailRepository userDetailRepository;

    private PersonService personService;

    private BankAccountService bankAccountService;


    @Autowired
    public AccreditedNetworkService(AccreditedNetworkRepository repository, UserDetailRepository userDetailRepository, PersonService personService,
                                    BankAccountService bankAccountService) {
        this.repository = repository;
        this.userDetailRepository = userDetailRepository;
        this.personService = personService;
        this.bankAccountService = bankAccountService;
    }

    public AccreditedNetwork create(AccreditedNetwork accreditedNetwork) {
        try {
            personService.save(accreditedNetwork.getPerson());
            bankAccountService.create(accreditedNetwork.getBankAccount());
            return repository.save(accreditedNetwork);
        } catch (DataIntegrityViolationException e){
            log.warn(String.format("Person institution already exists %s", accreditedNetwork.getPerson()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.PERSON_ACCREDITED_NETWORK_ALREADY_EXISTS);

        }
    }

    public Page<AccreditedNetwork> findByFilter(AccreditedNetworkFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public AccreditedNetwork getById(String id) {
        AccreditedNetwork accreditedNetwork = repository.findOne(id);
        if (accreditedNetwork == null) {
            throw UnovationExceptions.notFound();
        }
        return accreditedNetwork;
    }

    public void update(String id, AccreditedNetwork accreditedNetwork) {
        AccreditedNetwork current = repository.findOne(id);
        current.updateModel(accreditedNetwork);
        personService.save(accreditedNetwork.getPerson());
        repository.save(current);
    }

    public void delete(String id) {
        getById(id);
        if(hasUser(id))
            throw UnovationExceptions.conflict().withErrors(Errors.ACCREDITED_NETWORK_WITH_USERS);

        repository.delete(id);
    }

    private Boolean hasUser(String id) {
        return userDetailRepository.countByAccreditedNetworkId(id) > 0;
    }
}
