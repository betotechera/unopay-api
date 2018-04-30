package br.com.unopay.api.market.repository;

import br.com.unopay.api.market.model.ContractorBonus;
import br.com.unopay.api.market.model.filter.ContractorBonusFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;

public interface ContractorBonusRepository
        extends UnovationFilterRepository<ContractorBonus, String, ContractorBonusFilter> {
}
