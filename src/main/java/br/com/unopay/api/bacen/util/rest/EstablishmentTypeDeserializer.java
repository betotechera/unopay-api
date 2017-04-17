package br.com.unopay.api.bacen.util.rest;

import br.com.unopay.api.bacen.model.EstablishmentType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class EstablishmentTypeDeserializer extends JsonDeserializer {

    @Override
    public EstablishmentType deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        return EstablishmentType.valueOf(node.get("code").asText());
    }

}
