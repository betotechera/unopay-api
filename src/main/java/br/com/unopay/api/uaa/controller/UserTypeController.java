package br.com.unopay.api.uaa.controller;


import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.UserType;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.api.uaa.service.UserTypeService;
import br.com.unopay.bootcommons.jsoncollections.ListResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Timed(prefix = "api")
@RestController
public class UserTypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTypeController.class);

    private UserTypeService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public UserTypeController(UserTypeService service) {
        this.service = service;
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/user-types", method = RequestMethod.GET)
    public Results<UserType> findAll() {
        LOGGER.info("find all user types");
        List<UserType> types = service.findAll();
        return new Results<>(types);
    }

    @JsonView(Views.GroupUserType.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/user-types/{id}/groups", method = RequestMethod.GET)
    public Results<Group> findUserTypeGroups(@PathVariable String id) {
        LOGGER.info("find user type groups. userTypeId={}",id);
        List<Group> groups = service.findUserTypeGroups(id);
        return new ListResults<>(groups, String.format("%s/user-types/%s/groups", api, id), String.format("%s/user-types/%s", api,id));
    }

}
