package br.com.unopay.api.bacen.model;

import br.com.unopay.api.http.DescriptableEnum;

import static br.com.unopay.api.bacen.model.Segment.HEALTH;
import static br.com.unopay.api.bacen.model.Segment.TRANSPORT;

public enum ServiceType implements DescriptableEnum {

    FUEL_ALLOWANCE("Vale Abastecimento", TRANSPORT),
    FREIGHT("Frete",TRANSPORT),
    FREIGHT_RECEIPT("Quitaçao de Frete", TRANSPORT),
    ELECTRONIC_TOLL("Pedágio eletrônico", TRANSPORT),
    DOCTORS_APPOINTMENTS("Consultas Médicas", HEALTH),
    DIAGNOSIS_AND_THERAPY("Diagnose e Terapia", HEALTH),
    DENTISTRY("Odontologia", HEALTH),
    MEDICINES("Medicamentos", HEALTH);

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
