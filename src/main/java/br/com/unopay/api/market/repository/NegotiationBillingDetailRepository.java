package br.com.unopay.api.market.repository;

import br.com.unopay.api.market.model.NegotiationBillingDetail;
import br.com.unopay.api.market.model.filter.NegotiationBillingDetailFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Set;

public interface NegotiationBillingDetailRepository
        extends UnovationFilterRepository<NegotiationBillingDetail, String, NegotiationBillingDetailFilter>{

    Set<NegotiationBillingDetail> findByNegotiationBillingId(String billingId);
}
