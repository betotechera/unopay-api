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

    @Autowired
    public AccreditedNetworkService(AccreditedNetworkRepository repository, UserDetailRepository userDetailRepository, PersonService personService) {
        this.repository = repository;
        this.userDetailRepository = userDetailRepository;
        this.personService = personService;
    }

    public AccreditedNetwork create(AccreditedNetwork accreditedNetwork) {
        try {
            personService.save(accreditedNetwork.getPerson());
            return repository.save(accreditedNetwork);
        } catch (DataIntegrityViolationException e){
            log.warn(String.format("Person institution already exists %s", accreditedNetwork.getPerson()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.PERSON_INSTITUTION_ALREADY_EXISTS);

        }
    }

    public Page<AccreditedNetwork> findByFilter(AccreditedNetworkFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public AccreditedNetwork getById(String id) {
        AccreditedNetwork AccreditedNetwork = repository.findOne(id);
        if (AccreditedNetwork == null) {
            throw UnovationExceptions.notFound();
        }
        return AccreditedNetwork;
    }

    public List<AccreditedNetwork> findAll(List<String> ids){
        List<AccreditedNetwork> AccreditedNetworks = repository.findByIdIn(ids);
        List<String> founds = AccreditedNetworks.stream().map(AccreditedNetwork::getId).collect(Collectors.toList());
        List<String> notFounds = ids.stream().filter(id -> !founds.contains(id)).collect(Collectors.toList());
        if(!notFounds.isEmpty()) throw UnovationExceptions.notFound().withErrors(PAYMENT_RULE_GROUP_NOT_FOUND.withArguments(notFounds));
        return  AccreditedNetworks;
    }

    public void update(String id, AccreditedNetwork accreditedNetwork) {
        AccreditedNetwork current = repository.findOne(id);
        current.updateModel(accreditedNetwork);
        personService.save(accreditedNetwork.getPerson());
        repository.save(current);
    }

    public void delete(String id) {
        getById(id);
        if(hasUser(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.PAYMENT_RULE_GROUP_WITH_USERS);
        }

        repository.delete(id);
    }

    private Boolean hasUser(String id) {
        return userDetailRepository.countByAccreditedNetworkId(id) > 0;
    }
}
