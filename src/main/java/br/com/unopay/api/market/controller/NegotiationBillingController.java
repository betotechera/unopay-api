package br.com.unopay.api.market.controller;

import br.com.unopay.api.market.model.NegotiationBilling;
import br.com.unopay.api.market.model.filter.NegotiationBillingFilter;
import br.com.unopay.api.market.service.NegotiationBillingService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Slf4j
@RestController
@Timed(prefix = "api")
public class NegotiationBillingController {

    private NegotiationBillingService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public NegotiationBillingController(NegotiationBillingService service) {
        this.service = service;
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_HIRER_NEGOTIATION_BILLING')")
    @RequestMapping(value = "/hirer-negotiations/{id}/negotiation-billings", method = PUT)
    public void update(@PathVariable String id){
        log.info("updating negotiation={}", id);
        service.process(id);
    }

    @JsonView(Views.HirerNegotiation.Detail.class)
    @ResponseStatus(OK)
    @PreAuthorize("hasRole('ROLE_LIST_HIRER_NEGOTIATION_BILLING')")
    @RequestMapping(value = "/negotiation-billings/{id}", method = GET)
    public NegotiationBilling get(@PathVariable String id) {
        log.info("get negotiation={}", id);
        return service.findById(id);
    }

    @JsonView(Views.HirerNegotiation.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_HIRER_NEGOTIATION_BILLING')")
    @RequestMapping(value = "/negotiation-billings", method = RequestMethod.GET)
    public Results<NegotiationBilling> getByParams(NegotiationBillingFilter filter,
                                                 @Validated UnovationPageRequest pageable) {
        log.info("search negotiation with filter={}", filter);
        Page<NegotiationBilling> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/negotiation-billings", api));
    }
}
