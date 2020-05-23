package br.com.unopay.api.controller;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractEstablishment;
import br.com.unopay.api.model.filter.ContractFilter;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
public class ContractController {

    @Value("${unopay.api}")
    private String api;

    private ContractService service;

    @Autowired
    public ContractController(ContractService service) {
        this.service = service;
    }

    @JsonView(Views.Contract.Detail.class)
    @ResponseStatus(CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_CONTRACT')")
    @RequestMapping(value = "/contracts", method = POST)
    public ResponseEntity<Contract> create(@Validated(Create.class) @RequestBody Contract contract) {
        log.info("creating contract {}", contract);
        Contract created = service.create(contract);
        return
                created(URI.create("/contracts/"+created.getId()))
                .body(created);

    }
    @ResponseStatus(OK)
    @JsonView(Views.Contract.Detail.class)
    @PreAuthorize("hasRole('ROLE_LIST_CONTRACT') || hasRole('ROLE_MANAGE_SERVICE_AUTHORIZE') ")
    @RequestMapping(value = "/contracts/{id}", method = GET)
    public Contract get(@PathVariable String id) {
        log.info("get contract={}", id);
        return service.findById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_CONTRACT')")
    @RequestMapping(value = "/contracts/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Contract contract) {
        contract.setId(id);
        log.info("updating contract {}", contract);
        service.update(id,contract);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_CONTRACT')")
    @RequestMapping(value = "/contracts/installments", method = RequestMethod.PUT)
    public void installments() {
        log.info("processing the contract installments");
        service.createInstallmentOrders();
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_CONTRACT')")
    @RequestMapping(value = "/contracts/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing contract id={}", id);
        service.cancel(id);
    }

    @ResponseStatus(OK)
    @JsonView(Views.Contract.List.class)
    @PreAuthorize("hasRole('ROLE_LIST_CONTRACT') || hasRole('ROLE_MANAGE_SERVICE_AUTHORIZE') ")
    @RequestMapping(value = "/contracts", method = GET)
    public Results<Contract> getByParams(ContractFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search contract with filter={}", filter);
        Page<Contract> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contracts", api));
    }

    @PreAuthorize("hasRole('ROLE_MANAGE_CONTRACT')")
    @RequestMapping(value = "/contracts/{id}/establishments", method = RequestMethod.POST)
    public ResponseEntity<ContractEstablishment> addEstablishmentsToContract(@PathVariable  String id,
                                                                             @RequestBody ContractEstablishment
                                                                                     contractEstablishment) {
        log.info("Creating ContractEstablishment {} to contractId={}", contractEstablishment,id);
        ContractEstablishment created = service.addEstablishments(id, contractEstablishment);
        return created(URI.create("/contracts/"+id+"/establishments/"+created.getId()))
                        .body(created);
    }

    @PreAuthorize("hasRole('ROLE_MANAGE_CONTRACT')")
    @RequestMapping(value = "/contracts/{id}/establishments/{contractEstablishmentId}", method = RequestMethod.DELETE)
    public void removeEstablishment(@PathVariable  String id, @PathVariable String contractEstablishmentId) {
        log.info("Removing ContractEstablishment {} to contractId={}", contractEstablishmentId,id);
        service.removeEstablishment(id,contractEstablishmentId);
    }

    @JsonView(Views.Contract.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/contracts/menu")
    List<Contract> listForMenu() {
        return service.listForMenu();
    }

}
