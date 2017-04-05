package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Service;
import br.com.unopay.api.bacen.model.ServiceFilter;
import br.com.unopay.api.bacen.repository.ServiceRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static br.com.unopay.api.uaa.exception.Errors.SERVICE_NOT_FOUND;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository repository;

    public Service create(Service service) {
        return repository.save(service);
    }

    public void update(String id, Service event) {
        Service current = findById(id);
        current.setName(event.getName());
        repository.save(current);

    }

    public Service findById(String id) {
        Service service = repository.findOne(id);
        if(service == null) throw UnovationExceptions.notFound().withErrors(SERVICE_NOT_FOUND);
        return service;
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    public Page<Service> findByFilter(ServiceFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }
}
