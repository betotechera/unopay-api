package br.com.unopay.api.controller;

import br.com.unopay.api.model.Credit;
import br.com.unopay.api.model.CreditPaymentAccount;
import br.com.unopay.api.model.filter.CreditFilter;
import br.com.unopay.api.service.CreditPaymentAccountService;
import br.com.unopay.api.service.CreditService;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
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
import java.util.List;

@Slf4j
@RestController
@Timed(prefix = "api")
public class CreditPaymentAccountController {

    private CreditPaymentAccountService service;

    @Autowired
    public CreditPaymentAccountController(CreditPaymentAccountService service) {
        this.service = service;
    }

    @ResponseStatus(OK)
    @JsonView(Views.Public.class)
    @PreAuthorize("hasRole('ROLE_LIST_CREDIT')")
    @RequestMapping(value = "/hirers/{document}/credit-payment-accounts/{id}", method = GET)
    public CreditPaymentAccount get(@PathVariable String id) {
        log.info("get CreditPaymentAccount={}", id);
        return service.findById(id);
    }

    @ResponseStatus(OK)
    @JsonView(Views.List.class)
    @PreAuthorize("hasRole('ROLE_LIST_CREDIT')")
    @RequestMapping(value = "/hirers/{document}/credit-payment-accounts", method = GET)
    public Results<CreditPaymentAccount> findByHirerDocument(@PathVariable String document) {
        log.info("search CreditPaymentAccount with document={}", document);
        List<CreditPaymentAccount> accounts =  service.findByHirerDocument(document);
        return new Results<>(accounts);
    }

}
