package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.PaymentRuleGroupFilter;
import br.com.unopay.api.repository.UnovationJpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRuleGroupRepository extends CrudRepository<PaymentRuleGroup,String> , UnovationJpaSpecificationExecutor<PaymentRuleGroup, PaymentRuleGroupFilter> {
    PaymentRuleGroup findById(String id);

    List<PaymentRuleGroup> findByIdIn(List<String> ids);
}
