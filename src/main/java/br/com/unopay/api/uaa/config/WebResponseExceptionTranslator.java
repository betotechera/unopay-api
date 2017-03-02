package br.com.unopay.api.uaa.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;

public class WebResponseExceptionTranslator extends DefaultWebResponseExceptionTranslator {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebResponseExceptionTranslator.class);

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {

        if (e instanceof NullPointerException) {
            LOGGER.error("error", e);
        }

        return super.translate(e);
    }
}