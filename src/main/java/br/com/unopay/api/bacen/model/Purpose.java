package br.com.unopay.api.bacen.model;

import br.com.unopay.api.bacen.util.rest.PurposeDeserializer;
import br.com.unopay.api.bacen.util.rest.PurposeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = PurposeDeserializer.class)
@JsonSerialize(using = PurposeSerializer.class)
public enum Purpose{

    BUY("Compra"), TRANSFER("Tranferencia");

    private String description;

    Purpose(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
