package br.com.unopay.api.order.model;

import java.util.Date;
import lombok.Data;

@Data
public class OrderSummary {

    public OrderSummary(Order order) {
        this.id = order.getId();
        this.number = order.getNumber();
        this.createdAt = order.getCreateDateTime();
        this.paymentStatus = order.getStatus();
    }

    private String id;
    private String number;
    private PaymentStatus paymentStatus;
    private Date createdAt;
}
