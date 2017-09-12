package br.com.unopay.api.order.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum OrderStatus implements DescriptableEnum {

    WAITING_PAYMENT("Aguardando pagamento"),
    PAYMENT_DENIED("Pagamento negado"),
    CANCELED("Cancelado"),
    PAID("Pago");

    private String description;

    OrderStatus(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
