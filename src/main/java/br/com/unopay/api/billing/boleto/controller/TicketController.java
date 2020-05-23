package br.com.unopay.api.billing.boleto.controller;

import br.com.unopay.api.billing.boleto.model.Ticket;
import br.com.unopay.api.billing.boleto.model.filter.TicketFilter;
import br.com.unopay.api.billing.boleto.service.TicketService;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
public class TicketController {

    @Value("${unopay.api}")
    private String api;


    @Autowired
    private TicketService service;

    @JsonView(Views.Ticket.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_BOLETOS')")
    @RequestMapping(value = "/tickets", method = GET)
    public Results<Ticket> findBoletos(TicketFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("find tickets  with filter={}", filter);
        return getTicketResults(filter, pageable);
    }

    @ResponseStatus(OK)
    @JsonView(Views.Ticket.Detail.class)
    @PreAuthorize("hasRole('ROLE_LIST_BOLETOS')")
    @RequestMapping(value = "/tickets/{id}", method = GET)
    public Ticket get(@PathVariable String id) {
        log.info("get ticket={}", id);
        return service.findById(id);
    }

    @Deprecated
    @JsonView(Views.Ticket.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/tickets", method = GET, params = "orderNumber")
    public Results<Ticket> findBoletosByOrderNumberOnly(TicketFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("find tickets  with filter={}", filter);
        return getTicketResults(filter, pageable);
    }

    @Deprecated
    @JsonView(Views.Ticket.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/tickets", method = GET, params = "orderId")
    public Results<Ticket> findBoletosByOrderIdOnly(TicketFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("find tickets  with filter={}", filter);
        return getTicketResults(filter, pageable);
    }

    @Deprecated
    @JsonView(Views.Ticket.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/boletos", method = GET, params = "orderId")
    public Results<Ticket> findBoletosByOrderIdOnlyOld(TicketFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("find tickets  with filter={}", filter);
        return getTicketResults(filter, pageable);
    }

    @ResponseStatus(OK)
    @PreAuthorize("hasRole('ROLE_MANAGE_BOLETOS')")
    @RequestMapping(value = "/tickets/return-files", method = POST)
    public void processReturn(@RequestParam MultipartFile file) {
        service.processTicketReturn(file);
    }

    private Results<Ticket> getTicketResults(TicketFilter filter, @Validated UnovationPageRequest pageable) {
        Page<br.com.unopay.api.billing.boleto.model.Ticket> page = service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/tickets", api));
    }

}
