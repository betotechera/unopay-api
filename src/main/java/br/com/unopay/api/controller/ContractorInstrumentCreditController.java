package br.com.unopay.api.controller;

import br.com.unopay.api.credit.model.ContractorInstrumentCredit;
import br.com.unopay.api.credit.model.filter.ContractorInstrumentCreditFilter;
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@Timed(prefix = "api")
public class ContractorInstrumentCreditController {

    @Value("${unopay.api}")
    private String api;

    private ContractorInstrumentCreditService service;

    @Autowired
    public ContractorInstrumentCreditController(ContractorInstrumentCreditService service) {
        this.service = service;
    }

    @JsonView(Views.ContractorInstrumentCredit.Detail.class)
    @ResponseStatus(CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_CREDIT_PAYMENT_INSTRUMENT')")
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
    @JsonView(Views.ContractorInstrumentCredit.List.class)
    @PreAuthorize("hasRole('ROLE_LIST_CREDIT_PAYMENT_INSTRUMENT') || hasRole('ROLE_MANAGE_SERVICE_AUTHORIZE') ")
    @RequestMapping(value = "/payment-instruments/credits", method = GET)
    public Results<ContractorInstrumentCredit> findAll(ContractorInstrumentCreditFilter filter,
                                                      @Validated UnovationPageRequest pageable) {
        log.info("search ContractorInstrumentCredit with filter={}", filter);
        Page<ContractorInstrumentCredit> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),String.format("%s/payment-instruments/credits", api));
    }

    @ResponseStatus(OK)
    @JsonView(Views.ContractorInstrumentCredit.Detail.class)
    @PreAuthorize("hasRole('ROLE_LIST_CREDIT_PAYMENT_INSTRUMENT')")
    @RequestMapping(value = "/payment-instruments/{instrumentId}/credits/{id}", method = GET)
    public ContractorInstrumentCredit get(@PathVariable String instrumentId, @PathVariable String id) {
        log.info("get payment instrument credit={}", id);
        return service.findById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_CREDIT_PAYMENT_INSTRUMENT') || hasRole('ROLE_MANAGE_SERVICE_AUTHORIZE') ")
    @RequestMapping(value = "/payment-instruments/{instrumentId}/credits/{id}", method = RequestMethod.DELETE)
    public void cancel(@PathVariable String instrumentId, @PathVariable  String id) {
        log.info("canceling payment instrument credit id={}", id);
        service.cancel(instrumentId, id);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_CREDIT_PAYMENT_INSTRUMENT') || hasRole('ROLE_MANAGE_SERVICE_AUTHORIZE') ")
    @RequestMapping(value = "/contracts/{contractId}/payment-instruments/credits", method = RequestMethod.DELETE)
    public void cancelContractCredits(@PathVariable String contractId) {
        log.info("canceling payment instrument credit to contract id={}", contractId);
        service.cancel(contractId);
    }


}
