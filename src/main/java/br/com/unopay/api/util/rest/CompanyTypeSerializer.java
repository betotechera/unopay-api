package br.com.unopay.api.util.rest;

import br.com.unopay.api.model.CompanyActivity;
import br.com.unopay.api.model.CompanyType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class CompanyTypeSerializer extends JsonSerializer<CompanyType> {

    @Override
    public void serialize(CompanyType value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        gen.writeStringField("code",value.name());
        gen.writeStringField("description",value.getDescription());
        gen.writeEndObject();

    }
}
