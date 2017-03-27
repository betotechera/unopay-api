package br.com.unopay.api.model;

import br.com.unopay.api.util.rest.PersonTypeDeserializer;
import br.com.unopay.api.util.rest.PersonTypeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = PersonTypeDeserializer.class)
@JsonSerialize(using = PersonTypeSerializer.class)
public enum PersonType{

    PHYSICAL("Pessoa Fisica"), LEGAL("Pessoa Juridica");

    private String description;

    PersonType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
