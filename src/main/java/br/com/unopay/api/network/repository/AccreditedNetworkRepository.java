package br.com.unopay.api.network.repository;

import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.network.model.filter.AccreditedNetworkFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface AccreditedNetworkRepository extends
        UnovationFilterRepository<AccreditedNetwork,String, AccreditedNetworkFilter> {

    Optional<AccreditedNetwork> findById(String id);

    List<AccreditedNetwork> findByIdIn(List<String> ids);

    Long countByPaymentRuleGroupsId(String id);

}
