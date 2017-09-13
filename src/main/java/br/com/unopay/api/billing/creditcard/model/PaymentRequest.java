package br.com.unopay.api.billing.creditcard.model;

import java.math.BigDecimal;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull
    private PaymentMethod method;
    private String orderId;

    @Valid
    private CreditCard creditCard;

    private BigDecimal value;
    private Integer installments = 1;
    private Boolean storeCard;
    private Boolean oneClickPayment;

    public Transaction toTransaction() {
        Transaction transaction = new Transaction();
        transaction.setCreditCard(creditCard);
        transaction.setOrderId(orderId);
        transaction.setInstallments(installments);
        transaction.setPaymentMethod(method);
        transaction.setAmount(new Amount(CurrencyCode.BRL, value));
        return transaction;
    }
}
