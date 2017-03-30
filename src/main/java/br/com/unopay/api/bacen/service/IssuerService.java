package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.IssuerFilter;
import br.com.unopay.api.bacen.repository.IssuerRepository;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    public Page<Issuer> findByFilter(IssuerFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }
}
