package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Service;
import br.com.unopay.api.bacen.model.ServiceFilter;
import br.com.unopay.api.bacen.service.ServiceService;
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
public class ServiceController {

    @Value("${unopay.api}")
    private String api;

    @Autowired
    private ServiceService service;

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/services", method = RequestMethod.POST)
    public ResponseEntity<Service> create(@Validated(Create.class) @RequestBody Service service) {
        log.info("creating bank account {}", service);
        Service created = this.service.create(service);
        return ResponseEntity
                .created(URI.create("/services/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/services/{id}", method = RequestMethod.GET)
    public Service get(@PathVariable String id) {
        log.info("get bank account={}", id);
        return service.findById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/services/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Service service) {
        service.setId(id);
        log.info("updating bank account {}", service);
        this.service.update(id, service);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/services/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing bank account id={}", id);
        service.delete(id);
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/services", method = RequestMethod.GET)
    public Results<Service> getByParams(ServiceFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search Service by filter with filter={}", filter);
        Page<Service> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/services", api));
    }

}
