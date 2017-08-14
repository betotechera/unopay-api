package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.EstablishmentEvent;
import br.com.unopay.api.bacen.repository.EstablishmentEventRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_EVENT_NOT_FOUND;

@Service
public class EstablishmentEventService {

    private EstablishmentEventRepository repository;

    @Autowired
    public EstablishmentEventService(EstablishmentEventRepository repository) {
        this.repository = repository;
    }

    public EstablishmentEvent create(EstablishmentEvent establishment) {
        return repository.save(establishment);
    }

    public void update(String id, EstablishmentEvent establishment) {
        EstablishmentEvent current = findById(id);
        current.updateMe(establishment);
        repository.save(current);
    }

    public EstablishmentEvent findById(String id) {
        Optional<EstablishmentEvent> establishment = repository.findById(id);
        return establishment.orElseThrow(()->UnovationExceptions.notFound().withErrors(ESTABLISHMENT_EVENT_NOT_FOUND));
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

}
