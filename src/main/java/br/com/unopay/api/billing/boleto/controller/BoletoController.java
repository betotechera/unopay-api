package br.com.unopay.api.billing.boleto.controller;

import br.com.unopay.api.billing.boleto.model.Ticket;
import br.com.unopay.api.billing.boleto.model.filter.BoletoFilter;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
public class BoletoController {

    @Value("${unopay.api}")
    private String api;


    @Autowired
    private TicketService service;

    @JsonView(Views.Boleto.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_BOLETOS')")
    @RequestMapping(value = "/tickets", method = RequestMethod.GET)
    public Results<Ticket> findBoletos(BoletoFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("find tickets  with filter={}", filter);
        return getBoletoResults(filter, pageable);
    }

    @JsonView(Views.Boleto.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/tickets", method = RequestMethod.GET, params = "orderId")
    public Results<Ticket> findBoletosByOrderIdOnly(BoletoFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("find tickets  with filter={}", filter);
        return getBoletoResults(filter, pageable);
    }

    @Deprecated
    @JsonView(Views.Boleto.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/boletos", method = RequestMethod.GET, params = "orderId")
    public Results<Ticket> findBoletosByOrderIdOnlyOld(BoletoFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("find tickets  with filter={}", filter);
        return getBoletoResults(filter, pageable);
    }

    @ResponseStatus(OK)
    @PreAuthorize("hasRole('ROLE_MANAGE_BOLETOS')")
    @RequestMapping(value = "/tickets/return-files", method = POST)
    public void processReturn(@RequestParam MultipartFile file) {
        service.processTicketReturn(file);
    }

    private Results<Ticket> getBoletoResults(BoletoFilter filter, @Validated UnovationPageRequest pageable) {
        Page<Ticket> page = service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/tickets", api));
    }

}
