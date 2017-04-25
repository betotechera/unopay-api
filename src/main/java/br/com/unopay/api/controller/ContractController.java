package br.com.unopay.api.controller;

import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.filter.ContractFilter;
import br.com.unopay.api.service.ContractService;
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
public class ContractController {

    @Value("${unopay.api}")
    private String api;

    private ContractService service;

    @Autowired
    public ContractController(ContractService service) {
        this.service = service;
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/contracts", method = RequestMethod.POST)
    public ResponseEntity<Contract> create(@Validated(Create.class) @RequestBody Contract contract) {
        log.info("creating contract {}", contract);
        Contract created = service.save(contract);
        return ResponseEntity
                .created(URI.create("/contracts/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contracts/{id}", method = RequestMethod.GET)
    public Contract get(@PathVariable String id) {
        log.info("get contract={}", id);
        return service.findById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/contracts/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Contract contract) {
        contract.setId(id);
        log.info("updating contract {}", contract);
        service.update(id,contract);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/contracts/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing contract id={}", id);
        service.delete(id);
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contracts", method = RequestMethod.GET)
    public Results<Contract> getByParams(ContractFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search contract with filter={}", filter);
        Page<Contract> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contracts", api));
    }

}
