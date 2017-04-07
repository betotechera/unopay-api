package br.com.unopay.api.uaa.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

@Slf4j
public class PasswordEncoderController {

    private PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordEncoderController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "/encoders/passwords", method = RequestMethod.GET)
    public String encodePassword(@RequestParam(value = "password") String password) {
        String encoded = passwordEncoder.encode(password);
        log.info("encoded password {} as {}", password, encoded);
        return encoded;
    }

}
