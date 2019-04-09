package br.com.unopay.api.network.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum ServicePeriodSituation implements DescriptableEnum {

    ACTIVE("Ativo"), SUSPENDED("Suspenso");

    private String description;

    ServicePeriodSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
