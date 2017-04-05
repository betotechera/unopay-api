package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.PaymentRuleGroupFilter;
import br.com.unopay.api.repository.UnovationFilterRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentRuleGroupRepository extends UnovationFilterRepository<PaymentRuleGroup,String, PaymentRuleGroupFilter> {
    PaymentRuleGroup findById(String id);

    List<PaymentRuleGroup> findByIdIn(List<String> ids);

    Long countByInstitutionId(String id);

}
