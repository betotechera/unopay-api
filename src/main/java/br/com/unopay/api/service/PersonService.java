package br.com.unopay.api.service;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.PersonFilter;
import br.com.unopay.api.repository.AddressRepository;
import br.com.unopay.api.repository.LegalPersonDetailRepository;
import br.com.unopay.api.repository.PersonRepository;
import br.com.unopay.api.repository.PhysicalPersonDetailRepository;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
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
    public PersonService(PersonRepository repository, AddressRepository addressRepository,
                         LegalPersonDetailRepository legalPersonDetailRepository, PhysicalPersonDetailRepository physicalPersonDetailRepository) {
        this.repository = repository;
        this.addressRepository = addressRepository;
        this.legalPersonDetailRepository = legalPersonDetailRepository;
        this.physicalPersonDetailRepository = physicalPersonDetailRepository;
    }

    public Person save(Person person){
        try {
            person.validate();
            addressRepository.save(person.getAddress());
            if(person.isLegal())
                legalPersonDetailRepository.save(person.getLegalPersonDetail());
            else
                physicalPersonDetailRepository.save(person.getPhysicalPersonDetail());
            return repository.save(person);
        } catch (DataIntegrityViolationException e) {
            log.warn(String.format("Person document already exists %s", person.toString()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.PERSON_DOCUMENT_ALREADY_EXISTS).withArguments(person.getDocument());
        }
    }

    public Person findByDocument(PersonFilter personFilter){
        Page<Person> person = repository.findAll(personFilter, singlePageRequest());
        if(person.hasContent())
          return  person.getContent().get(0);
        throw UnovationExceptions.notFound().withErrors(Errors.PERSON_WITH_DOCUMENT_NOT_FOUND);
    }

    public Person findById(String id){
        if(id == null) throw UnovationExceptions.unprocessableEntity().withErrors(PERSON_ID_REQUIRED);
        Person person = repository.findOne(id);
        if(person == null) throw UnovationExceptions.notFound().withErrors(PERSON_NOT_FOUND.withArguments(id));
        return  person;
    }

    private PageRequest singlePageRequest() {

        return new PageRequest(0, 1);
    }

}
