package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.IssuerFilter;
import br.com.unopay.api.bacen.service.IssuerService;
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
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@Timed(prefix = "api")
public class IssuerController {

    private IssuerService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public IssuerController(IssuerService service) {
        this.service = service;
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/issuers", method = RequestMethod.POST)
    public ResponseEntity<Issuer> create(@Validated(Create.class) @RequestBody Issuer issuer) {
        log.info("creating issuer {}", issuer);
        Issuer created = service.create(issuer);
        return ResponseEntity
                .created(URI.create("/issuers/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/{id}", method = RequestMethod.GET)
    public Issuer get(@PathVariable  String id) {
        log.info("get issuer={}", id);
        return service.findById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/issuers/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Issuer institution) {
        institution.setId(id);
        log.info("updating issuers {}", institution);
        service.update(id,institution);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/issuers/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing payment rule groups id={}", id);
        service.delete(id);
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers", method = RequestMethod.GET)
    public Results<Issuer> getByParams(IssuerFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search issuer with filter={}", filter);
        Page<Issuer> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/issuers", api));
    }
}
