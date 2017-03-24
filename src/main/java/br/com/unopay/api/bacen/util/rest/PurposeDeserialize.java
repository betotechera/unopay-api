package br.com.unopay.api.bacen.util.rest;

import br.com.unopay.api.bacen.model.Purpose;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;

public class PurposeDeserialize extends JsonDeserializer {

    @Override
    public Purpose deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        return Purpose.valueOf(node.get("code").asText());
    }

}
