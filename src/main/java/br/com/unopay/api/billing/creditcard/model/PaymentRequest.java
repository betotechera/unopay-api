package br.com.unopay.api.billing.creditcard.model;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest implements Serializable{

    private static final long serialVersionUID = 6206327161119974478L;

    public static final int DEFAULT_INSTALLMENT = 1;
    private PaymentMethod method;
    private String orderId;

    @Valid
    private CreditCard creditCard;

    private BigDecimal value;
    private Integer installments = DEFAULT_INSTALLMENT;
    private Boolean storeCard;
    private Boolean oneClickPayment;

    public Transaction toTransaction() {
        Transaction transaction = new Transaction();
        transaction.setCreditCard(creditCard);
        transaction.setOrderId(orderId);
        transaction.setInstallments(DEFAULT_INSTALLMENT);
        transaction.setPaymentMethod(method);
        transaction.setAmount(new Amount(CurrencyCode.BRL, value));
        return transaction;
    }

    public boolean isMethod(PaymentMethod method) {
        return hasPaymentMethod() && this.method.equals(method);
    }

    public boolean hasPaymentMethod() {
        return method != null;
    }

    public boolean hasStoreCard() {
        return storeCard != null;
    }

    public boolean shouldStoreCard() {
        return isMethod(PaymentMethod.CARD)
                && hasStoreCard() && storeCard;
    }

}
