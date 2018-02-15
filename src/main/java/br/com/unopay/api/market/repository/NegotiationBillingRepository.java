package br.com.unopay.api.market.repository;

import br.com.unopay.api.market.model.NegotiationBilling;
import br.com.unopay.api.market.model.filter.NegotiationBillingFilter;
import br.com.unopay.api.order.model.PaymentStatus;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface NegotiationBillingRepository
        extends UnovationFilterRepository<NegotiationBilling, String, NegotiationBillingFilter>{

    Optional<NegotiationBilling> findFirstByHirerNegotiationHirerIdAndStatusInOrderByCreatedDateTimeDesc(
            String hirerId, List<PaymentStatus> status);

    Optional<NegotiationBilling> findById(String id);

    Set<NegotiationBilling> findByHirerNegotiationHirerId(String hirerId);

    Optional<NegotiationBilling> findByIdAndHirerNegotiationProductIssuerId(String id, String issuerId);


}
