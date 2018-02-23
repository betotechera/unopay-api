package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum TransactionSituation implements DescriptableEnum {
    AUTHORIZED("Autorizada"), CLOSED_PAYMENT_BATCH("Lote de Pagamento Fechado"), CANCELED("Cancelada");

    private String description;

    TransactionSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
