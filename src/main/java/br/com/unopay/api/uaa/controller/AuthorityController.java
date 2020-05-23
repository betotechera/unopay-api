package br.com.unopay.api.uaa.controller;

import br.com.unopay.api.uaa.model.Authority;
import br.com.unopay.api.uaa.service.AuthorityService;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Timed(prefix = "api")
@PreAuthorize("isAuthenticated()")
public class AuthorityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityController.class);

    private AuthorityService authorityService;

    @Autowired
    public AuthorityController(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @RequestMapping(value = "/authorities", method = RequestMethod.GET)
    public List<Authority> getAuthorities() {
        LOGGER.info("getting all authorities");
        return authorityService.getAll();
    }

}