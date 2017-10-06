package br.com.unopay.api.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GenericObjectMapper {

    private ObjectMapper objectMapper;

    public GenericObjectMapper(){}

    @Autowired
    public GenericObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T getAsObject(String notificationAsString, Class<T> klass) {
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(notificationAsString,klass);
        } catch (IOException e) {
            log.error("unable to parse class={}",klass.getSimpleName(), e);
            return null;
        }
    }
}
