package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.filter.InstitutionFilter;
import br.com.unopay.api.bacen.repository.InstitutionRepository;
import br.com.unopay.api.bacen.repository.PaymentRuleGroupRepository;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.CANNOT_INVOKE_TYPE;

@Slf4j
@Service
public class InstitutionService {
    private InstitutionRepository repository;
    private PersonService personService;
    private UserDetailService userDetailService;
    private PaymentRuleGroupRepository paymentRuleGroupRepository;

    @Autowired
    public InstitutionService(InstitutionRepository repository, PersonService personService,
                              UserDetailService userDetailService,
                              PaymentRuleGroupRepository paymentRuleGroupRepository) {
        this.repository = repository;
        this.personService = personService;
        this.userDetailService = userDetailService;
        this.paymentRuleGroupRepository = paymentRuleGroupRepository;
    }

    public Institution create(Institution institution) {
        try {
            personService.save(institution.getPerson());
            return repository.save(institution);
        } catch (DataIntegrityViolationException e){
            log.warn(String.format("Person institution already exists %s", institution.getPerson()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.PERSON_INSTITUTION_ALREADY_EXISTS);

        }
    }

    public Institution getById(String id) {
        Optional<Institution> institution = repository.findById(id);
        return institution.orElseThrow(()->UnovationExceptions.notFound().withErrors(Errors.INSTITUTION_NOT_FOUND));
    }

    public void update(String id, Institution institution) {
        Institution current = repository.findOne(id);
        current.updateModel(institution);
        personService.save(institution.getPerson());
        repository.save(current);
    }

    public void delete(String id) {
        getById(id);
        if(userDetailService.hasInstitution(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.INSTITUTION_WITH_USERS);
        }
        if(hasPaymentRuleGroup(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.INSTITUTION_WITH_PAYMENT_RULE_GROUPS);
        }
        repository.delete(id);
    }

    public Page<Institution> findByFilter(InstitutionFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }


    private boolean hasPaymentRuleGroup(String id) {
        return paymentRuleGroupRepository.countByInstitutionId(id) > 0;
    }

}
