package br.com.unopay.api.bacen.util.rest;

import br.com.unopay.api.bacen.model.EstablishmentType;
import br.com.unopay.api.bacen.model.Scope;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class EstablishmentTypeSerializer extends JsonSerializer<EstablishmentType> {


    @Override
    public void serialize(EstablishmentType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("code",value.name());
        gen.writeStringField("description",value.getDescription());
        gen.writeEndObject();

    }
}
