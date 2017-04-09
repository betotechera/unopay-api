package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Subsidiary;
import br.com.unopay.api.bacen.repository.EstablishmentRepository;
import br.com.unopay.api.bacen.repository.SubsidiaryRepository;
import br.com.unopay.api.service.ContactService;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.unopay.api.uaa.exception.Errors.*;

@Service
public class EstablishmentService {

    private EstablishmentRepository repository;
    private SubsidiaryRepository subsidiaryRepository;
    private ContactService contactService;
    private PersonService personService;
    private AccreditedNetworkService networkService;
    private BrandFlagService brandFlagService;
    private BankAccountService bankAccountService;

    @Autowired
    public EstablishmentService(EstablishmentRepository repository,
                                SubsidiaryRepository subsidiaryRepository,
                                ContactService contactService,
                                PersonService personService,
                                AccreditedNetworkService networkService,
                                BrandFlagService brandFlagService,
                                BankAccountService bankAccountService){
        this.repository = repository;
        this.subsidiaryRepository = subsidiaryRepository;
        this.contactService = contactService;
        this.personService = personService;
        this.networkService = networkService;
        this.bankAccountService = bankAccountService;
        this.brandFlagService = brandFlagService;
        this.bankAccountService = bankAccountService;
    }



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
        List<Subsidiary> subsidiaries =  subsidiaryRepository.findByMatrixId(id);
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
