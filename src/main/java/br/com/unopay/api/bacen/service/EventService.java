package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.repository.EventRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.xml.stream.EventFilter;

import static br.com.unopay.api.uaa.exception.Errors.EVENT_NOT_FOUND;

@Service
public class EventService {

    @Autowired
    private EventRepository repository;

    @Autowired
    private ProviderService providerService;

    public Event create(Event event) {
        event.validate();
        providerService.findById(event.getProviderId());
        return repository.save(event);
    }

    public void update(String id, Event event) {
        Event current = findById(id);
        event.validate();
        providerService.findById(event.getProviderId());
        current.updateMe(event);
        repository.save(current);

    }

    public Event findById(String id) {
        Event event = repository.findOne(id);
        if(event == null) throw UnovationExceptions.notFound().withErrors(EVENT_NOT_FOUND);
        return event;
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    public Page<Event> findByFilter(EventFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }
}
