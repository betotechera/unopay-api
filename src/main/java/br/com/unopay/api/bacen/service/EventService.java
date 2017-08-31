package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.bacen.model.filter.EventFilter;
import br.com.unopay.api.bacen.repository.EventRepository;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.EVENT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.FUEL_EVENT_NOT_FOUND;

@Service
public class EventService {

    private EventRepository repository;

    private ServiceService serviceService;

    @Autowired
    public EventService(EventRepository repository, ServiceService serviceService) {
        this.repository = repository;
        this.serviceService = serviceService;
    }

    public Event create(Event event) {
        event.validate();
        validateFields(event);
        serviceService.findById(event.getProviderId());
        return repository.save(event);
    }

    private boolean alreadyHasCode(String ncmCode) {
        return repository.countByNcmCode(ncmCode) > 0;
    }

    private boolean alreadyHasName(String name) {
        return repository.countByName(name) > 0;
    }

    public void update(String id, Event event) {
        Event current = findById(id);
        event.validate();
        if(!current.getName().equals(event.getName())) {
            validateName(event.getName());
        }
        if(!current.getNcmCode().equals(event.getNcmCode())) {
            validateCode(event.getNcmCode());
        }
        serviceService.findById(event.getProviderId());
        current.updateMe(event);
         repository.save(current);
    }

    private void validateFields(Event event) {
        validateName(event.getName());
        validateCode(event.getNcmCode());
    }

    private void validateCode(String code) {
        if(alreadyHasCode(code)) {
            throw UnovationExceptions.conflict().withErrors(Errors.EVENT_CODE_ALREADY_EXISTS);
        }
    }

    private void validateName(String name) {
        if(alreadyHasName(name)) {
            throw UnovationExceptions.conflict().withErrors(Errors.EVENT_NAME_ALREADY_EXISTS);
        }
    }

    public Event findById(String id) {
        Optional<Event> event = repository.findById(id);
        return event.orElseThrow(()->UnovationExceptions.notFound().withErrors(EVENT_NOT_FOUND));
    }

    public Event findByNcmCode(String code) {
        Optional<Event> event = repository.findByNcmCode(code);
        return event.orElseThrow(()->UnovationExceptions.notFound().withErrors(EVENT_NOT_FOUND));
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    public Page<Event> findByFilter(EventFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public Event findByIdAndServiceType(String id, ServiceType serviceType) {
        Optional<Event> event = repository.findByIdAndServiceType(id, serviceType);
        return event.orElseThrow(()->UnovationExceptions.notFound().withErrors(FUEL_EVENT_NOT_FOUND));
    }


}
