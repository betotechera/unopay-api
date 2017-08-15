package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.EstablishmentEvent;
import br.com.unopay.api.bacen.service.EstablishmentEventService;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed(prefix = "api")
public class EstablishmentEventController {

    private EstablishmentEventService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public EstablishmentEventController(EstablishmentEventService service) {
        this.service = service;
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_EVENT_VALUE') ")
    @RequestMapping(value = "/establishments/{id}/events", method = RequestMethod.POST)
    public ResponseEntity<EstablishmentEvent> create(@PathVariable  String id, @Validated(Create.class)
                                                @RequestBody EstablishmentEvent establishment) {
        log.info("creating establishment event{}", establishment);

        EstablishmentEvent created = service.create(id, establishment);
        return ResponseEntity
                .created(URI.create("/establishments/"+id+"/events"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_EVENT_VALUE')")
    @RequestMapping(value = "/establishments/{establishmentId}/events/{id}", method = RequestMethod.GET)
    public EstablishmentEvent get(@PathVariable  String establishmentId, @PathVariable  String id) {
        log.info("get establishment event={}", id);
        return service.findByEstablishmentIdAndId(establishmentId, id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_EVENT_VALUE') ")
    @RequestMapping(value = "/establishments/{establishmentId}/events/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String establishmentId, @PathVariable  String id,
                       @Validated(Update.class) @RequestBody EstablishmentEvent establishment) {
        establishment.setId(id);
        log.info("updating establishment event {}", establishment);
        service.update(establishmentId,establishment);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_EVENT_VALUE') ")
    @RequestMapping(value = "/establishments/{establishmentId}/events/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String establishmentId, @PathVariable  String id) {
        log.info("removing establishment event id={}", id);
        service.deleteByEstablishmentIdAndId(establishmentId, id);
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_EVENT_VALUE')")
    @RequestMapping(value = "/establishments/{id}/events", method = RequestMethod.GET)
    public Results<EstablishmentEvent> getByParams(@PathVariable  String id) {
        log.info("find establishment events of establishment={}", id);
        List<EstablishmentEvent> page =  service.findByEstablishmentId(id);
        return new Results<>(page);
    }
}
