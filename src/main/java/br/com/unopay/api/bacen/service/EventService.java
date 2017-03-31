package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.repository.EventRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.EVENT_NOT_FOUND;

@Service
public class EventService {

    @Autowired
    private EventRepository repository;

    public Event create(Event event) {
        return repository.save(event);
    }

    public void update(String id, Event event) {
        Event current = findById(id);
        current.setName(event.getName());
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
}
