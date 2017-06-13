package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.bacen.model.filter.EventFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.Optional;


public interface EventRepository extends UnovationFilterRepository<Event,String, EventFilter> {

    Optional<Event> findById(String id);

    int countByServiceId(String id);

    int countByName(String name);

    int countByNcmCode(String ncmCode);

    Optional<Event> findByIdAndServiceType(String id, ServiceType serviceType);
}
