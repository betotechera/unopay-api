package br.com.unopay.api.bacen.model;

import br.com.unopay.api.http.DescriptableEnum;

import static br.com.unopay.api.bacen.model.Segment.*;

public enum ServiceType implements DescriptableEnum {

    FUEL_ALLOWANCE("Vale Abastecimento", TRANSPORT),
    FREIGHT("Frete",TRANSPORT),
    FREIGHT_RECEIPT("Quitaçao de Frete", TRANSPORT),
    ELECTRONIC_TOLL("Pedágio eletrônico", TRANSPORT);

    private String description;
    private Segment segment;

    ServiceType(String description, Segment segment) {
        this.description = description;
        this.segment = segment;
    }

    public String getDescription() {
        return description;
    }

    public Segment getSegment() {
        return segment;
    }
}
