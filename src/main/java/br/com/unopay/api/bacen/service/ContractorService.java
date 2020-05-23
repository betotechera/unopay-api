package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.api.bacen.repository.ContractorRepository;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.filter.ContractFilter;
import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.CONTRACTOR_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACTOR_WITH_USERS;
import static br.com.unopay.api.uaa.exception.Errors.PERSON_CONTRACTOR_ALREADY_EXISTS;

@Slf4j
@Service
public class ContractorService {

    private ContractorRepository repository;
    private PersonService personService;
    private UserDetailRepository userDetailRepository;
    private BankAccountService bankAccountService;

    @Autowired
    public ContractorService(ContractorRepository repository,
                             PersonService personService,
                             UserDetailRepository userDetailRepository,
                             BankAccountService bankAccountService) {
        this.repository = repository;
        this.personService = personService;
        this.userDetailRepository = userDetailRepository;
        this.bankAccountService = bankAccountService;
    }

    public Contractor create(Contractor contractor) {
        try {
            checkContractor(contractor);
            if(contractor.withBankAccount()) {
                bankAccountService.create(contractor.getBankAccount());
            }
            personService.createOrUpdate(contractor.getPerson());
            return repository.save(contractor);
        } catch (DataIntegrityViolationException e){
            log.warn(String.format("Person contractor already exists %s", contractor.getPerson()), e);
            throw UnovationExceptions.conflict()
                    .withErrors(PERSON_CONTRACTOR_ALREADY_EXISTS
                            .withOnlyArgument(contractor.getDocumentNumber()));

        }
    }

    private void checkContractor(Contractor contractor) {
        Optional<Contractor> contractorOptional = getOptionalByDocument(contractor.getDocumentNumber());
        contractorOptional.ifPresent((ThrowingConsumer)-> {
            throw UnovationExceptions.conflict().withErrors(PERSON_CONTRACTOR_ALREADY_EXISTS
                .withOnlyArgument(contractor.getDocumentNumber()));
        });
    }

    public Contractor getByIdForIssuer(String id, Issuer issuer) {
        Optional<Contractor> contractor = repository.findByIdAndContractsProductIssuerId(id, issuer.getId());
        return contractor.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(CONTRACTOR_NOT_FOUND.withOnlyArgument(id)));
    }

    public Contractor getByIdForIssuers(String id, Set<String> issuersIds) {
        Optional<Contractor> contractor = repository.findByIdAndContractsProductIssuerIdIn(id, issuersIds);
        return contractor.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(CONTRACTOR_NOT_FOUND.withOnlyArgument(id)));
    }

    public Contractor getByIdForNetwork(String id, AccreditedNetwork accreditedNetwork) {
        Optional<Contractor> contractor = repository.findByIdAndContractsProductAccreditedNetworkId(id, accreditedNetwork.getId());
        return contractor.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(CONTRACTOR_NOT_FOUND.withOnlyArgument(id)));
    }

    public Contractor getByIdForConctract(String id, Contract contract) {
        Optional<Contractor> contractor = repository.findByIdAndContractsId(id, contract.getId());
        return contractor.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(CONTRACTOR_NOT_FOUND.withOnlyArgument(id)));
    }

    public Contractor getByIdForHirer(String id, Hirer hirer) {
        Optional<Contractor> contractor = repository.findByIdAndContractsHirerId(id, hirer.getId());
        return contractor.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(CONTRACTOR_NOT_FOUND.withOnlyArgument(id)));
    }

    public Contractor getById(String id) {
        Optional<Contractor> contractor = repository.findById(id);
        return contractor.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(CONTRACTOR_NOT_FOUND.withOnlyArgument(id)));
    }

    public Contractor getByDocument(String document) {
        Optional<Contractor> contractor = repository.findByPersonDocumentNumber(document);
        return contractor.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(CONTRACTOR_NOT_FOUND.withOnlyArgument(document)));
    }

    public Optional<Contractor> getOptionalByDocument(String document) {
        return repository.findByPersonDocumentNumber(document);
    }

    public void updateForHirer(String id, Hirer hirer, Contractor contractor) {
        Contractor current = getByIdForHirer(id, hirer);
        update(contractor, current);
    }

    public void updateForIssuer(String id, Set<String> issuersIds, Contractor contractor) {
        Contractor current = getByIdForIssuers(id, issuersIds);
        update(contractor, current);
    }

    public void update(String id, Contractor contractor) {
        Contractor current = getById(id);
        update(contractor, current);
    }

    private void update(Contractor contractor, Contractor current) {
        current.updateModel(contractor);
        if(!current.withBankAccount() && contractor.withBankAccount()) {
            current.setBankAccount(bankAccountService.create(contractor.getBankAccount()));
        }
        personService.createOrUpdate(contractor.getPerson());
        repository.save(current);
    }

    public void delete(String id) {
        getById(id);
        if(hasUser(id)){
            throw UnovationExceptions.conflict().withErrors(CONTRACTOR_WITH_USERS);
        }
        repository.delete(id);
    }

    public List<Contractor> listForMenu() {
        ContractorFilter filter = new ContractorFilter();
        UnovationPageRequest pageable = new UnovationPageRequest();
        pageable.setSize(50);
        return findByFilter(filter, pageable).getContent();
    }

    private Boolean hasUser(String id) {
        return userDetailRepository.countByContractorId(id) > 0;
    }

    public Page<Contractor> findByFilter(ContractorFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }
    
}
