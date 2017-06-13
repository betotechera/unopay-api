package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Service;
import br.com.unopay.api.bacen.model.filter.ServiceFilter;
import br.com.unopay.api.bacen.repository.EventRepository;
import br.com.unopay.api.bacen.repository.ServiceRepository;
import br.com.unopay.api.uaa.exception.Errors;
import static br.com.unopay.api.uaa.exception.Errors.SERVICE_NOT_FOUND;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
        validateFields(service);
        return repository.save(service);
    }
    private void validateFields(Service service) {
        validateName(service.getName());
        validateCode(service.getCode());
    }
    private void validateCode(Integer code) {
        if(alreadyHasCode(code)){
            throw UnovationExceptions.conflict().withErrors(Errors.SERVICE_CODE_ALREADY_EXISTS);
        }
    }

    private void validateName(String name) {
        if(alreadyHasName(name)) {
            throw UnovationExceptions.conflict().withErrors(Errors.SERVICE_NAME_ALREADY_EXISTS);
        }
    }
    private boolean alreadyHasCode(Integer code) {
        return repository.countByCode(code) > 0;
    }
    private boolean alreadyHasName(String name) {
        return repository.countByName(name) > 0;
    }



    public void update(String id, Service service) {
        service.validate();
        Service current = findById(id);
        if(!current.getName().equals(service.getName())) {
            validateName(service.getName());
        }
        if(!current.getCode().equals(service.getCode())) {
            validateCode(service.getCode());
        }
        current.updateModel(service);
        repository.save(current);

    }

    public Service findById(String id) {
        Optional<Service> service = repository.findById(id);
        return service.orElseThrow(()->UnovationExceptions.notFound().withErrors(SERVICE_NOT_FOUND));
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
