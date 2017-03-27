package br.com.unopay.api.util.rest;

import br.com.unopay.api.bacen.model.Purpose;
import br.com.unopay.api.model.PersonType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class PersonTypeDeserializer extends JsonDeserializer {

    @Override
    public PersonType deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        return PersonType.valueOf(node.get("code").asText());
    }

}
