package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum PaymentInstrumentType implements DescriptableEnum {
    DIGITAL_WALLET("Carteira digital"), ACCOUNT_DEPOSIT("Conta deposito."),
    PREPAID_CARD("Cartao prepago"), VIRTUAL_CARD("Cartao virtual"), TAG("Tag");

    private String description;

    PaymentInstrumentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
