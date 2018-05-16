package br.com.unopay.api.market.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum BonusSituation implements DescriptableEnum {

    FOR_PROCESSING("Aguardando processamento"),
    PROCESSED("Processado"),
    CANCELED("Cancelado");

    private String description;

    BonusSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
