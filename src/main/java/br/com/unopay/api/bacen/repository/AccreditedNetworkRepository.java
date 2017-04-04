package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.AccreditedNetworkFilter;
import br.com.unopay.api.repository.UnovationJpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccreditedNetworkRepository extends CrudRepository<AccreditedNetwork,String> , UnovationJpaSpecificationExecutor<AccreditedNetwork, AccreditedNetworkFilter> {
    AccreditedNetwork findById(String id);

    List<AccreditedNetwork> findByIdIn(List<String> ids);

    Long countByPaymentRuleGroupsId(String id);

}
