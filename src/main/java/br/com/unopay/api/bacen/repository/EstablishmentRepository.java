package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.filter.EstablishmentFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;

public interface EstablishmentRepository
        extends UnovationFilterRepository<Establishment, String, EstablishmentFilter> {

    Optional<Establishment> findById(String id);
    Optional<Establishment> findByIdAndNetworkId(String id, String networkId);
    Optional<Establishment> findByPersonDocumentNumber(String document);

}
