package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.EstablishmentEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface EstablishmentEventRepository
        extends CrudRepository<EstablishmentEvent, String> {

    Optional<EstablishmentEvent> findById(String id);
    Optional<EstablishmentEvent> findByEstablishmentIdAndId(String establishmentId, String id);
    List<EstablishmentEvent> findByEstablishmentId(String establishmentId);
    List<EstablishmentEvent> findByEstablishmentPersonDocumentNumber(String document);

    int countByEstablishmentId(String establishmentId);

    void deleteByEstablishmentIdAndId(String establishmentId, String id);

}
