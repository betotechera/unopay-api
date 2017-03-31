package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    @Autowired
    private EventRepository repository;

    public Event create(Event event) {
        return repository.save(event);
    }
}
