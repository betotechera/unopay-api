package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.IssuerFilter;
import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.bacen.repository.IssuerRepository;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static br.com.unopay.api.uaa.exception.Errors.ISSUER_NOT_FOUND;

@Service
public class IssuerService {

    @Autowired
    private IssuerRepository repository;

    @Autowired
    private PersonService personService;

    @Autowired
    private BankAccountService bankAccountService;

    private PaymentBankAccountService paymentBankAccountService;

    private PaymentRuleGroupService paymentRuleGroupService;

    @Autowired
    public IssuerService(IssuerRepository repository, PersonService personService, BankAccountService bankAccountService,
                         PaymentBankAccountService paymentBankAccountService,
                         PaymentRuleGroupService paymentRuleGroupService) {
        this.repository = repository;
        this.personService = personService;
        this.bankAccountService = bankAccountService;
        this.paymentBankAccountService = paymentBankAccountService;
        this.paymentRuleGroupService = paymentRuleGroupService;
    }

    public Issuer create(Issuer issuer) {
        issuer.validate();
        createRequiredReferences(issuer);
        validateReferences(issuer);
        return repository.save(issuer);
    }

    public Issuer findById(String id) {
        Issuer issuer = repository.findOne(id);
        if(issuer == null) throw UnovationExceptions.notFound().withErrors(ISSUER_NOT_FOUND);
        return  issuer;
    }

    public Issuer update(String id, Issuer issuer) {
        issuer.validate();
        Issuer current = findById(id);
        validateReferences(issuer);
        current.updateMe(issuer);
        return  repository.save(current);
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
        repository.delete(id);
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
}
