package br.com.unopay.api.controller;

import br.com.unopay.api.model.Address;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.filter.PersonFilter;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.service.AddressService;
import br.com.unopay.api.service.PersonService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@Slf4j
@RestController
public class AddressController {

    @Autowired
    AddressService service;

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/addresses", method = RequestMethod.GET)
    public ResponseEntity<Address> searchAddress(@RequestParam String zipCode) {
        log.info("find Address with zipCode={}", zipCode);
        return ResponseEntity.ok(service.search(zipCode));
    }

}