package br.com.unopay.api.network.controller;

import br.com.unopay.api.network.model.Event;
import br.com.unopay.api.network.model.filter.EventFilter;
import br.com.unopay.api.network.service.EventService;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
public class EventController {

    @Value("${unopay.api}")
    private String api;

    private EventService service;

    @Autowired
    public EventController(EventService service) {
        this.service = service;
    }

    @JsonView(Views.Event.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_EVENT')")
    @RequestMapping(value = "/events", method = RequestMethod.POST)
    public ResponseEntity<Event> create(@Validated(Create.class) @RequestBody Event event) {
        log.info("creating event {}", event);
        Event created = service.create(event);
        return ResponseEntity
                .created(URI.create("/events/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Event.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_EVENT') || hasRole('ROLE_MANAGE_SERVICE_AUTHORIZE') ")
    @RequestMapping(value = "/events/{id}", method = RequestMethod.GET)
    public Event get(@PathVariable String id) {
        log.info("get event={}", id);
        return service.findById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_EVENT')")
    @RequestMapping(value = "/events/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Event event) {
        event.setId(id);
        log.info("updating event {}", event);
        service.update(id,event);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_EVENT')")
    @RequestMapping(value = "/events/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing event id={}", id);
        service.delete(id);
    }

    @JsonView(Views.Event.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public Results<Event> getByParams(EventFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search event with filter={}", filter);
        Page<Event> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/events", api));
    }

}
