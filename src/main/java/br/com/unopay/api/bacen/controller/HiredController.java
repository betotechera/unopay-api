package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Hired;
import br.com.unopay.api.bacen.model.filter.HiredFilter;
import br.com.unopay.api.bacen.service.HiredService;
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
public class HiredController {

    private HiredService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public HiredController(HiredService service) {
        this.service = service;
     }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/hireds", method = RequestMethod.POST)
    public ResponseEntity<Hired> create(@Validated(Create.class) @RequestBody Hired hired) {
        log.info("creating hired {}", hired);
        Hired created = service.create(hired);
        return ResponseEntity
                .created(URI.create("/hireds/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/hireds/{id}", method = RequestMethod.GET)
    public Hired get(@PathVariable  String id) {
        log.info("get Hired={}", id);
        return service.getById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/hireds/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Hired hired) {
        hired.setId(id);
        log.info("updating hired {}", hired);
        service.update(id,hired);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/hireds/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing hired id={}", id);
        service.delete(id);
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/hireds", method = RequestMethod.GET)
    public Results<Hired> getByParams(HiredFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search Hired with filter={}", filter);
        Page<Hired> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/hireds", api));
    }

}
