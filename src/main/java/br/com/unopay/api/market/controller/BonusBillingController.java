package br.com.unopay.api.market.controller;

import br.com.unopay.api.market.model.BonusBilling;
import br.com.unopay.api.market.model.filter.BonusBillingFilter;
import br.com.unopay.api.market.service.BonusBillingService;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.http.ResponseEntity.created;

@Slf4j
@RestController
@Timed(prefix = "api")
public class BonusBillingController {

    private BonusBillingService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public BonusBillingController(BonusBillingService service) {
        this.service = service;
    }


    @JsonView(Views.BonusBilling.Detail.class)
    @ResponseStatus(CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_BONUS_BILLING')")
    @RequestMapping(value = "/bonus-billings", method = POST)
    public ResponseEntity<BonusBilling> create(@Validated(Create.class) @RequestBody BonusBilling bonusBilling){
        log.info("creating bonus billing={}", bonusBilling);
        BonusBilling created = service.create(bonusBilling);
        log.info("created bonus billing={}", created);
        return created(URI.create(String
                .format("/bonus-billings/%s",created.getId()))).body(created);
    }

    @JsonView(Views.BonusBilling.Detail.class)
    @ResponseStatus(OK)
    @PreAuthorize("hasRole('ROLE_LIST_BONUS_BILLING')")
    @RequestMapping(value = "/bonus-billings/{id}", method = GET)
    public BonusBilling get(@PathVariable String id) {
        log.info("get bonus billing={}", id);
        return service.findById(id);
    }

    @JsonView(Views.BonusBilling.Detail.class)
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_BONUS_BILLING')")
    @RequestMapping(value = "/bonus-billings/{id}", method = DELETE)
    public void delete(@PathVariable String id) {
        log.info("get bonus billing={}", id);
        service.delete(id);
    }

    @JsonView(Views.BonusBilling.List.class)
    @ResponseStatus(OK)
    @PreAuthorize("hasRole('ROLE_LIST_BONUS_BILLING')")
    @RequestMapping(value = "/bonus-billings", method = RequestMethod.GET)
    public Results<BonusBilling> getByParams(BonusBillingFilter filter,
                                             @Validated UnovationPageRequest pageable) {
        log.info("search bonus billing with filter={}", filter);
        Page<BonusBilling> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/bonus-billings", api));
    }
}
