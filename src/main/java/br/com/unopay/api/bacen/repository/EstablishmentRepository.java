package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.filter.EstablishmentFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.Optional;

public interface EstablishmentRepository
        extends UnovationFilterRepository<Establishment, String, EstablishmentFilter> {

    Optional<Establishment> findById(String id);
    Optional<Establishment> findByPersonNameIgnoreCaseLike(String name);

}
