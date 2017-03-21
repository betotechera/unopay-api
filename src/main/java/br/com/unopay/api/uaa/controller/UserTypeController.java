package br.com.unopay.api.uaa.controller;


import br.com.unopay.api.uaa.model.UserType;
import br.com.unopay.api.uaa.model.valistionsgroups.Views;
import br.com.unopay.api.uaa.service.UserTypeService;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Timed(prefix = "api")
@RestController
public class UserTypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTypeController.class);

    @Autowired
    private UserTypeService service;

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/user-types", method = RequestMethod.GET)
    public Results<UserType> findAll() {
        LOGGER.info("find all user types");
        List<UserType> types = service.findAll();
        return new Results<>(types);
    }
}
