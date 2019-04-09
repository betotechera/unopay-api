package br.com.unopay.api.network.repository;

import br.com.unopay.api.network.model.Branch;
import br.com.unopay.api.network.model.filter.BranchFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;

public interface BranchRepository extends UnovationFilterRepository<Branch, String, BranchFilter> {

    int countByHeadOfficeId(String headOfficeId);

     Optional<Branch> findById(String id);

    Optional<Branch> findByIdAndHeadOfficeNetworkId(String id, String networkId);

}
