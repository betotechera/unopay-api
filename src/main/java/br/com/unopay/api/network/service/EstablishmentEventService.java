package br.com.unopay.api.network.service;

import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.network.model.Establishment;
import br.com.unopay.api.network.model.EstablishmentEvent;
import br.com.unopay.api.network.model.Event;
import br.com.unopay.api.bacen.model.csv.EstablishmentEventFeeCsv;
import br.com.unopay.api.network.model.filter.EstablishmentEventFilter;
import br.com.unopay.api.network.model.filter.EstablishmentFilter;
import br.com.unopay.api.network.repository.EstablishmentEventRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_EVENT_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_EVENT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_EVENT;

@Service
public class EstablishmentEventService {

    private EstablishmentEventRepository repository;
    private EstablishmentService establishmentService;
    private EventService eventService;

    private static final char SEMICOLON = ';';

    @Autowired
    public EstablishmentEventService(EstablishmentEventRepository repository,
                                     EstablishmentService establishmentService,
                                     EventService eventService) {
        this.repository = repository;
        this.establishmentService = establishmentService;
        this.eventService = eventService;
    }
    public EstablishmentEvent create(String establishmentId, EstablishmentEvent establishmentEvent, AccreditedNetwork accreditedNetwork) {
        establishmentService.findByIdAndNetworks(establishmentId, accreditedNetwork);
        return create(establishmentId,establishmentEvent);
    }

    public EstablishmentEvent create(String establishmentId, EstablishmentEvent establishmentEvent) {
        checkEstablishmentEventAlreadyExists(establishmentEvent);
        setReferences(establishmentId, establishmentEvent);
        return repository.save(establishmentEvent);
    }

    private void setReferences(String establishmentId, EstablishmentEvent establishmentEvent) {
        Establishment establishment = establishmentService.findById(establishmentId);
        Event event = eventService.findById(establishmentEvent.getEvent().getId());
        establishmentEvent.setEvent(event);
        establishmentEvent.setEstablishment(establishment);
    }

    public void update(String establishmentEventId, EstablishmentEvent establishmentEvent, AccreditedNetwork accreditedNetwork) {
        findByNetworkIdAndId(establishmentEventId, accreditedNetwork);
        establishmentService.findByIdAndNetworks(establishmentEvent.establishmentId(), accreditedNetwork);
        update(establishmentEvent.establishmentId(), establishmentEvent);
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

    public EstablishmentEvent findByNetworkIdAndId(String id, AccreditedNetwork accreditedNetwork) {
        Optional<EstablishmentEvent> establishment = repository.findByEstablishmentNetworkIdAndId(accreditedNetwork.getId(), id);
        return establishment.orElseThrow(()->UnovationExceptions.notFound().withErrors(ESTABLISHMENT_EVENT_NOT_FOUND));
    }

    public EstablishmentEvent findByEstablishmentIdAndId(String establishmentId, String id) {
        Optional<EstablishmentEvent> establishment = repository.findByEstablishmentIdAndId(establishmentId, id);
        return establishment.orElseThrow(()->UnovationExceptions.notFound().withErrors(ESTABLISHMENT_EVENT_NOT_FOUND.withOnlyArguments(id)));
    }

    public EstablishmentEvent findByEventIdAndEstablishmentId(String eventId, String establishmentId) {
        Optional<EstablishmentEvent> establishment = repository.findByEventIdAndEstablishmentId(eventId, establishmentId);
        return establishment.orElseThrow(()->UnovationExceptions.notFound().withErrors(ESTABLISHMENT_EVENT_NOT_FOUND));
    }

    public List<EstablishmentEvent> findByEstablishmentId(String establishmentId) {
        return repository.findByEstablishmentId(establishmentId);
    }

    public Page<EstablishmentEvent> findByFilter(EstablishmentEventFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public List<EstablishmentEvent> findByEstablishmentDocument(String document) {
        return repository.findByEstablishmentPersonDocumentNumber(document);
    }

    @Transactional
    public void delete(String id, AccreditedNetwork accreditedNetwork) {
        findByNetworkIdAndId(id, accreditedNetwork);
        repository.delete(id);
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

    @SneakyThrows
    @Transactional
    public void createFromCsv(String id, MultipartFile multipartFile, AccreditedNetwork accreditedNetwork) {
        List<EstablishmentEventFeeCsv> csvLines = getEstablishmentEventFeeCsvs(multipartFile);
        Optional<Establishment> establishmentOptional = establishmentService.findByIdAndNetworksOptional(id, accreditedNetwork);
        csvLines.forEach(csvLine ->  {
            Establishment establishment = establishmentOptional.orElseGet(() ->
                    establishmentService.findByDocumentNumberAndNetwork(csvLine.getEstablishmentDocument(), accreditedNetwork));
            createEventFee(csvLine, establishment);
        });
    }

    @SneakyThrows
    @Transactional
    public void createFromCsv(String id, MultipartFile multipartFile) {
        List<EstablishmentEventFeeCsv> csvLines = getEstablishmentEventFeeCsvs(multipartFile);
        Optional<Establishment> establishmentOptional = establishmentService.findByIdOptional(id);
        csvLines.forEach(csvLine ->  {
            Establishment establishment = establishmentOptional.orElseGet(() ->
                    establishmentService.findByDocumentNumber(csvLine.getEstablishmentDocument()));
            createEventFee(csvLine, establishment);
        });
    }

    private void createEventFee(EstablishmentEventFeeCsv csvLine, Establishment establishment) {
        Event event = eventService.findByNcmCode(csvLine.getEventName());
        EstablishmentEvent establishmentEvent = csvLine.toEstablishmentEventFee(event);
        create(establishment.getId(), establishmentEvent);
    }

    private List<EstablishmentEventFeeCsv> getEstablishmentEventFeeCsvs(MultipartFile multipartFile) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(multipartFile.getInputStream());
        return new CsvToBeanBuilder<EstablishmentEventFeeCsv>(inputStreamReader)
                .withType(EstablishmentEventFeeCsv.class).withSeparator(SEMICOLON).build().parse();
    }

    private void checkEstablishmentEventAlreadyExists(EstablishmentEvent establishmentEvent){
        if(repository.countByEventIdAndEstablishmentId(establishmentEvent.getEvent().getId(), establishmentEvent.getEstablishment().getId()) > 0)
            throw UnovationExceptions.conflict().withErrors(ESTABLISHMENT_EVENT_ALREADY_EXISTS);
    }


}
