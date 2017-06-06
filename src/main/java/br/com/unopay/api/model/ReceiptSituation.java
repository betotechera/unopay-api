package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum ReceiptSituation implements DescriptableEnum {

    ACCEPTED("Aceita"), REFUSED("Recusada");

    private String description;

    ReceiptSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}