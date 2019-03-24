package br.com.unopay.api.bacen.model;

import br.com.unopay.api.http.DescriptableEnum;

import static br.com.unopay.api.bacen.model.Segment.HEALTH;
import static br.com.unopay.api.bacen.model.Segment.TRANSPORT;

public enum EstablishmentType implements DescriptableEnum {

    DOCTORS_OFFICE("Consultório Médico", HEALTH),
    CLINICAL_ANALYSIS_LABORATORY("Laboratório Análises Clinicas", HEALTH),
    PICTURE_SERVICES("Serviços de Imagem", HEALTH),
    DIAGNOSTIC_AND_THERAPY_SERVICES("Serviços de Diagnose e Terapia", HEALTH),
    PHYSIOTHERAPY("Fisioterapia", HEALTH),
    DENTISTRY("Odontologia", HEALTH),
    PSYCHOLOGY("Psicologia", HEALTH),
    PHARMACIES("Farmácias", HEALTH),
    ALTERNATIVE_MEDICINE("Medicina alternativa", HEALTH),
    HOSPITAL("Hospitais", HEALTH);

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
