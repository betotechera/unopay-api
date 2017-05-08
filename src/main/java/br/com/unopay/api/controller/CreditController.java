package br.com.unopay.api.controller;

import br.com.unopay.api.model.Credit;
import br.com.unopay.api.service.CreditService;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.ResponseEntity.created;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@Timed(prefix = "api")
public class CreditController {

    private CreditService service;

    @Autowired
    public CreditController(CreditService service) {
        this.service = service;
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_CREDIT')")
    @RequestMapping(value = "/hirers/{document}/credits", method = POST)
    public ResponseEntity<Credit> create(@PathVariable String document,
                                         @Validated(Create.class) @RequestBody Credit credit) {
        log.info("inserting credit={}", credit);
        credit.setHirerDocument(document);
        Credit created = service.insert(credit);
        log.info("Inserted credit={}", created);
        return created(URI.create(String.format("/hirers/%s/credits/%s",document, created.getId()))).body(created);

    }

    @ResponseStatus(OK)
    @JsonView(Views.Public.class)
    @PreAuthorize("hasRole('ROLE_LIST_CREDIT')")
    @RequestMapping(value = "/hirers/{document}/credits/{id}", method = GET)
    public Credit get(@PathVariable String id) {
        log.info("get contract={}", id);
        return service.findById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_CONTRACT')")
    @RequestMapping(value = "/hirers/{document}/credits/{id}", method = RequestMethod.DELETE)
    public void cancel(@PathVariable  String id) {
        log.info("canceling credit id={}", id);
        service.cancel(id);
    }
}
