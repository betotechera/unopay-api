package br.com.unopay.api.bacen.util.rest;

import br.com.unopay.api.bacen.model.ServiceType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ServiceTypeSerializer extends JsonSerializer<ServiceType> {


    @Override
    public void serialize(ServiceType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("code",value.name());
        gen.writeStringField("description",value.getDescription());
        gen.writeEndObject();

    }
}
