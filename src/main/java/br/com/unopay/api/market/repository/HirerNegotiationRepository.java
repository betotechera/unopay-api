package br.com.unopay.api.market.repository;

import br.com.unopay.api.market.model.HirerNegotiation;
import br.com.unopay.api.market.model.filter.HirerNegotiationFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

public interface HirerNegotiationRepository
        extends UnovationFilterRepository<HirerNegotiation, String, HirerNegotiationFilter> {

    Optional<HirerNegotiation> findByHirerPersonDocumentNumberAndProductId(String number, String productId);

    Optional<HirerNegotiation> findByHirerIdAndProductId(String hirerId, String productId);

    Optional<HirerNegotiation> findByIdAndHirerId(String id, String hirerId);

    Optional<HirerNegotiation> findById(String id);

    Integer countByHirerId(String id);

    Optional<HirerNegotiation> findByIdAndProductIssuerId(String id, String issuerId);

    Optional<HirerNegotiation> findByHirerIdAndProductIdAndActiveTrue(String hirerId, String productId);

    Set<HirerNegotiation> findByPaymentDayAndEffectiveDateBefore(Integer paymentDay, Date effectiveDate);
}
