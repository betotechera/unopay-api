package br.com.unopay.api.service;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.filter.PersonFilter;
import br.com.unopay.api.repository.AddressRepository;
import br.com.unopay.api.repository.LegalPersonDetailRepository;
import br.com.unopay.api.repository.PersonRepository;
import br.com.unopay.api.repository.PhysicalPersonDetailRepository;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.PERSON_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PERSON_NOT_FOUND;

@Slf4j
@Service
public class PersonService {

    private PersonRepository repository;

    private AddressRepository addressRepository;

    private LegalPersonDetailRepository legalPersonDetailRepository;

    private PhysicalPersonDetailRepository physicalPersonDetailRepository;

    @Autowired
    public PersonService(PersonRepository repository,
                         AddressRepository addressRepository,
                         LegalPersonDetailRepository legalPersonDetailRepository,
                         PhysicalPersonDetailRepository physicalPersonDetailRepository) {
        this.repository = repository;
        this.addressRepository = addressRepository;
        this.legalPersonDetailRepository = legalPersonDetailRepository;
        this.physicalPersonDetailRepository = physicalPersonDetailRepository;
    }

    public Person createOrUpdate(Person person){
        try {
            person.validate();
            person.normalize();
            addressRepository.save(person.getAddress());
            Optional<Person> byDocumentNumber = repository.findByIdOrDocumentNumber(person.getId(), person.documentNumber());
            return byDocumentNumber.map(current -> update(current, person)).orElseGet(() -> save(person));
        } catch (DataIntegrityViolationException e) {
            log.warn(String.format("Person document already exists %s", person.toString()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.PERSON_DOCUMENT_ALREADY_EXISTS)
                    .withArguments(person.getDocument());
        }
    }

    private Person update(Person current, Person person) {
        current.updateMe(person);
        if(current.isLegal()) {
            current.getLegalPersonDetail().updateMe(person.getLegalPersonDetail());
        }
        if(current.isPhysical()) {
            current.getPhysicalPersonDetail().updateMe(person.getPhysicalPersonDetail());
        }
        return save(current);
    }

    private Person save(Person person) {
        if(person.isLegal()) {
            legalPersonDetailRepository.save(person.getLegalPersonDetail());
        }
        if(person.isPhysical()) {
            physicalPersonDetailRepository.save(person.getPhysicalPersonDetail());
        }
        return repository.save(person);
    }

    public Person findByFilter(PersonFilter personFilter){
        Page<Person> person = repository.findAll(personFilter, singlePageRequest());
        if(person.hasContent()) {
            return person.getContent().get(0);
        }
        throw UnovationExceptions.notFound().withErrors(Errors.PERSON_WITH_DOCUMENT_NOT_FOUND);
    }

    public Person findById(String id){
        if(id == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PERSON_ID_REQUIRED);
        }
        Optional<Person> person =  repository.findById(id);
        return person.orElseThrow(()->UnovationExceptions.notFound().withErrors(PERSON_NOT_FOUND.withOnlyArgument(id)));
    }

    public Person findByDocument(String document) {
        Optional<Person> person = repository.findByDocumentNumber(document);
        return person.orElseThrow(()-> UnovationExceptions.notFound()
                .withErrors(PERSON_NOT_FOUND.withOnlyArgument(document)));
    }

    public Optional<Person> findOptionalByDocument(String document) {
        return repository.findByDocumentNumber(document);
    }

    private PageRequest singlePageRequest() {

        return new PageRequest(0, 1);
    }

}
