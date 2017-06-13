package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.filter.HirerFilter;
import br.com.unopay.api.bacen.service.HirerService;
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
public class HirerController {

    private HirerService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public HirerController(HirerService service) {
        this.service = service;
     }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/hirers", method = RequestMethod.POST)
    public ResponseEntity<Hirer> create(@Validated(Create.class) @RequestBody Hirer hirer) {
        log.info("creating hirer {}", hirer);
        Hirer created = service.create(hirer);
        return ResponseEntity
                .created(URI.create("/hirers/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/hirers/{id}", method = RequestMethod.GET)
    public Hirer get(@PathVariable  String id) {
        log.info("get Hirer={}", id);
        return service.getById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/hirers/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Hirer hirer) {
        hirer.setId(id);
        log.info("updating hirer {}", hirer);
        service.update(id,hirer);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/hirers/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing hirer id={}", id);
        service.delete(id);
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/hirers", method = RequestMethod.GET)
    public Results<Hirer> getByParams(HirerFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search Hirer with filter={}", filter);
        Page<Hirer> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/hirers", api));
    }

}
