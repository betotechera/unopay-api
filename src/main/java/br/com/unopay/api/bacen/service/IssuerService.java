package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.api.bacen.model.filter.IssuerFilter;
import br.com.unopay.api.bacen.repository.IssuerRepository;
import br.com.unopay.api.job.RemittanceJob;
import br.com.unopay.api.job.UnopayScheduler;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.ISSUER_NOT_FOUND;

@Slf4j
@Service
public class IssuerService {

    private IssuerRepository repository;
    private UserDetailService userDetailService;
    private PersonService personService;
    private BankAccountService bankAccountService;
    private PaymentBankAccountService paymentBankAccountService;
    private PaymentRuleGroupService paymentRuleGroupService;
    @Setter private UnopayScheduler scheduler;

    public IssuerService(){}

    @Autowired
    public IssuerService(IssuerRepository repository,
                         UserDetailService userDetailService,
                         PersonService personService,
                         BankAccountService bankAccountService,
                         PaymentBankAccountService paymentBankAccountService,
                         PaymentRuleGroupService paymentRuleGroupService,
                         UnopayScheduler scheduler) {
        this.repository = repository;
        this.userDetailService = userDetailService;
        this.personService = personService;
        this.bankAccountService = bankAccountService;
        this.paymentBankAccountService = paymentBankAccountService;
        this.paymentRuleGroupService = paymentRuleGroupService;
        this.scheduler = scheduler;
    }

    public Issuer create(Issuer issuer) {
        try {
        issuer.validate();
        issuer.setMeUp();
        createRequiredReferences(issuer);
        validateReferences(issuer);
        Issuer created = repository.save(issuer);
        scheduleClosingJob(created);
        return created;
        } catch (DataIntegrityViolationException e){
            log.warn(String.format("Person issuer already exists %s", issuer.getPerson()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.PERSON_ISSUER_ALREADY_EXISTS);

        }
    }

    public Issuer findById(String id) {
        Optional<Issuer> issuer = repository.findById(id);
        return  issuer.orElseThrow(()->UnovationExceptions.notFound().withErrors(ISSUER_NOT_FOUND));
    }

    public Issuer findByDocument(String documentNumber) {
        Optional<Issuer> issuer = repository.findByPersonDocumentNumber(documentNumber);
        return  issuer.orElseThrow(()->UnovationExceptions.notFound().withErrors(ISSUER_NOT_FOUND));
    }

    @Transactional
    public Issuer updateMe(String id, Issuer issuer) {
        issuer.setAuthorizeServiceWithoutContractorPassword(null);
        return update(id, issuer);
    }

    @Transactional
    public Issuer update(String id, Issuer issuer) {
        issuer.validate();
        Issuer current = findById(id);
        validateReferences(issuer);
        current.updateMe(issuer);
        personService.createOrUpdate(current.getPerson());
        bankAccountService.update(issuer.getMomentAccountId(),issuer.getMovementAccount());
        paymentBankAccountService.update(issuer.getPaymentAccountId(),issuer.getPaymentAccount());
        scheduleClosingJob(current);
        return repository.save(current);
    }

    private void validateReferences(Issuer issuer) {
        personService.findById(issuer.getPerson().getId());
        bankAccountService.findById(issuer.getMomentAccountId());
        paymentBankAccountService.findById(issuer.getPaymentAccountId());
        if(issuer.hasPaymentRuleGroup()){
            paymentRuleGroupService.findAll(issuer.getPaymentRuleGroupIds());
        }
    }

    public void delete(String id) {
        findById(id);
        if(userDetailService.hasIssuer(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.ISSUER_WITH_USERS);
        }
        repository.delete(id);
    }

    public Page<Issuer> findByFilter(IssuerFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public List<Issuer> listForMenu() {
        IssuerFilter filter = new IssuerFilter();
        UnovationPageRequest pageable = new UnovationPageRequest();
        pageable.setSize(50);
        return findByFilter(filter, pageable).getContent();
    }

    private void createRequiredReferences(Issuer issuer) {
        Person person = personService.createOrUpdate(issuer.getPerson());
        PaymentBankAccount paymentBankAccount = paymentBankAccountService.create(issuer.getPaymentAccount());
        bankAccountService.create(issuer.getMovementAccount());
        issuer.setPaymentAccount(paymentBankAccount);
        issuer.setPerson(person);
    }

    private void scheduleClosingJob(Issuer created) {
        scheduler.schedule(created.getId(), created.depositPeriodPattern(),RemittanceJob.class);
    }

}
