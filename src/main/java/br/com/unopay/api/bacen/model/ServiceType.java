package br.com.unopay.api.bacen.model;

import br.com.unopay.api.filter.DescriptibleEnum;

public enum ServiceType implements DescriptibleEnum {

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
