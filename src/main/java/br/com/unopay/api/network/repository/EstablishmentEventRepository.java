package br.com.unopay.api.network.repository;

import br.com.unopay.api.network.model.EstablishmentEvent;
import br.com.unopay.api.network.model.filter.EstablishmentEventFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;

public interface EstablishmentEventRepository
        extends UnovationFilterRepository<EstablishmentEvent, String, EstablishmentEventFilter > {

    Optional<EstablishmentEvent> findById(String id);
    Optional<EstablishmentEvent> findFirstByEventIdAndEstablishmentIdOrderByExpirationDesc(String eventId, String establishmentId);
    Optional<EstablishmentEvent> findByEstablishmentIdAndId(String establishmentId, String id);
    Optional<EstablishmentEvent> findByEstablishmentNetworkIdAndId(String networkId, String id);
    List<EstablishmentEvent> findByEstablishmentId(String establishmentId);
    List<EstablishmentEvent> findByEstablishmentPersonDocumentNumber(String document);

    int countByEstablishmentId(String establishmentId);

    void deleteByEstablishmentIdAndId(String establishmentId, String id);

}
