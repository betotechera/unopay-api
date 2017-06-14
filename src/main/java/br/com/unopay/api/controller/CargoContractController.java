package br.com.unopay.api.controller;

import br.com.unopay.api.model.CargoContract;
import br.com.unopay.api.model.filter.CargoContractFilter;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.service.CargoContractService;
import br.com.unopay.api.service.FreightReceiptService;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed(prefix = "api")
public class CargoContractController {


    private CargoContractService service;
    private FreightReceiptService freightReceiptService;

    @Autowired
    public CargoContractController(CargoContractService service, FreightReceiptService freightReceiptService) {
        this.service = service;
        this.freightReceiptService = freightReceiptService;
    }


    @JsonView({Views.Public.class})
    @ResponseStatus(OK)
    @PreAuthorize("#oauth2.isUser() && hasRole('ROLE_MANAGE_SERVICE_AUTHORIZE')")
    @RequestMapping(value = "/cargo-contracts/documents", method = GET)
    public CargoContract listDocuments(CargoContractFilter filter) {
        log.info("searching documents={}", filter);
        CargoContract cargoContract = freightReceiptService.listDocuments(filter);
        log.info("found cargoContract={}", cargoContract);
        return cargoContract;
    }

    @JsonView({Views.Public.class})
    @ResponseStatus(OK)
    @PreAuthorize("#oauth2.isUser() && hasRole('ROLE_MANAGE_SERVICE_AUTHORIZE')")
    @RequestMapping(value = "/cargo-contracts/{id}", method = GET)
    public CargoContract findById(@PathVariable String id) {
        log.info("searching cargoContract by id={}", id);
        return service.findById(id);
    }

}
