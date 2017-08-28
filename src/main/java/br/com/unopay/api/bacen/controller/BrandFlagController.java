package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.service.BrandFlagService;
import br.com.unopay.api.model.BrandFlag;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed(prefix = "api")
public class BrandFlagController {

    @Value("${unopay.api}")
    private String api;

    private BrandFlagService service;

    @Autowired
    public BrandFlagController(BrandFlagService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/brand-flags", method = RequestMethod.GET)
    public Results<BrandFlag> getAll(@Validated UnovationPageRequest pageable) {
        log.info("get brand flags");
        List<BrandFlag> brandFlags =  service.findAll("all");
        return new Results<>(brandFlags);
    }
}
