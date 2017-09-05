package br.com.unopay.api.billing.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum PaymentMethod implements DescriptableEnum{

    CARD("Cartao de credito"),
    BOLETO("Boleto");

    private String description;

    PaymentMethod(String description){

        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
