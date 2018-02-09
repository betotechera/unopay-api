package br.com.unopay.api.market.repository;

import br.com.unopay.api.market.model.NegotiationBilling;
import br.com.unopay.api.market.model.filter.NegotiationBillingFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;

public interface NegotiationBillingRepository
        extends UnovationFilterRepository<NegotiationBilling, String, NegotiationBillingFilter>{
}
