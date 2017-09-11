package br.com.unopay.api.order.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum OrderType implements DescriptableEnum{

    ADHESION("Adesao"), INSTALLMENT_PAYMENT("Pagamento de parcela"), CREDIT("Credito");

    private String description;

    OrderType(String description){
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
