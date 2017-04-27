package br.com.unopay.api.filter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.SneakyThrows;

public class GenericEnumSerializer <T extends DescriptibleEnum> extends JsonSerializer<T>{

    @Override
    @SneakyThrows
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers){
        gen.writeStartObject();
        gen.writeStringField("code",value.name());
        gen.writeStringField("description",value.getDescription());
        gen.writeEndObject();
    }
}
