package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.filter.AccreditedNetworkFilter;
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
