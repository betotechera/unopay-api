package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.filter.HirerFilter;
import br.com.unopay.api.bacen.repository.HirerRepository;
import br.com.unopay.api.market.repository.HirerNegotiationRepository;
import br.com.unopay.api.model.filter.ProductFilter;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.HIRER_DOCUMENT_NOT_FOUND;

@Timed
@Slf4j
@Service
public class HirerService {
    private HirerRepository repository;
    private PersonService personService;
    private UserDetailService userDetailService;
    private BankAccountService bankAccountService;
    private HirerNegotiationRepository hirerNegotiationRepository;

    @Autowired
    public HirerService(HirerRepository repository,
                        PersonService personService,
                        UserDetailService userDetailService,
                        BankAccountService bankAccountService,
                        HirerNegotiationRepository hirerNegotiationRepository) {
        this.repository = repository;
        this.personService = personService;
        this.userDetailService = userDetailService;
        this.bankAccountService = bankAccountService;
        this.hirerNegotiationRepository = hirerNegotiationRepository;
    }

    public Hirer create(Hirer hirer) {
        try {
            bankAccountService.create(hirer.getBankAccount());
            personService.createOrUpdate(hirer.getPerson());
            return repository.save(hirer);
        } catch (DataIntegrityViolationException e){
            log.warn(String.format("Person hirer already exists %s", hirer.getPerson()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.PERSON_HIRER_ALREADY_EXISTS);

        }
    }

    public Hirer getByIdForIssuer(String id, Issuer issuer) {
        Optional<Hirer> hirer = repository.findByIdAndNegotiationsProductIssuerId(id, issuer.getId());
        return hirer.orElseThrow(()->UnovationExceptions.notFound().withErrors(Errors.HIRER_NOT_FOUND));
    }

    public Hirer getById(String id) {
        Optional<Hirer> hirer = repository.findById(id);
        return hirer.orElseThrow(()->UnovationExceptions.notFound().withErrors(Errors.HIRER_NOT_FOUND));
    }

    public void updateForIssuer(String id, Issuer issuer, Hirer hirer) {
        Hirer current = getByIdForIssuer(id, issuer);
        update(hirer, current);
    }

    public void update(String id, Hirer hirer) {
        Hirer current = getById(id);
        update(hirer, current);
    }

    private void update(Hirer hirer, Hirer current) {
        current.updateModel(hirer);
        personService.createOrUpdate(hirer.getPerson());
        repository.save(current);
    }

    public void delete(String id) {
        getById(id);
        if(userDetailService.hasHirer(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.HIRER_WITH_USERS.withOnlyArgument(id));
        }
        if(hasNegotiation(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.HIRER_WITH_NEGOTIATION.withOnlyArgument(id));
        }
        repository.delete(id);
    }

    public List<Hirer> listForMenu() {
        HirerFilter filter = new HirerFilter();
        UnovationPageRequest pageable = new UnovationPageRequest();
        pageable.setSize(50);
        return findByFilter(filter, pageable).getContent();
    }

    public boolean hasNegotiation(String id) {
        return hirerNegotiationRepository.countByHirerId(id) > 0;
    }

    public boolean hasNegotiationForTheProduct(String id, String productId) {
        return hirerNegotiationRepository.countByHirerIdAndProductId(id, productId) > 0;
    }

    public Hirer findByDocumentNumber(String documentNumber){
        Optional<Hirer> hirer = getByDocumentNumber(documentNumber);
        return hirer.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(HIRER_DOCUMENT_NOT_FOUND.withOnlyArgument(documentNumber)));
    }

    public Optional<Hirer> getByDocumentNumber(String documentNumber) {
        return repository.findByPersonDocumentNumber(documentNumber);
    }

    public Page<Hirer> findByFilter(HirerFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

}
