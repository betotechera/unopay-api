package br.com.unopay.api.bacen.model;

import br.com.unopay.api.bacen.util.rest.EstablishmentTypeDeserializer;
import br.com.unopay.api.bacen.util.rest.EstablishmentTypeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonDeserialize(using = EstablishmentTypeDeserializer.class)
@JsonSerialize(using = EstablishmentTypeSerializer.class)
public enum EstablishmentType {
    SUPPORT_POINT("Ponto de apoio"), SUPPLY_STATION("Posto de abastecimento"),TOLL_STATION("Posto de Pedágio");

    private String description;

    EstablishmentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
