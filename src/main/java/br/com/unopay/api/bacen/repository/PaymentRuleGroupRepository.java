package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.Purpose;
import br.com.unopay.api.bacen.model.Scope;
import br.com.unopay.api.bacen.model.UserRelationship;
import br.com.unopay.api.bacen.model.filter.PaymentRuleGroupFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRuleGroupRepository extends
        UnovationFilterRepository<PaymentRuleGroup,String, PaymentRuleGroupFilter> {
    Optional<PaymentRuleGroup> findByCode(String code);

    Optional<PaymentRuleGroup> findById(String id);

    List<PaymentRuleGroup> findByIdIn(List<String> ids);

    Long countByInstitutionId(String id);

    Long countByInstitutionAndPurposeAndScopeAndUserRelationship
            (Institution institution, Purpose purpose, Scope scope, UserRelationship userRelationship);
}
