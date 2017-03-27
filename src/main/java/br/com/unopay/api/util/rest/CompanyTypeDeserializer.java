package br.com.unopay.api.util.rest;

import br.com.unopay.api.model.CompanyActivity;
import br.com.unopay.api.model.CompanyType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class CompanyTypeDeserializer extends JsonDeserializer {

    @Override
    public CompanyType deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        return CompanyType.valueOf(node.get("code").asText());
    }

}
