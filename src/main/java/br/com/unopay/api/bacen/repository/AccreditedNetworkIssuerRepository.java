package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.AccreditedNetworkIssuer;
import br.com.unopay.api.bacen.model.filter.AccreditedNetworkIssuerFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;

public interface AccreditedNetworkIssuerRepository extends
        UnovationFilterRepository<AccreditedNetworkIssuer, String, AccreditedNetworkIssuerFilter>{
}
