package br.com.unopay.api.wingoo.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WingooClient {

    @Value("${client.wingoo.user}")
    private String wingooUser;

    @Value("${client.wingoo.password}")
    private String wingooPassword;

    @Bean
    public RestTemplate wingooRestTemplate() {
        final RestTemplate restTemplate = new RestTemplate();

        restTemplate.setMessageConverters(Arrays.asList(
                new FormHttpMessageConverter(),
                new StringHttpMessageConverter()
        ));
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(wingooUser, wingooPassword));
        restTemplate.getMessageConverters().add(getMappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    private MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return jsonHttpMessageConverter;
    }


}
