package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.filter.IssuerFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;

import java.util.Optional;

public interface IssuerRepository extends UnovationFilterRepository<Issuer, String, IssuerFilter> {

    Optional<Issuer> findById(String id);
    Long countByPaymentRuleGroupsId(String id);

}
