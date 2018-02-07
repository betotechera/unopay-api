package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.HirerNegotiation;
import br.com.unopay.api.bacen.model.filter.HirerNegotiationFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;

public interface HirerNegotiationRepository
        extends UnovationFilterRepository<HirerNegotiation, String, HirerNegotiationFilter> {
}
