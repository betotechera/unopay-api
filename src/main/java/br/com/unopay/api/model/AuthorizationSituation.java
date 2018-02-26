package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum AuthorizationSituation implements DescriptableEnum {
    AUTHORIZED("Autorizada"), CLOSED_PAYMENT_BATCH("Lote de Pagamento Fechado"), CANCELED("Cancelada");

    private String description;

    AuthorizationSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
