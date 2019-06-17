package br.com.unopay.api.network.model;

import br.com.unopay.api.bacen.model.Segment;
import br.com.unopay.api.http.DescriptableEnum;

import static br.com.unopay.api.bacen.model.Segment.HEALTH;

public enum ServiceType implements DescriptableEnum {

    DOCTORS_APPOINTMENTS("Consultas Médicas", HEALTH),
    DIAGNOSIS_AND_THERAPY("Diagnose e Terapia", HEALTH),
    DENTISTRY("Odontologia", HEALTH),
    MEDICINES("Medicamentos", HEALTH),
    INTEGRATES_AND_ALTERNATIVES("Práticas Integrativas e Complementares", HEALTH),
    HOSPITALIZATION("Internações",HEALTH),
    MULTIDISCIPLINARY("Especialidade  Multidisciplinar", HEALTH),
    EMS("Pronto Socorro", HEALTH),
    BODY_THERAPY("Terapia do corpo", HEALTH),
    ENERGY_THERAPY("Terapia de energia", HEALTH),
    CONSCIENCE_THERAPY("Terapia da consciência", HEALTH),
    HOLISTIC_MEDICINE("Medicina alternativa", HEALTH);

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
