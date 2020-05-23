package br.com.unopay.api.uaa.controller;

import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.UserType;
import br.com.unopay.api.uaa.service.UserTypeService;
import br.com.unopay.bootcommons.jsoncollections.ListResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/user-types", method = RequestMethod.GET)
    public Results<UserType> findAll() {
        LOGGER.info("find all user types");
        List<UserType> types = service.findAll();
        return new Results<>(types);
    }

    @JsonView({Views.Group.Detail.class})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_USER_TYPE')")
    @RequestMapping(value = "/user-types/{id}/groups", method = RequestMethod.GET)
    public Results<Group> findUserTypeGroups(@PathVariable String id) {
        LOGGER.info("find user type groups. userTypeId={}",id);
        List<Group> groups = service.findUserTypeGroups(id);
        return new ListResults<>(groups, String.format("%s/user-types/%s/groups", api, id),
                                                        String.format("%s/user-types/%s", api,id));
    }

}
