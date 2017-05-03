package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.filter.EstablishmentFilter;
import br.com.unopay.api.bacen.repository.BranchRepository;
import br.com.unopay.api.bacen.repository.EstablishmentRepository;
import br.com.unopay.api.service.ContactService;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.uaa.exception.Errors;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_WITH_BRANCH;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class EstablishmentService {

    private EstablishmentRepository repository;
    private BranchRepository branchRepository;
    private ContactService contactService;
    private PersonService personService;
    private AccreditedNetworkService networkService;
    private BrandFlagService brandFlagService;
    private BankAccountService bankAccountService;
    private UserDetailRepository userDetailRepository;

    @Autowired
    public EstablishmentService(EstablishmentRepository repository,
                                BranchRepository branchRepository,
                                ContactService contactService,
                                PersonService personService,
                                AccreditedNetworkService networkService,
                                BrandFlagService brandFlagService,
                                BankAccountService bankAccountService,
                                UserDetailRepository userDetailRepository) {
        this.repository = repository;
        this.branchRepository = branchRepository;
        this.contactService = contactService;
        this.personService = personService;
        this.networkService = networkService;
        this.brandFlagService = brandFlagService;
        this.bankAccountService = bankAccountService;
        this.userDetailRepository = userDetailRepository;
    }

    public Establishment create(Establishment establishment) {
        establishment.validateCreate();
        saveReferences(establishment);
        validateReferences(establishment);
        return repository.save(establishment);
    }

    public void update(String id, Establishment establishment) {
        establishment.validateUpdate();
        Establishment current = findById(id);
        validateReferences(establishment);
        saveReferences(establishment);
        current.updateMe(establishment);
        repository.save(current);
    }

    public Establishment findById(String id) {
        Establishment establishment = repository.findOne(id);
        if(establishment == null){
            throw UnovationExceptions.notFound().withErrors(ESTABLISHMENT_NOT_FOUND);
        }
        return establishment;
    }

    public void delete(String id) {
        findById(id);
        validateDelete(id);
        repository.delete(id);
    }

    private void validateDelete(String id) {
        if(hasBranches(id)){
            throw UnovationExceptions.conflict().withErrors(ESTABLISHMENT_WITH_BRANCH);
        }
        if(hasUsers(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.ESTABLISHMENT_WITH_USERS);
        }
    }

    private boolean hasUsers(String id) {
        return userDetailRepository.countByEstablishmentId(id) > 0;
    }

    private boolean hasBranches(String id) {
        return branchRepository.countByHeadOfficeId(id) > 0;
    }

    private void saveReferences(Establishment establishment) {
        contactService.save(establishment.getAdministrativeContact());
        contactService.save(establishment.getFinancierContact());
        contactService.save(establishment.getOperationalContact());
        personService.save(establishment.getPerson());
        bankAccountService.create(establishment.getBankAccount());
    }

    private void validateReferences(Establishment establishment) {
        brandFlagService.findById(establishment.getBrandFlag().getId());
        networkService.getById(establishment.getNetwork().getId());
        contactService.findById(establishment.getOperationalContact().getId());
        contactService.findById(establishment.getFinancierContact().getId());
        contactService.findById(establishment.getAdministrativeContact().getId());
        bankAccountService.findById(establishment.getBankAccount().getId());
    }

    public Page<Establishment> findByFilter(EstablishmentFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }
}
