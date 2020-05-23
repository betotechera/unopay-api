package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.filter.HirerFilter;
import br.com.unopay.api.bacen.model.filter.PaymentRuleGroupFilter;
import br.com.unopay.api.network.repository.AccreditedNetworkRepository;
import br.com.unopay.api.bacen.repository.IssuerRepository;
import br.com.unopay.api.bacen.repository.PaymentRuleGroupRepository;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_RULE_GROUP_CODE_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_RULE_GROUP_NOT_FOUND;

@Slf4j
@Service
public class PaymentRuleGroupService {

    private PaymentRuleGroupRepository repository;

    private IssuerRepository issuerRepository;

    private AccreditedNetworkRepository accreditedNetworkRepository;

    @Autowired
    public PaymentRuleGroupService(PaymentRuleGroupRepository repository,
                                   IssuerRepository issuerRepository,
                                   AccreditedNetworkRepository accreditedNetworkRepository) {
        this.repository = repository;
        this.issuerRepository = issuerRepository;
        this.accreditedNetworkRepository = accreditedNetworkRepository;
    }

    public PaymentRuleGroup createForInstitution(PaymentRuleGroup paymentRuleGroup, Institution institution) {
        paymentRuleGroup.setInstitution(institution);
        return create(paymentRuleGroup);
    }

    public PaymentRuleGroup create(PaymentRuleGroup paymentRuleGroup) {
        try {
            paymentRuleGroup.validate();
            checkIfAnPaymentRuleGroupWithTheSameValuesAlreadyExists(paymentRuleGroup);
          return repository.save(paymentRuleGroup);
        } catch (DataIntegrityViolationException e) {
            log.warn(String.format("PaymentRuleGroup code already exists %s", paymentRuleGroup.toString()), e);
            throw UnovationExceptions.conflict().withErrors(PAYMENT_RULE_GROUP_CODE_ALREADY_EXISTS)
                    .withArguments(paymentRuleGroup.getCode());
        }
    }

    private void checkIfAnPaymentRuleGroupWithTheSameValuesAlreadyExists(PaymentRuleGroup paymentRuleGroup) {
        if(paymentRuleGroupAlreadyExists(paymentRuleGroup)) {
            throw UnovationExceptions.conflict().withErrors(Errors.PAYMENT_RULE_GROUP_ALREADY_EXISTS)
                    .withArguments(paymentRuleGroup.getCode());
        }
    }

    private Boolean paymentRuleGroupAlreadyExists(PaymentRuleGroup paymentRuleGroup) {
        return repository.
                countByInstitutionAndPurposeAndScopeAndUserRelationship
                (paymentRuleGroup.getInstitution(),paymentRuleGroup.getPurpose(),paymentRuleGroup.getScope(),
                                paymentRuleGroup.getUserRelationship()) > 0;
    }

    public Page<PaymentRuleGroup> findByFilterForInstitution(Institution institution,
                                                             PaymentRuleGroupFilter filter,
                                                             UnovationPageRequest pageable) {
        filter.setInstitution(institution.documentNumber());
        return findByFilter(filter, pageable);
    }

    public Page<PaymentRuleGroup> findByFilter(PaymentRuleGroupFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public PaymentRuleGroup getById(String id) {
        Optional<PaymentRuleGroup> paymentRuleGroup = repository.findById(id);
        return paymentRuleGroup
                .orElseThrow(()->UnovationExceptions.notFound().withErrors(PAYMENT_RULE_GROUP_NOT_FOUND));
    }

    public PaymentRuleGroup getByCode(String code) {
        Optional<PaymentRuleGroup> paymentRuleGroup = repository.findByCode(code);
        return paymentRuleGroup
                .orElseThrow(() -> UnovationExceptions.notFound().withErrors(PAYMENT_RULE_GROUP_NOT_FOUND));
    }

    public List<PaymentRuleGroup> findAll(List<String> ids){
        List<PaymentRuleGroup> paymentRuleGroups = repository.findByIdIn(ids);
        List<String> founds = paymentRuleGroups.stream().map(PaymentRuleGroup::getId).collect(Collectors.toList());
        List<String> notFounds = ids.stream().filter(id -> !founds.contains(id)).collect(Collectors.toList());
        if(!notFounds.isEmpty()) {
            throw UnovationExceptions.notFound().withErrors(PAYMENT_RULE_GROUP_NOT_FOUND
                    .withOnlyArguments(notFounds));
        }
        return  paymentRuleGroups;
    }

    @Transactional
    public void updateForInstitution(String id, Institution institution, PaymentRuleGroup paymentRuleGroup) {
        PaymentRuleGroup current = getForInstitutionById(id, institution);
        current.updateMe(paymentRuleGroup);
        repository.save(current);
    }

    public PaymentRuleGroup getForInstitutionById(String id, Institution institution) {
        Optional<PaymentRuleGroup> paymentRuleGroup = repository.findByIdAndInstitutionId(id, institution.getId());
        return paymentRuleGroup.orElseThrow(()-> UnovationExceptions.notFound().withErrors(PAYMENT_RULE_GROUP_NOT_FOUND));
    }

    public void update(String id, PaymentRuleGroup paymentRuleGroup) {
        PaymentRuleGroup current = repository.findOne(id);
        current.updateMe(paymentRuleGroup);
        try {
            repository.save(current);
        } catch (DataIntegrityViolationException e) {
            log.warn(String.format("PaymentRuleGroup code already exists %s", current.toString()), e);
            throw UnovationExceptions.conflict().withErrors(PAYMENT_RULE_GROUP_CODE_ALREADY_EXISTS)
                    .withArguments(current.getCode());
        }
    }

    public void deleteForInstitution(String id, Institution institution) {
        getForInstitutionById(id, institution);
        deleteIfPossible(id);
    }

    public void delete(String id) {
        getById(id);
        deleteIfPossible(id);
    }

    public List<PaymentRuleGroup> listForMenu() {
        PaymentRuleGroupFilter filter = new PaymentRuleGroupFilter();
        UnovationPageRequest pageable = new UnovationPageRequest();
        pageable.setSize(50);
        return findByFilter(filter, pageable).getContent();
    }

    private void deleteIfPossible(String id) {
        if(hasIssuer(id)) {
            throw UnovationExceptions.conflict().withErrors(Errors.PAYMENT_RULE_GROUP_IN_ISSUER);
        }
        if(hasAccreditedNetwork(id)) {
            throw UnovationExceptions.conflict().withErrors(Errors.PAYMENT_RULE_GROUP_IN_ACCREDITED_NETWORK);
        }
        repository.delete(id);
    }

    private boolean hasAccreditedNetwork(String id) {
        return accreditedNetworkRepository.countByPaymentRuleGroupsId(id) > 0;
    }

    private boolean hasIssuer(String id) {
        return issuerRepository.countByPaymentRuleGroupsId(id) > 0;
    }


}
