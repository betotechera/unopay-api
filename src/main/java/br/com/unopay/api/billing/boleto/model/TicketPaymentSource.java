package br.com.unopay.api.billing.boleto.model;

import br.com.unopay.bootcommons.http.DescriptableEnum;

public enum TicketPaymentSource implements DescriptableEnum {

    HIRER("contratante"), CONTRACTOR("favorecido");

    private String description;

    TicketPaymentSource(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
