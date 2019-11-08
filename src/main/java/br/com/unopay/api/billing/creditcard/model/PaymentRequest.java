package br.com.unopay.api.billing.creditcard.model;

import br.com.unopay.api.order.model.RecurrencePaymentInformation;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.Valid;
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
    private String issuerDocument;

    public Transaction toTransaction() {
        Transaction transaction = new Transaction();
        transaction.setCreditCard(creditCard);
        transaction.setOrderId(orderId);
        transaction.setInstallments(DEFAULT_INSTALLMENT);
        transaction.setPaymentMethod(method);
        transaction.setAmount(new Amount(CurrencyCode.BRL, value));
        transaction.setIssuerDocument(this.issuerDocument);
        return transaction;
    }

    public RecurrencePaymentInformation toRecurrencePaymentInformation() {
        RecurrencePaymentInformation paymentInformation = new RecurrencePaymentInformation();
        if(creditCard != null) {
            paymentInformation.setCreditCardHolderName(creditCard.getHolderName());
            paymentInformation.setCreditCardBrand(creditCard.getCardBrand());
            paymentInformation.setCreditCardLastFourDigits(creditCard.lastValidFourDigits());
            paymentInformation.setCreditCardMonth(creditCard.getExpiryMonth());
            paymentInformation.setCreditCardYear(creditCard.getExpiryYear());
            paymentInformation.setCreditCardToken(creditCard.getToken());
            paymentInformation.setPaymentMethod(PaymentMethod.CARD);
        }
        return paymentInformation;
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
