package br.com.unopay.api.uaa.controller;

import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.model.groups.Create;
import br.com.unopay.api.uaa.model.groups.Views;
import br.com.unopay.api.uaa.service.UserDetailService;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UserDetailController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailController.class);

    private UserDetailService userDetailService;

    @Autowired
    public UserDetailController(UserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<UserDetail> create(@Validated(Create.class) @RequestBody UserDetail user) {
        LOGGER.info("creating uaa user {}", user);
        UserDetail created = userDetailService.create(user);
        return ResponseEntity
                .created(URI.create("/users"+created.getId()))
                .body(created);

    }
}
