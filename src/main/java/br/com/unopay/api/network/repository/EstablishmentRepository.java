package br.com.unopay.api.network.repository;

import br.com.unopay.api.network.model.Establishment;
import br.com.unopay.api.network.model.filter.EstablishmentFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;

public interface EstablishmentRepository
        extends UnovationFilterRepository<Establishment, String, EstablishmentFilter> {

    Optional<Establishment> findById(String id);
    Optional<Establishment> findByIdAndNetworkId(String id, String networkId);
    Optional<Establishment> findByPersonDocumentNumber(String document);

}
