package br.com.unopay.api.uaa.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class PasswordEncoderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordEncoderController.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/encoders/passwords", method = RequestMethod.GET)
    public String encodePassword(@RequestParam(value = "password") String password) {
        String encoded = passwordEncoder.encode(password);
        LOGGER.info("encoded password {} as {}", password, encoded);
        return encoded;
    }

}
