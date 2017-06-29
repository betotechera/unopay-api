package br.com.unopay.api.http;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenericEnumSerializer <T extends DescriptableEnum> extends JsonSerializer<T>{

    @Override
    @SneakyThrows
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers){
        serialize(value, gen);
    }

    @Override
    @SneakyThrows
    public void serializeWithType(T value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) {
        serialize(value, gen);
    }

    private void serialize(T value, JsonGenerator gen) {
        try {
            gen.writeStartObject();
            gen.writeStringField("code", value.name());
            gen.writeStringField("description", value.getDescription());
            gen.writeEndObject();
        }catch (Exception e){
            log.error("cannot serialise value={}", value, e);
        }
    }

}
