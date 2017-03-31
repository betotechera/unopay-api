package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Bank;
import br.com.unopay.api.bacen.service.BankService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed(prefix = "api")
public class BankController {

    @Value("${unopay.api}")
    private String api;

    @Autowired
    private BankService service;

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/banks", method = RequestMethod.GET)
    public Results<Bank> getAll(@Validated UnovationPageRequest pageable) {
        log.info("get banks");
        Page<Bank> page =  service.findAll(pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/banks", api));
    }
}