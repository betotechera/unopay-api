package br.com.unopay.api.bacen.model;

import br.com.unopay.api.http.DescriptionEnum;


public enum EstablishmentType implements DescriptionEnum {
    SUPPORT_POINT("Ponto de apoio"), SUPPLY_STATION("Posto de abastecimento"),TOLL_STATION("Posto de Pedágio");

    private String description;

    EstablishmentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
