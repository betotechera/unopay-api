package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Service;
import br.com.unopay.api.bacen.model.ServiceFilter;
import br.com.unopay.api.bacen.repository.EventRepository;
import br.com.unopay.api.bacen.repository.ServiceRepository;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationError;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static br.com.unopay.api.uaa.exception.Errors.SERVICE_NOT_FOUND;

@org.springframework.stereotype.Service
public class ServiceService {

    private ServiceRepository repository;

    private EventRepository eventRepository;

    @Autowired
    public ServiceService(ServiceRepository repository, EventRepository eventRepository) {
        this.repository = repository;
        this.eventRepository = eventRepository;
    }

    public Service create(Service service) {
        service.validate();
        return repository.save(service);
    }

    public void update(String id, Service service) {
        service.validate();
        Service current = findById(id);
        current.updateModel(service);
        repository.save(current);

    }

    public Service findById(String id) {
        Service service = repository.findOne(id);
        if(service == null) throw UnovationExceptions.notFound().withErrors(SERVICE_NOT_FOUND);
        return service;
    }

    public void delete(String id) {
        findById(id);
        if(hasEvents(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.SERVICE_WITH_EVENTS);
        }
        repository.delete(id);
    }

    private boolean hasEvents(String id) {
     return eventRepository.countByServiceId(id) > 0;
    }

    public Page<Service> findByFilter(ServiceFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }
}
