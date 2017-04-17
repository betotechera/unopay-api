package br.com.unopay.api.bacen.util.rest;

import br.com.unopay.api.bacen.model.UserRelationship;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class UserRelationshipSerializer extends JsonSerializer<UserRelationship> {


    @Override
    public void serialize(UserRelationship value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("code",value.name());
        gen.writeStringField("description",value.getDescription());
        gen.writeEndObject();

    }
}
