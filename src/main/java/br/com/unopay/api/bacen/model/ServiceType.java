package br.com.unopay.api.bacen.model;

import br.com.unopay.api.bacen.util.rest.ServiceTypeDeserializer;
import br.com.unopay.api.bacen.util.rest.ServiceTypeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = ServiceTypeDeserializer.class)
@JsonSerialize(using = ServiceTypeSerializer.class)
public enum ServiceType {

    FUEL_ALLOWANCE("Vale Abastecimento"), 
    FREIGHT("Frete"), 
    FREIGHT_RECEIPT("Quitaçao de Frete"), 
    ELECTRONIC_TOLL("Pedágio eletrônico");

    private String description;

    ServiceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
