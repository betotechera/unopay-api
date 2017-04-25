package br.com.unopay.api.controller;

import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.filter.PaymentInstrumentFilter;
import br.com.unopay.api.service.PaymentInstrumentService;
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
public class PaymentInstrumentController {

    @Value("${unopay.api}")
    private String api;

    private PaymentInstrumentService service;

    @Autowired
    public PaymentInstrumentController(PaymentInstrumentService service) {
        this.service = service;
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/payment-instruments", method = RequestMethod.POST)
    public ResponseEntity<PaymentInstrument> create(@Validated(Create.class)
                                                        @RequestBody PaymentInstrument paymentInstrument) {
        log.info("creating paymentInstrument {}", paymentInstrument);
        PaymentInstrument created = service.save(paymentInstrument);
        return ResponseEntity
                .created(URI.create("/payment-instruments/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/payment-instruments/{id}", method = RequestMethod.GET)
    public PaymentInstrument get(@PathVariable String id) {
        log.info("get paymentInstrument={}", id);
        return service.findById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/payment-instruments/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id,
                       @Validated(Update.class) @RequestBody PaymentInstrument paymentInstrument) {
        paymentInstrument.setId(id);
        log.info("updating paymentInstrument {}", paymentInstrument);
        service.update(id,paymentInstrument);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/payment-instruments/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing paymentInstrument id={}", id);
        service.delete(id);
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/payment-instruments", method = RequestMethod.GET)
    public Results<PaymentInstrument> getByParams(PaymentInstrumentFilter filter,
                                                  @Validated UnovationPageRequest pageable) {
        log.info("search paymentInstrument with filter={}", filter);
        Page<PaymentInstrument> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/payment-instruments", api));
    }

}
