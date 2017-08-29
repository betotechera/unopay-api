package br.com.unopay.api.billing.gateway.payzen.service;

import br.com.unopay.api.billing.model.Amount;
import br.com.unopay.api.billing.model.CardBrand;
import br.com.unopay.api.billing.model.CreditCard;
import br.com.unopay.api.billing.model.Gateway;
import br.com.unopay.api.billing.model.Transaction;
import br.com.unopay.api.model.Address;
import br.com.unopay.api.uaa.model.UserDetail;
import eu.payzen.webservices.sdk.ServiceResult;
import eu.payzen.webservices.sdk.builder.PaymentBuilder;
import eu.payzen.webservices.sdk.builder.request.CardRequestBuilder;
import eu.payzen.webservices.sdk.builder.request.OrderRequestBuilder;
import eu.payzen.webservices.sdk.builder.request.PaymentRequestBuilder;
import java.util.Set;

import static eu.payzen.webservices.sdk.Payment.create;

public class PayzenService implements Gateway {

    @Override
    public CreditCard storeCard(UserDetail user, CreditCard card, Address billingAddress) {
        return null;
    }

    @Override
    public Set<CreditCard> getCards(String userId) {
        return null;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        OrderRequestBuilder orderRequestBuilder
                = OrderRequestBuilder
                .create()
                .orderId(transaction.getOrderId());

        PaymentRequestBuilder paymentRequestBuilder
                = PaymentRequestBuilder
                .create()
                .amount(500)
                .currency(978);

        CardRequestBuilder cardRequestBuilder
                = CardRequestBuilder
                .create()
                .number(transaction.getCreditCard().getNumber())
                .scheme(CardBrand.fromCardNumber(transaction.getCreditCard().getNumber()).name())
                .expiryMonth(Integer.valueOf(transaction.getCreditCard().getExpiryMonth()))
                .expiryYear(Integer.valueOf(transaction.getCreditCard().getExpiryMonth()))
                .cardSecurityCode(transaction.getCreditCard().getSecurityCode());

        ServiceResult result = create(PaymentBuilder
                        .getBuilder()
                        .order(orderRequestBuilder.build())
                        .payment(paymentRequestBuilder.build())
                        .card(cardRequestBuilder.build())
                        .buildCreate(),
               null);
        return null;
    }

    @Override
    public Transaction cancelTransaction(Transaction transaction, Amount amount) {
        return null;
    }

    @Override
    public Transaction captureTransaction(Transaction transaction) {
        return null;
    }
}
