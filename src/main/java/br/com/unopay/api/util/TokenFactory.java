package br.com.unopay.api.util;

import org.apache.commons.lang.RandomStringUtils;

public class TokenFactory {

    private String token;

    private TokenFactory(){}

    public static String generateToken() {
       return RandomStringUtils.randomAlphanumeric(9).toUpperCase();
    }

}
