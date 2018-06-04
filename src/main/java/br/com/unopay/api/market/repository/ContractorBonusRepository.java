package br.com.unopay.api.market.repository;

import br.com.unopay.api.market.model.ContractorBonus;
import br.com.unopay.api.market.model.filter.ContractorBonusFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;

public interface ContractorBonusRepository
        extends UnovationFilterRepository<ContractorBonus, String, ContractorBonusFilter> {

    Optional<ContractorBonus> findById(String id);

    Optional<ContractorBonus> findByIdAndContractorId(String id, String contractorId);

    Optional<ContractorBonus> findByIdAndPayerId(String id, String payerId);

}
