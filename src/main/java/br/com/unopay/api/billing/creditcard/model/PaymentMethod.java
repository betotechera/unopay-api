package br.com.unopay.api.billing.creditcard.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum PaymentMethod implements DescriptableEnum{

    CARD("Cartao de credito"),
    BOLETO("Boleto"),
    DIRECT_DEBIT("Debito em conta");


    private String description;

    PaymentMethod(String description){

        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
