package br.com.unopay.api.http;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.SneakyThrows;

public class GenericEnumSerializer <T extends DescriptionEnum> extends JsonSerializer<T>{

    @Override
    @SneakyThrows
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers){
        gen.writeStartObject();
        gen.writeStringField("code",value.name());
        gen.writeStringField("description",value.getDescription());
        gen.writeEndObject();
    }
}
