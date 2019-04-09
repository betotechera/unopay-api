package br.com.unopay.api.network.repository;

import br.com.unopay.api.network.model.Event;
import br.com.unopay.api.network.model.ServiceType;
import br.com.unopay.api.network.model.filter.EventFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;


public interface EventRepository extends UnovationFilterRepository<Event,String, EventFilter> {

    Optional<Event> findById(String id);

    int countByServiceId(String id);

    int countByName(String name);

    int countByNcmCode(String ncmCode);

    Optional<Event> findByIdAndServiceType(String id, ServiceType serviceType);

    Optional<Event> findByNcmCode(String code);
}
