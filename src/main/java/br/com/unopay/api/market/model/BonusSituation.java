package br.com.unopay.api.market.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum BonusSituation implements DescriptableEnum {

    FOR_PROCESSING("À processar"),
    PROCESSED("Para processar"),
    BLOCKED("Aguardando processamento");

    private String description;

    BonusSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
