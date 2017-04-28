package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptionEnum;

public enum State implements DescriptionEnum{

    AM("AMAZONAS"),
    BA("BAHIA"),
    CE("CEARÁ"),
    DF("DISTRITO FEDERAL"),
    ES("ESPÍRITO SANTO"),
    GO("GOIÁS"),
    MA("MARANHÃO"),
    MT("MATO GROSSO"),
    MS("MATO GROSSO DO SUL"),
    MG("MINAS GERAIS"),
    PA("PARÁ"),
    PB("PARAÍBA"),
    PR("PARANÁ"),
    PE("PERNAMBUCO"),
    PI("PIAUÍ"),
    RJ("RIO DE JANEIRO"),
    RN("RIO GRANDE DO NORTE"),
    RS("RIO GRANDE DO SUL"),
    RO("RONDONIA"),
    RR("RORAIMA"),
    SC("SANTA CATARINA"),
    SP("SÃO PAULO"),
    SE("SERGIPE"),
    TO("TOCANTINS");

    private String description;

    State(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
