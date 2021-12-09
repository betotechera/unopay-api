package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum State implements DescriptableEnum {
    AC("ACRE"),
    AL("ALAGOAS"),
    AM("AMAZONAS"),
    AP("AMAPA"),
    BA("BAHIA"),
    CE("CEARA"),
    DF("DISTRITO FEDERAL"),
    ES("ESPIRITO SANTO"),
    GO("GOIAS"),
    MA("MARANHAO"),
    MT("MATO GROSSO"),
    MS("MATO GROSSO DO SUL"),
    MG("MINAS GERAIS"),
    PA("PARA"),
    PB("PARAIBA"),
    PR("PARANA"),
    PE("PERNAMBUCO"),
    PI("PIAUI"),
    RJ("RIO DE JANEIRO"),
    RN("RIO GRANDE DO NORTE"),
    RS("RIO GRANDE DO SUL"),
    RO("RONDONIA"),
    RR("RORAIMA"),
    SC("SANTA CATARINA"),
    SP("SAO PAULO"),
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
