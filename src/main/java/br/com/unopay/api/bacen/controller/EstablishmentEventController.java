package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.EstablishmentEvent;
import br.com.unopay.api.bacen.service.EstablishmentEventService;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
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
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@PreAuthorize("#oauth2.isUser()")
@Timed(prefix = "api")
public class EstablishmentEventController {

    private EstablishmentEventService service;
    private UserDetailService userDetailService;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public EstablishmentEventController(EstablishmentEventService service,
                                        UserDetailService userDetailService) {
        this.service = service;
        this.userDetailService = userDetailService;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/establishments/{id}/event-fees", method = RequestMethod.POST,
            consumes = "multipart/form-data")
    public void createFromCsvById(@PathVariable  String id, @RequestParam MultipartFile file){
        String fileName = file.getOriginalFilename();
        log.info("reading establishment event fee csv file {}", fileName);
        service.createFromCsv(id, file);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/establishments/event-fees", method = RequestMethod.POST,
            consumes = "multipart/form-data")
    public void createFromCsv(@RequestParam MultipartFile file){
        String fileName = file.getOriginalFilename();
        log.info("reading establishment event fee csv file {}", fileName);
        service.createFromCsv(null, file);
    }

    @JsonView({Views.EstablishmentEvent.Detail.class})
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_ALL_ESTABLISHMENT_EVENT_VALUE') ")
    @RequestMapping(value = "/establishments/{id}/event-fees", method = RequestMethod.POST)
    public ResponseEntity<EstablishmentEvent> create(@PathVariable  String id, @Validated(Create.class)
                                                @RequestBody EstablishmentEvent establishment) {
        log.info("creating establishment event{}", establishment);

        EstablishmentEvent created = service.create(id, establishment);
        return ResponseEntity
                .created(URI.create("/establishments/"+id+"/event-fees"+created.getId()))
                .body(created);

    }

    @JsonView({Views.EstablishmentEvent.Detail.class})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_ALL_ESTABLISHMENT_EVENT_VALUE')")
    @RequestMapping(value = "/establishments/{establishmentId}/event-fees/{id}", method = RequestMethod.GET)
    public EstablishmentEvent get(@PathVariable  String establishmentId, @PathVariable  String id) {
        log.info("get establishment event={}", id);
        return service.findByEstablishmentIdAndId(establishmentId, id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_ALL_ESTABLISHMENT_EVENT_VALUE') ")
    @RequestMapping(value = "/establishments/{establishmentId}/event-fees/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String establishmentId, @PathVariable  String id,
                       @Validated(Update.class) @RequestBody EstablishmentEvent establishment) {
        establishment.setId(id);
        log.info("updating establishment event {}", establishment);
        service.update(establishmentId,establishment);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_ALL_ESTABLISHMENT_EVENT_VALUE') ")
    @RequestMapping(value = "/establishments/{establishmentId}/event-fees/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String establishmentId, @PathVariable  String id) {
        log.info("removing establishment event id={}", id);
        service.deleteByEstablishmentIdAndId(establishmentId, id);
    }

    @JsonView({Views.EstablishmentEvent.List.class})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_ALL_ESTABLISHMENT_EVENT_VALUE')")
    @RequestMapping(value = "/establishments/{id}/event-fees", method = RequestMethod.GET)
    public Results<EstablishmentEvent> getByParams(@PathVariable  String id) {
        log.info("find establishment events of establishment={}", id);
        List<EstablishmentEvent> page =  service.findByEstablishmentId(id);
        return new Results<>(page);
    }

    @JsonView({Views.EstablishmentEvent.Detail.class})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/establishments/me/event-fees", method = RequestMethod.POST)
    public ResponseEntity<EstablishmentEvent> createMy(Establishment establishment, @Validated(Create.class)
    @RequestBody EstablishmentEvent event) {
        log.info("creating event event{}", event);
        EstablishmentEvent created = service.create(establishment.getId(), event);
        return ResponseEntity
                .created(URI.create("/establishments/"+establishment.getId()+"/event-fees"+created.getId()))
                .body(created);

    }
    @JsonView({Views.EstablishmentEvent.Detail.class})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments/me/event-fees/{id}", method = RequestMethod.GET)
    public EstablishmentEvent getMy(Establishment establishment, @PathVariable  String id) {
        log.info("get establishment event={}", id);
        return service.findByEstablishmentIdAndId(establishment.getId(), id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/establishments/me/event-fees/{id}", method = RequestMethod.PUT)
    public void updateMy(Establishment establishment, @PathVariable  String id,
                       @Validated(Update.class) @RequestBody EstablishmentEvent event) {
        event.setId(id);
        log.info("updating establishment event={}", event);
        service.update(establishment.getId(),event);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/establishments/me/event-fees/{id}", method = RequestMethod.DELETE)
    public void removeMy(Establishment establishment, @PathVariable  String id) {
        log.info("removing establishment event id={}", id);
        service.deleteByEstablishmentIdAndId(establishment.getId(), id);
    }

    @JsonView({Views.EstablishmentEvent.List.class})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments/me/event-fees", method = RequestMethod.GET)
    public Results<EstablishmentEvent> getMyByParams(Establishment establishment) {
        log.info("find establishment events of establishment={}", establishment.documentNumber());
        List<EstablishmentEvent> page =  service.findByEstablishmentId(establishment.getId());
        return new Results<>(page);
    }

}
