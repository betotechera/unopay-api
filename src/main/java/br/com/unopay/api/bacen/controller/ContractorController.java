package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.api.bacen.service.ContractorService;
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
import org.springframework.web.bind.annotation.*; // NOSONAR

import java.net.URI;

@Slf4j
@RestController
@Timed(prefix = "api")
public class ContractorController {

    private ContractorService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public ContractorController(ContractorService service) {
        this.service = service;
     }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/contractors", method = RequestMethod.POST)
    public ResponseEntity<Contractor> create(@Validated(Create.class) @RequestBody Contractor contractor) {
        log.info("creating contractor {}", contractor);
        Contractor created = service.create(contractor);
        return ResponseEntity
                .created(URI.create("/contractors/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/{id}", method = RequestMethod.GET)
    public Contractor get(@PathVariable  String id) {
        log.info("get Contractor={}", id);
        return service.getById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/contractors/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Contractor contractor) {
        contractor.setId(id);
        log.info("updating contractor {}", contractor);
        service.update(id, contractor);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/contractors/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing hired id={}", id);
        service.delete(id);
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors", method = RequestMethod.GET)
    public Results<Contractor> getByParams(ContractorFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search Contractor with filter={}", filter);
        Page<Contractor> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors", api));
    }

}
