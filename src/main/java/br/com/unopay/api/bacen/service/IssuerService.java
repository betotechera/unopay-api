package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.bacen.model.filter.IssuerFilter;
import br.com.unopay.api.bacen.repository.IssuerRepository;
import br.com.unopay.api.job.RemittanceJob;
import br.com.unopay.api.job.UnopayScheduler;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.billing.remittance.model.filter.RemittanceFilter;
import br.com.unopay.api.billing.remittance.service.PaymentRemittanceService;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.ISSUER_NOT_FOUND;

@Slf4j
@Service
public class IssuerService {

    private IssuerRepository repository;
    private UserDetailRepository userDetailRepository;
    private PersonService personService;
    private BankAccountService bankAccountService;
    private PaymentBankAccountService paymentBankAccountService;
    private PaymentRuleGroupService paymentRuleGroupService;
    @Setter private UnopayScheduler scheduler;
    @Setter private PaymentRemittanceService paymentRemittanceService;

    public IssuerService(){}

    @Autowired
    public IssuerService(IssuerRepository repository,
                         UserDetailRepository userDetailRepository,
                         PersonService personService,
                         BankAccountService bankAccountService,
                         PaymentBankAccountService paymentBankAccountService,
                         PaymentRuleGroupService paymentRuleGroupService,
                         UnopayScheduler scheduler, @Lazy PaymentRemittanceService paymentRemittanceService) {
        this.repository = repository;
        this.userDetailRepository = userDetailRepository;
        this.personService = personService;
        this.bankAccountService = bankAccountService;
        this.paymentBankAccountService = paymentBankAccountService;
        this.paymentRuleGroupService = paymentRuleGroupService;
        this.scheduler = scheduler;
        this.paymentRemittanceService = paymentRemittanceService;
    }

    public Issuer create(Issuer issuer) {
        try {
        issuer.validate();
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

    @Transactional
    public Issuer update(String id, Issuer issuer) {
        issuer.validate();
        Issuer current = findById(id);
        validateReferences(issuer);
        current.updateMe(issuer);
        personService.save(current.getPerson());
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
        if(hasUsers(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.ISSUER_WITH_USERS);
        }
        repository.delete(id);
    }

    private boolean hasUsers(String id) {
        return  userDetailRepository.countByIssuerId(id) > 0;
    }

    public Page<Issuer> findByFilter(IssuerFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private void createRequiredReferences(Issuer issuer) {
        Person person = personService.save(issuer.getPerson());
        PaymentBankAccount paymentBankAccount = paymentBankAccountService.create(issuer.getPaymentAccount());
        bankAccountService.create(issuer.getMovementAccount());
        issuer.setPaymentAccount(paymentBankAccount);
        issuer.setPerson(person);
    }

    private void scheduleClosingJob(Issuer created) {
        scheduler.schedule(created.getId(), created.depositPeriodPattern(),RemittanceJob.class);
    }

    public void executePaymentRemittance(RemittanceFilter filter) {
        paymentRemittanceService.execute(filter);
    }

}
