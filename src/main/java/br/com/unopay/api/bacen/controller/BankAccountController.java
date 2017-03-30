package br.com.unopay.api.bacen.controller;


import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.InstitutionFilter;
import br.com.unopay.api.bacen.service.BankAccountService;
import br.com.unopay.api.bacen.service.InstitutionService;
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
public class BankAccountController {

    @Value("${unopay.api}")
    private String api;

    @Autowired
    private BankAccountService service;

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/bankAccounts", method = RequestMethod.POST)
    public ResponseEntity<BankAccount> create(@Validated(Create.class) @RequestBody BankAccount institution) {
        log.info("creating bank account {}", institution);
        BankAccount created = service.create(institution);
        return ResponseEntity
                .created(URI.create("/bankAccounts/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/bankAccounts/{id}", method = RequestMethod.GET)
    public BankAccount get(@PathVariable  String id) {
        log.info("get bank account={}", id);
        return service.findBydId(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/bankAccounts/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody BankAccount institution) {
        institution.setId(id);
        log.info("updating bank account {}", institution);
        service.update(id,institution);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/bankAccounts/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing bank account id={}", id);
        service.delete(id);
    }

}
