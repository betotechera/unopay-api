package br.com.unopay.api.billing.remittance.controller;

import br.com.unopay.api.billing.boleto.service.BoletoService;
import br.com.unopay.api.billing.remittance.model.PaymentRemittance;
import br.com.unopay.api.billing.remittance.model.filter.PaymentRemittanceFilter;
import br.com.unopay.api.billing.remittance.service.PaymentRemittanceService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@PreAuthorize("#oauth2.isUser()")
@Timed(prefix = "api")
public class PaymentRemittanceController {

    @Value("${unopay.api}")
    private String api;

    private PaymentRemittanceService service;
    @Autowired
    private BoletoService boletoService;

    @Autowired
    public PaymentRemittanceController(PaymentRemittanceService service) {
        this.service = service;
    }

    @ResponseStatus(OK)
    @JsonView(Views.PaymentRemittance.Detail.class)
    @PreAuthorize("hasRole('ROLE_LIST_PAYMENT_REMITTANCE')")
    @RequestMapping(value = "/payment-remittances/{id}", method = GET)
    public PaymentRemittance get(@PathVariable String id) {
        log.info("get batchClosing={}", id);
        return service.findById(id);
    }

    @ResponseStatus(OK)
    @JsonView(Views.PaymentRemittance.List.class)
    @PreAuthorize("hasRole('ROLE_LIST_PAYMENT_REMITTANCE')")
    @RequestMapping(value = "/payment-remittances", method = GET)
    public Results<PaymentRemittance> findByFilter(PaymentRemittanceFilter filter,
                                                     @Validated UnovationPageRequest pageable) {
        log.info("search PaymentRemittance with filter={}", filter);
        Page<PaymentRemittance> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/payment-remittances", api));
    }

    @ResponseStatus(OK)
    @PreAuthorize("hasRole('ROLE_MANAGE_PAYMENT_REMITTANCE')")
    @RequestMapping(value = "/payment-remittances/return-files", method = POST)
    public void processReturn(@RequestParam MultipartFile file) {
        boletoService.processTicketReturn(file);
    }

}
