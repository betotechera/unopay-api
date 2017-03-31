package br.com.unopay.api.bacen.util.rest;

import br.com.unopay.api.bacen.model.AccreditedNetworkType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class AccreditedNetworkTypeDeserializer extends JsonDeserializer {

    @Override
    public AccreditedNetworkType deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        return AccreditedNetworkType.valueOf(node.get("code").asText());
    }

}