package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum CreditInsertionType implements DescriptableEnum {
    BOLETO("Boleto"), DIRECT_DEBIT("Debito em conta."),
    CREDIT_CARD("Cartao de credito."), PAMCARD_SYSTEM("Systema Pamcary");

    private String description;

    CreditInsertionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
