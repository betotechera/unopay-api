package br.com.unopay.api.uaa.controller;


import br.com.unopay.api.notification.model.Email;
import br.com.unopay.api.notification.model.EventType;
import br.com.unopay.api.notification.model.Notification;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.UserDetail;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Timed(prefix = "api")
@RestController
public class UserTypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTypeController.class);

    @Autowired
    private UserTypeService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    private NotificationService notificationService;

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/user-types", method = RequestMethod.GET)
    public Results<UserType> findAll() {
        LOGGER.info("find all user types");
        notifyTeste();
        List<UserType> types = service.findAll();
        return new Results<>(types);
    }

    private void notifyTeste(){
        Email email = new Email(){{setTo("teste@teste.com.br"); setSubject("teste"); setPersonalFrom("unopay"); setFrom("teste@unovation.com.br");}};
        UserDetail user = new UserDetail() {{ setName("ze"); }};
        Map<String, Object> payload = new HashMap<String, Object>() {{ put("user",user); }};
        Notification notification = new Notification(){{setEmail(email); setContent("TESTE");  setEventType(EventType.CREATE_PASSWORD); setPayload(payload);}};
        notificationService.notify(notification);
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
