package br.com.unopay.api.uaa.controller;

import br.com.unopay.api.uaa.model.Authority;
import br.com.unopay.api.uaa.service.AuthorityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("#oauth2.isClient()")
public class AuthorityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityController.class);

    @Autowired
    private AuthorityService authorityService;

    @RequestMapping(value = "/authorities", method = RequestMethod.GET)
    public List<Authority> getAuthorities() {
        LOGGER.info("getting all authorities");
        List<Authority> authorities = authorityService.getAll();
        return authorities;
    }

}