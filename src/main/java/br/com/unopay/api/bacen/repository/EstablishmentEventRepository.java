package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.EstablishmentEvent;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface EstablishmentEventRepository
        extends CrudRepository<EstablishmentEvent, String> {

    Optional<EstablishmentEvent> findById(String id);

    int countByEstablishmentId(String establishmentId);

}
