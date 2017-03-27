package br.com.unopay.api.util.rest;

import br.com.unopay.api.model.PersonType;
import br.com.unopay.api.model.RegistryEntity;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class RegistryEntitySerializer extends JsonSerializer<RegistryEntity> {

    @Override
    public void serialize(RegistryEntity value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        gen.writeStringField("code",value.name());
        gen.writeStringField("description",value.getDescription());
        gen.writeEndObject();

    }
}
