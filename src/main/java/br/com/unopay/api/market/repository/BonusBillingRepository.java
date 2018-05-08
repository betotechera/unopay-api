package br.com.unopay.api.market.repository;

import br.com.unopay.api.market.model.BonusBilling;
import br.com.unopay.api.market.model.filter.BonusBillingFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;

import java.util.Optional;

public interface BonusBillingRepository extends UnovationFilterRepository<BonusBilling, String, BonusBillingFilter> {
    Optional<BonusBilling> findById(String id);
}
