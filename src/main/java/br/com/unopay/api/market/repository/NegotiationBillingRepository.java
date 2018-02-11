package br.com.unopay.api.market.repository;

import br.com.unopay.api.market.model.NegotiationBilling;
import br.com.unopay.api.market.model.filter.NegotiationBillingFilter;
import br.com.unopay.api.order.model.PaymentStatus;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;

public interface NegotiationBillingRepository
        extends UnovationFilterRepository<NegotiationBilling, String, NegotiationBillingFilter>{

    Optional<NegotiationBilling> findFirstByHirerNegotiationHirerIdAndStatusInOrderByCreatedDateTimeDesc(
            String hirerId, List<PaymentStatus> status);


}
