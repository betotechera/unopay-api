package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.PaymentRuleGroupFilter;
import br.com.unopay.api.bacen.repository.PaymentRuleGroupRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_RULE_GROUP_CODE_ALREADY_EXISTS;

@Slf4j
@Service
public class PaymentRuleGroupService {

    private PaymentRuleGroupRepository repository;

    @Autowired
    public PaymentRuleGroupService(PaymentRuleGroupRepository repository) {
        this.repository = repository;
    }

    public PaymentRuleGroup create(PaymentRuleGroup paymentRuleGroup) {
        try {
          return repository.save(paymentRuleGroup);
        } catch (DataIntegrityViolationException e) {
            log.warn(String.format("PaymentRuleGroup code already exists %s", paymentRuleGroup.toString()), e);
            throw UnovationExceptions.conflict().withErrors(PAYMENT_RULE_GROUP_CODE_ALREADY_EXISTS).withArguments(paymentRuleGroup.getCode());
        }
    }

    public Page<PaymentRuleGroup> findByFilter(PaymentRuleGroupFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public PaymentRuleGroup getById(String id) {
        PaymentRuleGroup paymentRuleGroup = repository.findOne(id);
        if (paymentRuleGroup == null) {
            throw UnovationExceptions.notFound();
        }
        return paymentRuleGroup;
    }

    public void update(PaymentRuleGroup paymentRuleGroup) {
        PaymentRuleGroup current = repository.findOne(paymentRuleGroup.getId());
        current.updateModel(paymentRuleGroup);

        try {
            repository.save(current);
        } catch (DataIntegrityViolationException e) {
            log.warn(String.format("PaymentRuleGroup code already exists %s", current.toString()), e);
            throw UnovationExceptions.conflict().withErrors(PAYMENT_RULE_GROUP_CODE_ALREADY_EXISTS).withArguments(current.getCode());
        }
    }

    public void delete(String id) {
        getById(id);
        repository.delete(id);
    }
}
