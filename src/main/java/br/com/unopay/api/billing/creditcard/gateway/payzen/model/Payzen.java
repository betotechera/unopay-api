package br.com.unopay.api.billing.creditcard.gateway.payzen.model;

import br.com.unopay.api.billing.creditcard.gateway.payzen.config.PayzenConfig;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import eu.payzen.webservices.sdk.ServiceResult;
import eu.payzen.webservices.sdk.builder.PaymentBuilder;
import eu.payzen.webservices.sdk.builder.request.CardRequestBuilder;
import eu.payzen.webservices.sdk.builder.request.OrderRequestBuilder;
import eu.payzen.webservices.sdk.builder.request.PaymentRequestBuilder;
import static eu.payzen.webservices.sdk.Payment.create;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Payzen {

    @Autowired
    private PayzenConfig payzenConfig;

    public ServiceResult createTransaction(Transaction transaction) {
        OrderRequestBuilder orderRequestBuilder
                = OrderRequestBuilder
                .create()
                .orderId(transaction.getOrderId());

        PaymentRequestBuilder paymentRequestBuilder
                = PaymentRequestBuilder
                .create()
                .expectedCaptureDate(new Date())
                .amount(transaction.getLongAmountValue())
                .currency(transaction.getAmountCurrencyIsoCode());

        CardRequestBuilder cardRequestBuilder
                = CardRequestBuilder
                .create()
                .number(transaction.getCreditCard().getNumber())
                .scheme(transaction.getCardBrand().name())
                .expiryMonth(Integer.valueOf(transaction.getCreditCard().getExpiryMonth()))
                .expiryYear(Integer.valueOf(transaction.getCreditCard().getExpiryYear()))
                .cardSecurityCode(transaction.getCreditCard().getSecurityCode());

        return create(PaymentBuilder
                        .getBuilder()
                        .order(orderRequestBuilder.build())
                        .payment(paymentRequestBuilder.build())
                        .card(cardRequestBuilder.build())
                        .buildCreate(), new HashMap<>(payzenConfig.getConfig()));
    }
}
