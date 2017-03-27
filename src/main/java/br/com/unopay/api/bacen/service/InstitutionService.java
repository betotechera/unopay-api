package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.InstitutionFilter;
import br.com.unopay.api.bacen.repository.InstitutionRepository;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.PersonFilter;
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
public class InstitutionService {

    private InstitutionRepository repository;

   private PersonService personService;

    private UserDetailRepository userDetailRepository;

    @Autowired
    public InstitutionService(InstitutionRepository repository, PersonService personService, UserDetailRepository userDetailRepository) {
        this.repository = repository;
        this.personService = personService;
        this.userDetailRepository = userDetailRepository;
    }

    public Institution create(Institution institution) {
            Person person;
            try {
                 person = personService.findByDocument(new PersonFilter(institution.getPerson().getDocument()));
                 person.updateModel(person);
                 institution.setPerson(person);
            } catch(Exception e){
                person = institution.getPerson();
                log.info("Person not found. Creating {}", person);
            }

            personService.save(person);
        try {
            return repository.save(institution);
        } catch (DataIntegrityViolationException e){
            log.warn(String.format("Person institution already exists %s", institution.getPerson()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.PERSON_INSTITUTION_ALREADY_EXISTS);

        }
    }

    public Institution getById(String id) {
        Institution institution = repository.findOne(id);
        if (institution == null) {
            throw UnovationExceptions.notFound();
        }
        return institution;

    }

    public void update(String id, Institution institution) {
        Institution current = repository.findOne(id);
        current.updateModel(institution);
        personService.save(institution.getPerson());
        repository.save(current);
    }

    public void delete(String id) {
        getById(id);
        repository.delete(id);
    }


    public Page<Institution> findByFilter(InstitutionFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

}
