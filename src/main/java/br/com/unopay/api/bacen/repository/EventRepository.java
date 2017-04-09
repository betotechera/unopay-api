package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.model.filter.EventFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;


public interface EventRepository extends UnovationFilterRepository<Event,String, EventFilter> {

    int countByServiceId(String id);

    int countByName(String name);

    int countByNcmCode(String ncmCode);
}
