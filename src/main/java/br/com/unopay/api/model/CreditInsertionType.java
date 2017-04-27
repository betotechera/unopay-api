package br.com.unopay.api.model;

import br.com.unopay.api.filter.DescriptibleEnum;

public enum CreditInsertionType implements DescriptibleEnum {
    BOLETO("Boleto"), DIRECT_DEBIT("Debito em conta."),  CREDIT_CARD("Cartao de credito."), PAMCARD_SYSTEM("Systema Pamcary");

    private String description;

    CreditInsertionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
