package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.HirerNegotiation;
import br.com.unopay.api.bacen.model.filter.HirerNegotiationFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;

public interface HirerNegotiationRepository
        extends UnovationFilterRepository<HirerNegotiation, String, HirerNegotiationFilter> {

    Optional<HirerNegotiation> findByHirerPersonDocumentNumberAndProductId(String number, String productId);
}
