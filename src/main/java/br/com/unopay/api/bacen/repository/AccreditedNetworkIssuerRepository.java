package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.AccreditedNetworkIssuer;
import br.com.unopay.api.bacen.model.filter.AccreditedNetworkIssuerFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;

public interface AccreditedNetworkIssuerRepository extends
        UnovationFilterRepository<AccreditedNetworkIssuer, String, AccreditedNetworkIssuerFilter>{

    Optional<AccreditedNetworkIssuer> findByIssuerIdAndAccreditedNetworkId(String issuerId, String networkId);
}
