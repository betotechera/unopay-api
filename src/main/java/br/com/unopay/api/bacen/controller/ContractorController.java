package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.ContractorInstrumentCreditService;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import java.net.URI;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed(prefix = "api")
public class ContractorController {

    private ContractorService service;
    private ContractService contractService;
    private ContractorInstrumentCreditService contractorInstrumentCreditService;
    private PaymentInstrumentService paymentInstrumentService;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public ContractorController(ContractorService service, ContractService contractService,
                                ContractorInstrumentCreditService contractorInstrumentCreditService,
                                PaymentInstrumentService paymentInstrumentService) {
        this.service = service;
        this.contractService = contractService;
        this.contractorInstrumentCreditService = contractorInstrumentCreditService;
        this.paymentInstrumentService = paymentInstrumentService;
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/contractors", method = RequestMethod.POST)
    public ResponseEntity<Contractor> create(@Validated(Create.class) @RequestBody Contractor contractor) {
        log.info("creating contractor {}", contractor);
        Contractor created = service.create(contractor);
        return ResponseEntity
                .created(URI.create("/contractors/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/{id}", method = RequestMethod.GET)
    public Contractor get(@PathVariable  String id) {
        log.info("get Contractor={}", id);
        return service.getById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/contractors/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Contractor contractor) {
        contractor.setId(id);
        log.info("updating contractor {}", contractor);
        service.update(id, contractor);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/contractors/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing hired id={}", id);
        service.delete(id);
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors", method = RequestMethod.GET)
    public Results<Contractor> getByParams(ContractorFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search Contractor with filter={}", filter);
        Page<Contractor> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors", api));
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/{id}/contracts", method = RequestMethod.GET)
    public Results<Contract> getValidContracts(@PathVariable  String id, @RequestParam(required = false)
            String establishmentId,@RequestParam(required = false) Set<ServiceType> serviceType) {
        log.info("search Contractor Contracts id={} establishmentId={}", id,establishmentId);
        List<Contract> contracts = contractService.getContractorValidContracts(id, establishmentId,serviceType);
        return new Results<>(contracts);
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/{id}/credits", method = RequestMethod.GET)
    public Results<ContractorInstrumentCredit> getCredits(@PathVariable  String id,@RequestParam(required = false)
                                                    String contractId, @Validated UnovationPageRequest pageable) {
        log.info("search Contractor credits id={}", id);
        Page<ContractorInstrumentCredit> page = contractorInstrumentCreditService
                                                                    .findContractorCredits(contractId, id, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors", api));
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/{id}/payment-instruments", method = RequestMethod.GET)
    public Results<PaymentInstrument> getInstruments(@PathVariable String id) {
        log.info("search Contractor instruments id={}", id);
        List<PaymentInstrument> contracts = paymentInstrumentService.findByContractorId(id);
        return new Results<>(contracts);
    }

}
