package br.com.unopay.api.order.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum PaymentStatus implements DescriptableEnum {

    WAITING_PAYMENT("Aguardando pagamento"),
    PAYMENT_DENIED("Pagamento negado"),
    CANCELED("Cancelado"),
    PAID("Pago");

    private String description;

    PaymentStatus(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
