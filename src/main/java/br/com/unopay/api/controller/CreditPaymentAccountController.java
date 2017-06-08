package br.com.unopay.api.controller;

import br.com.unopay.api.model.CreditPaymentAccount;
import br.com.unopay.api.service.CreditPaymentAccountService;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
