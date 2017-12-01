package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.filter.EstablishmentFilter;
import br.com.unopay.api.bacen.repository.BranchRepository;
import br.com.unopay.api.bacen.repository.EstablishmentEventRepository;
import br.com.unopay.api.bacen.repository.EstablishmentRepository;
import br.com.unopay.api.job.BatchClosingJob;
import br.com.unopay.api.job.UnopayScheduler;
import br.com.unopay.api.service.ContactService;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Optional;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_WITH_BRANCH;
import static br.com.unopay.api.uaa.exception.Errors.CANNOT_INVOKE_TYPE;

@Service
public class EstablishmentService {

    private EstablishmentRepository repository;
    private BranchRepository branchRepository;
    private ContactService contactService;
    private PersonService personService;
    private AccreditedNetworkService networkService;
    private BankAccountService bankAccountService;
    private UserDetailService userDetailService;
    private EstablishmentEventRepository establishmentEventRepository;
    @Setter
    private UnopayScheduler scheduler;

    @Autowired
    public EstablishmentService(EstablishmentRepository repository,
                                BranchRepository branchRepository,
                                ContactService contactService,
                                PersonService personService,
                                AccreditedNetworkService networkService,
                                BankAccountService bankAccountService,
                                UserDetailService userDetailService,
                                EstablishmentEventRepository establishmentEventRepository,
                                UnopayScheduler scheduler) {
        this.repository = repository;
        this.branchRepository = branchRepository;
        this.contactService = contactService;
        this.personService = personService;
        this.networkService = networkService;
        this.bankAccountService = bankAccountService;
        this.userDetailService = userDetailService;
        this.establishmentEventRepository = establishmentEventRepository;
        this.scheduler = scheduler;
    }

    public Establishment create(Establishment establishment) {
        establishment.validateCreate();
        saveReferences(establishment);
        validateReferences(establishment);
        Establishment created = repository.save(establishment);
        scheduleClosingJob(created);
        return created;
    }

    public void updateMe(String email, Establishment establishment) {
        Establishment userEstablishment = getUserEstablishment(email);
        update(userEstablishment.getId(), establishment);
    }

    public void update(String id, Establishment establishment) {
        establishment.validateUpdate();
        Establishment current = findById(id);
        validateReferences(establishment);
        saveReferences(establishment);
        current.updateMe(establishment);
        repository.save(current);
        scheduleClosingJob(current);
    }

    public Establishment findMe(String email) {
        Establishment userEstablishment = getUserEstablishment(email);
        return findById(userEstablishment.getId());
    }

    public Establishment findByIdAndNewtwork(String id, AccreditedNetwork network) {
        Optional<Establishment> establishment = repository.findByIdAndNetworkId(id, network.getId());
        return establishment.orElseThrow(()->UnovationExceptions.notFound().withErrors(ESTABLISHMENT_NOT_FOUND));
    }

    public Establishment findById(String id) {
        Optional<Establishment> establishment = repository.findById(id);
        return establishment.orElseThrow(()->UnovationExceptions.notFound().withErrors(ESTABLISHMENT_NOT_FOUND));
    }

    public Optional<Establishment> findByIdOptional(String id){
        return repository.findById(id);
    }

    public Establishment findByDocumentNumber(String document) {
        Optional<Establishment> establishment = repository.findByPersonDocumentNumber(document);
        return establishment.orElseThrow(()->UnovationExceptions.notFound().withErrors(ESTABLISHMENT_NOT_FOUND));
    }

    public void delete(String id) {
        findById(id);
        validateDelete(id);
        repository.delete(id);
    }

    public Page<Establishment> findMeByFilter(String email, EstablishmentFilter filter, UnovationPageRequest pageable) {
        Establishment userEstablishment = getUserEstablishment(email);
        filter.setDocumentNumber(userEstablishment.documentNumber());
        return findByFilter(filter, pageable);
    }

    public Page<Establishment> findByFilterForNetwork(AccreditedNetwork accreditedNetwork,
                                                      EstablishmentFilter filter, UnovationPageRequest pageable) {
        filter.setAccreditedNetwork(accreditedNetwork.documentNumber());
        return findByFilter(filter, pageable);
    }

    public Page<Establishment> findByFilter(EstablishmentFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private void validateDelete(String id) {
        if(hasBranches(id)){
            throw UnovationExceptions.conflict().withErrors(ESTABLISHMENT_WITH_BRANCH);
        }
        if(userDetailService.hasEstablishment(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.ESTABLISHMENT_WITH_USERS);
        }
        if(hasEventValue(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.ESTABLISHMENT_WITH_EVENT_VALUE);
        }
    }

    private boolean hasBranches(String id) {
        return branchRepository.countByHeadOfficeId(id) > 0;
    }

    private boolean hasEventValue(String id){
        return establishmentEventRepository.countByEstablishmentId(id) > 0;
    }

    private void saveReferences(Establishment establishment) {
        contactService.save(establishment.getAdministrativeContact());
        contactService.save(establishment.getFinancierContact());
        contactService.save(establishment.getOperationalContact());
        personService.save(establishment.getPerson());
        bankAccountService.create(establishment.getBankAccount());
    }

    private void validateReferences(Establishment establishment) {
        networkService.getById(establishment.getNetwork().getId());
        contactService.findById(establishment.getOperationalContact().getId());
        contactService.findById(establishment.getFinancierContact().getId());
        contactService.findById(establishment.getAdministrativeContact().getId());
        bankAccountService.findById(establishment.getBankAccount().getId());
    }

    private Establishment getUserEstablishment(String email) {
        UserDetail currentUser = userDetailService.getByEmail(email);
        return currentUser.myEstablishment()
                .orElseThrow(()-> UnovationExceptions.forbidden().withErrors(CANNOT_INVOKE_TYPE));
    }

    private void scheduleClosingJob(Establishment created) {
        scheduler.schedule(created.getId(), created.getCheckout().getPeriod().getPattern(),BatchClosingJob.class);
    }
}
