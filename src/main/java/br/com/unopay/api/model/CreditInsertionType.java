package br.com.unopay.api.model;

import br.com.unopay.api.filter.DescriptionEnum;

public enum CreditInsertionType implements DescriptionEnum {
    BOLETO("Boleto"), DIRECT_DEBIT("Debito em conta."),  CREDIT_CARD("Cartao de credito."), PAMCARD_SYSTEM("Systema Pamcary");

    private String description;

    CreditInsertionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
