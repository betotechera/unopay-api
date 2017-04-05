package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.IssuerFilter;
import br.com.unopay.api.repository.UnovationFilterRepository;
import org.springframework.data.repository.CrudRepository;

public interface IssuerRepository extends UnovationFilterRepository<Issuer, String, IssuerFilter> {

    Long countByPaymentRuleGroupsId(String id);

}
