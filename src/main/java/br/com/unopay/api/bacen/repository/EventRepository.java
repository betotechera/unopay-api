package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.repository.UnovationFilterRepository;

import javax.xml.stream.EventFilter;

public interface EventRepository extends UnovationFilterRepository<Event,String, EventFilter> {
}
