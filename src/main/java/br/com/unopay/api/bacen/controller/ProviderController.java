package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Provider;
import br.com.unopay.api.bacen.service.ProviderService;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@Timed(prefix = "api")
public class ProviderController {

    @Value("${unopay.api}")
    private String api;

    @Autowired
    private ProviderService service;

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/providers", method = RequestMethod.POST)
    public ResponseEntity<Provider> create(@Validated(Create.class) @RequestBody Provider provider) {
        log.info("creating bank account {}", provider);
        Provider created = service.create(provider);
        return ResponseEntity
                .created(URI.create("/providers/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/providers/{id}", method = RequestMethod.GET)
    public Provider get(@PathVariable String id) {
        log.info("get bank account={}", id);
        return service.findById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/providers/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Provider provider) {
        provider.setId(id);
        log.info("updating bank account {}", provider);
        service.update(id,provider);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/providers/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing bank account id={}", id);
        service.delete(id);
    }

}
