package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum PaymentInstrumentSituation implements DescriptableEnum {
    ISSUED("Emitido"), ENABLED("Habilitado"), ACTIVE("Ativo"), SUSPENDED("Supspenso."), CANCELED("Cancelado");

    private String description;

    PaymentInstrumentSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
