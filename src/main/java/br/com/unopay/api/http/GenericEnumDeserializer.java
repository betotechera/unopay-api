package br.com.unopay.api.http;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.SneakyThrows;

public class GenericEnumDeserializer<T extends Enum<T>> extends JsonDeserializer<T> implements
        ContextualDeserializer {

    private Class<T> targetClass;

    public GenericEnumDeserializer() {}

    public GenericEnumDeserializer(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    @SneakyThrows
    public T deserialize(JsonParser jp, DeserializationContext dc) {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        if(node != null && node.get("code") != null){
            String code = node.get("code").asText();
            return T.valueOf(targetClass, code);
        }
        return node != null ? T.valueOf(targetClass, node.asText()) : null ;
    }

    @Override
    @SneakyThrows
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)  {
        Class<T> targetClass = (Class<T>) ctxt.getContextualType().getRawClass();
        return new GenericEnumDeserializer(targetClass);
    }
}
