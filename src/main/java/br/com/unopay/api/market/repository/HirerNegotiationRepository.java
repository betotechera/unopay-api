package br.com.unopay.api.market.repository;

import br.com.unopay.api.market.model.HirerNegotiation;
import br.com.unopay.api.market.model.filter.HirerNegotiationFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;

public interface HirerNegotiationRepository
        extends UnovationFilterRepository<HirerNegotiation, String, HirerNegotiationFilter> {

    Optional<HirerNegotiation> findByHirerPersonDocumentNumberAndProductId(String number, String productId);

    Optional<HirerNegotiation> findByHirerIdAndProductId(String hirerId, String productId);

    Optional<HirerNegotiation> findByIdAndHirerId(String id, String hirerId);

    Optional<HirerNegotiation> findByHirerId(String hirerId);

    Optional<HirerNegotiation> findByIdAndProductIssuerId(String id, String issuerId);
}
