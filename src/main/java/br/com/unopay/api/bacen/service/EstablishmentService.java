package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Subsidiary;
import br.com.unopay.api.bacen.repository.EstablishmentRepository;
import br.com.unopay.api.service.ContactService;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.unopay.api.uaa.exception.Errors.*;

@Service
public class EstablishmentService {

    @Autowired
    private EstablishmentRepository repository;

    @Autowired
    private SubsidiaryService subsidiaryService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private PersonService personService;

    @Autowired
    private AccreditedNetworkService networkService;

    @Autowired
    private BrandFlagService brandFlagService;

    @Autowired
    private BankAccountService bankAccountService;


    public Establishment create(Establishment establishment) {
        establishment.validateCreate();
        createReferences(establishment);
        validateExistingReferences(establishment);
        return repository.save(establishment);
    }

    public void update(String id, Establishment establishment) {
        establishment.validateUpdate();
        Establishment current = findById(id);
        validateExistingReferences(establishment);
        current.setTechnicalContact(establishment.getTechnicalContact());
        repository.save(current);

    }

    public Establishment findById(String id) {
        Establishment establishment = repository.findOne(id);
        if(establishment == null) throw UnovationExceptions.notFound().withErrors(ESTABLISHMENT_NOT_FOUND);
        return establishment;
    }

    public void delete(String id) {
        findById(id);
        List<Subsidiary> subsidiaries =  subsidiaryService.findByMatrixId(id);
        if(!subsidiaries.isEmpty()) throw UnovationExceptions.conflict().withErrors(ESTABLISHMENT_WITH_SUBSIDIARY);
        repository.delete(id);
    }

    private void createReferences(Establishment establishment) {
        contactService.create(establishment.getAdministrativeContact());
        contactService.create(establishment.getFinancierContact());
        contactService.create(establishment.getOperationalContact());
        personService.save(establishment.getPerson());
        bankAccountService.create(establishment.getBankAccount());
    }

    private void validateExistingReferences(Establishment establishment) {
        brandFlagService.findById(establishment.getBrandFlag().getId());
        networkService.getById(establishment.getNetwork().getId());
        contactService.findById(establishment.getOperationalContact().getId());
        contactService.findById(establishment.getFinancierContact().getId());
        contactService.findById(establishment.getAdministrativeContact().getId());
        bankAccountService.findById(establishment.getBankAccount().getId());
    }

}
