package br.com.unopay.api.bacen.model;

import br.com.unopay.api.bacen.util.rest.PurposeDeserialize;
import br.com.unopay.api.bacen.util.rest.PurposeSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = PurposeDeserialize.class)
@JsonSerialize(using = PurposeSerialize.class)
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
