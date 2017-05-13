package br.com.unopay.api.controller;

import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.service.ContractorInstrumentCreditService;
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
public class ContractorInstrumentCreditController {

    private ContractorInstrumentCreditService service;

    @Autowired
    public ContractorInstrumentCreditController(ContractorInstrumentCreditService service) {
        this.service = service;
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(CREATED)
    @RequestMapping(value = "/payment-instruments/{instrumentId}/credits", method = POST)
    public ResponseEntity<ContractorInstrumentCredit> create(@PathVariable String instrumentId,@Validated(Create.class)
                                                             @RequestBody ContractorInstrumentCredit credit) {
        log.info("inserting payment instrument credit={}", credit);
        ContractorInstrumentCredit created = service.insert(instrumentId, credit);
        log.info("Inserted payment instrument credit={}", created);
        return created(URI.create(
                String.format("/payment-instruments/%s/credits/%s",instrumentId,
                        created.getId()))).body(created);

    }

    @ResponseStatus(OK)
    @JsonView(Views.Public.class)
    @PreAuthorize("hasRole('ROLE_LIST_CREDIT_PAYMENT_INSTRUMENT')")
    @RequestMapping(value = "/payment-instruments/{instrumentId}/credits/{id}", method = GET)
    public ContractorInstrumentCredit get(@PathVariable String instrumentId, @PathVariable String id) {
        log.info("get payment instrument credit={}", id);
        return service.findById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_CREDIT_PAYMENT_INSTRUMENT')")
    @RequestMapping(value = "/payment-instruments/{instrumentId}/credits/{id}", method = RequestMethod.DELETE)
    public void cancel(@PathVariable String instrumentId, @PathVariable  String id) {
        log.info("canceling payment instrument credit id={}", id);
        service.cancel(id);
    }
}
