package br.com.unopay.api.uaa.google;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class GoogleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleService.class);

    private static final String ME = "https://www.googleapis.com/plus/v1/people/me?access_token={access_token}";

    private RestTemplate restTemplate = new RestTemplate();

    public GoogleProfile getProfile(String token) {
        Map<String, String> uriVars = ImmutableMap.of("access_token", token);
        try {
            Map response = restTemplate.getForObject(ME, Map.class, uriVars);
            return new GoogleProfile(response);

        } catch(HttpClientErrorException e) {
            LOGGER.warn("error getting google user profile with token ".concat(token), e);
            return null;
        }
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
