package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.filter.EstablishmentFilter;
import br.com.unopay.api.bacen.service.EstablishmentService;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@Timed(prefix = "api")
public class EstablishmentController {

    private EstablishmentService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public EstablishmentController(EstablishmentService service) {
        this.service = service;
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/establishments", method = RequestMethod.POST)
    public ResponseEntity<Establishment> create(@Validated(Create.class) @RequestBody Establishment establishment) {
        log.info("creating establishment {}", establishment);
        Establishment created = service.create(establishment);
        return ResponseEntity
                .created(URI.create("/establishments/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments/{id}", method = RequestMethod.GET)
    public Establishment get(@PathVariable  String id) {
        log.info("get establishment={}", id);
        return service.findById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/establishments/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Establishment institution) {
        institution.setId(id);
        log.info("updating establishments {}", institution);
        service.update(id,institution);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/establishments/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing payment rule groups id={}", id);
        service.delete(id);
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments", method = RequestMethod.GET)
    public Results<Establishment> getByParams(EstablishmentFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search establishment with filter={}", filter);
        Page<Establishment> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/establishments", api));
    }
}
