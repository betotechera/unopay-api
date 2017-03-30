package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.repository.IssuerRepository;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.ISSUER_NOT_FOUND;

@Service
public class IssuerService {

    @Autowired
    private IssuerRepository repository;

    @Autowired
    private PersonService personService;

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private PaymentRuleGroupService paymentRuleGroupService;

    public Issuer create(Issuer issuer) {
        issuer.validate();
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
        validateReferences(issuer);
        Issuer current = findById(id);
        current.updateMe(issuer);
        return  repository.save(current);
    }

    private void validateReferences(Issuer issuer) {
        personService.findById(issuer.getPerson().getId());
        bankAccountService.findAll(issuer.getAccountsIds());
        if(issuer.hasPaymentRuleGroup()){
            paymentRuleGroupService.findAll(issuer.getPaymentRuleGroupIds());
        }
    }
}
