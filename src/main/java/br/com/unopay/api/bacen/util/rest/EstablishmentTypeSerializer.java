package br.com.unopay.api.bacen.util.rest;

import br.com.unopay.api.bacen.model.EstablishmentType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.SneakyThrows;

public class EstablishmentTypeSerializer extends JsonSerializer<EstablishmentType> {


    @Override
    @SneakyThrows
    public void serialize(EstablishmentType value, JsonGenerator gen, SerializerProvider serializers)  {
        gen.writeStartObject();
        gen.writeStringField("code",value.name());
        gen.writeStringField("description",value.getDescription());
        gen.writeEndObject();

    }
}
