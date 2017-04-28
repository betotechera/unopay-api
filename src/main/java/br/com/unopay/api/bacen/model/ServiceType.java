package br.com.unopay.api.bacen.model;

import br.com.unopay.api.http.DescriptionEnum;

public enum ServiceType implements DescriptionEnum {

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
