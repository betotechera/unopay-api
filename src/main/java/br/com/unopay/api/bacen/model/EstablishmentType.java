package br.com.unopay.api.bacen.model;

import br.com.unopay.api.http.DescriptableEnum;

import static br.com.unopay.api.bacen.model.Segment.HEALTH;
import static br.com.unopay.api.bacen.model.Segment.TRANSPORT;

public enum EstablishmentType implements DescriptableEnum {

    SUPPORT_POINT("Ponto de apoio", TRANSPORT),
    SUPPLY_STATION("Posto de abastecimento", TRANSPORT),
    TOLL_STATION("Posto de Pedágio", TRANSPORT),
    DOCTORS_OFFICE("Consultório Médico", HEALTH),
    CLINICAL_ANALYSIS_LABORATORY("Laboratório Análises Clinicas", HEALTH),
    PICTURE_SERVICES("Serviços de Imagem", HEALTH),
    DIAGNOSTIC_AND_THERAPY_SERVICES("Serviços de Diagnose e Terapia", HEALTH),
    PHYSIOTHERAPY("Fisioterapia", HEALTH),
    DENTISTRY("Odontologia", HEALTH),
    PSYCHOLOGY("Psicologia", HEALTH),
    PHARMACIES("Farmácias", HEALTH);

    private String description;
    private Segment segment;

    EstablishmentType(String description, Segment segment) {
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
