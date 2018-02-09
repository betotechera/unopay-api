package br.com.unopay.api.market.repository;

import br.com.unopay.api.market.model.NegotiationBillingDetail;
import br.com.unopay.api.market.model.filter.NegotiationBillingDetailFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;

public interface NegotiationBillingDetailRepository
        extends UnovationFilterRepository<NegotiationBillingDetail, String, NegotiationBillingDetailFilter>{
}
