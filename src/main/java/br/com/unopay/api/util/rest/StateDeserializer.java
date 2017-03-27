package br.com.unopay.api.util.rest;

import br.com.unopay.api.model.PersonType;
import br.com.unopay.api.model.State;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class StateDeserializer extends JsonDeserializer {

    @Override
    public State deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        return State.valueOf(node.get("code").asText());
    }

}
