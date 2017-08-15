package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.EstablishmentEvent;
import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.repository.EstablishmentEventRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_EVENT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_EVENT;

@Service
public class EstablishmentEventService {

    private EstablishmentEventRepository repository;
    private EstablishmentService establishmentService;
    private EventService eventService;

    @Autowired
    public EstablishmentEventService(EstablishmentEventRepository repository,
                                     EstablishmentService establishmentService,
                                     EventService eventService) {
        this.repository = repository;
        this.establishmentService = establishmentService;
        this.eventService = eventService;
    }

    public EstablishmentEvent create(String establishmentId, EstablishmentEvent establishmentEvent) {
        setReferences(establishmentId, establishmentEvent);
        return repository.save(establishmentEvent);
    }

    private void setReferences(String establishmentId, EstablishmentEvent establishmentEvent) {
        Establishment establishment = establishmentService.findById(establishmentId);
        Event event = eventService.findById(establishmentEvent.getEvent().getId());
        establishmentEvent.setEvent(event);
        establishmentEvent.setEstablishment(establishment);
    }

    public void update(String establishmentId, EstablishmentEvent establishmentEvent) {
        EstablishmentEvent current = findById(establishmentEvent.getId());
        setReferences(establishmentId, establishmentEvent);
        checkOwner(establishmentId, establishmentEvent.getId());
        current.updateMe(establishmentEvent);
        repository.save(current);
    }

    private void checkOwner(String establishmentId, String id) {
        List<EstablishmentEvent> byEstablishment = findByEstablishmentId(establishmentId);
        if(byEstablishment.stream().noneMatch(event-> Objects.equals(event.getId(), id))){
            throw UnovationExceptions.unprocessableEntity().withErrors(ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_EVENT);
        }
    }

    public EstablishmentEvent findById(String id) {
        Optional<EstablishmentEvent> establishment = repository.findById(id);
        return establishment.orElseThrow(()->UnovationExceptions.notFound().withErrors(ESTABLISHMENT_EVENT_NOT_FOUND));
    }

    public EstablishmentEvent findByEstablishmentIdAndId(String establishmentId, String id) {
        Optional<EstablishmentEvent> establishment = repository.findByEstablishmentIdAndId(establishmentId, id);
        return establishment.orElseThrow(()->UnovationExceptions.notFound().withErrors(ESTABLISHMENT_EVENT_NOT_FOUND));
    }

    public List<EstablishmentEvent> findByEstablishmentId(String establishmentId) {
        return repository.findByEstablishmentId(establishmentId);
    }

    @Transactional
    public void deleteByEstablishmentIdAndId(String establishmentId, String id) {
        findByEstablishmentIdAndId(establishmentId, id);
        repository.deleteByEstablishmentIdAndId(establishmentId, id);
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

}