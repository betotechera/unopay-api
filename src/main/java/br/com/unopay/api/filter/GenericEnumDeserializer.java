package br.com.unopay.api.filter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;

import java.lang.reflect.ParameterizedType;

public class GenericEnumDeserializer<T extends Enum<T>>  extends JsonDeserializer {

    @Override
    @SneakyThrows
    public T deserialize(JsonParser jp, DeserializationContext dc) {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        Class<T> type = (Class<T>) ((ParameterizedType) T).getActualTypeArguments()[0];

        if(node != null){
            String code = node.get("code").asText();
            return T.valueOf(type, code);
        }
        return T.valueOf(type, node.asText());
    }

}
