package br.com.unopay.api.credit.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum CreditSituation implements DescriptableEnum {
    PROCESSING("Processando"),TO_COLLECT("A cobrar"),
    CONFIRMED("Confirmado"), CANCELED("Cancelado"),
    EXPIRED("Expirado"), AVAILABLE("Disponivel");

    private String description;

    CreditSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
