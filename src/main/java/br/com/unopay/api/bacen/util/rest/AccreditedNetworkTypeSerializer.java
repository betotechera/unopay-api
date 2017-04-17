package br.com.unopay.api.bacen.util.rest;

import br.com.unopay.api.bacen.model.AccreditedNetworkType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.SneakyThrows;

public class AccreditedNetworkTypeSerializer extends JsonSerializer<AccreditedNetworkType> {


    @Override
    @SneakyThrows
    public void serialize(AccreditedNetworkType value, JsonGenerator gen, SerializerProvider serializers)  {
        gen.writeStartObject();
        gen.writeStringField("code",value.name());
        gen.writeStringField("description",value.getDescription());
        gen.writeEndObject();

    }
}
