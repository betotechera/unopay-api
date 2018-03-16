package br.com.unopay.api.billing.creditcard.gateway.payzen.model;

import br.com.unopay.api.billing.creditcard.gateway.payzen.config.PayzenConfig;
import br.com.unopay.api.billing.creditcard.model.CreditCard;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.uaa.model.UserDetail;
import com.lyra.vads.ws.v5.BillingDetailsRequest;
import com.lyra.vads.ws.v5.CardRequest;
import com.lyra.vads.ws.v5.CreatePayment;
import com.lyra.vads.ws.v5.CreateTokenResponse;
import com.lyra.vads.ws.v5.CustomerRequest;
import com.lyra.vads.ws.v5.PaymentAPI;
import eu.payzen.webservices.sdk.ServiceResult;
import eu.payzen.webservices.sdk.builder.PaymentBuilder;
import eu.payzen.webservices.sdk.builder.request.CardRequestBuilder;
import eu.payzen.webservices.sdk.builder.request.OrderRequestBuilder;
import eu.payzen.webservices.sdk.builder.request.PaymentRequestBuilder;
import eu.payzen.webservices.sdk.client.ClientV5;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static eu.payzen.webservices.sdk.Payment.create;

@Component
public class Payzen {

    @Autowired
    private PayzenConfig payzenConfig;

    public ServiceResult createTransaction(Transaction transaction) {
        OrderRequestBuilder orderRequestBuilder
                = OrderRequestBuilder
                .create()
                .orderId(transaction.getOrderId());

        PaymentRequestBuilder paymentRequestBuilder = getPaymentRequestBuilder(transaction);
        CardRequest cardRequest = getCardRequest(transaction);

        return create(PaymentBuilder
                        .getBuilder()
                        .order(orderRequestBuilder.build())
                        .payment(paymentRequestBuilder.build())
                        .card(cardRequest)
                        .buildCreate(), new HashMap<>(payzenConfig.getConfig()));
    }

    private CardRequest getCardRequest(Transaction transaction) {
        if(transaction.hasCardToken()) {
            return getCardTokenRequest(transaction.getCreditCard());
        }
        return getCardRequest(transaction.getCreditCard());
    }


    public String storeCard(UserDetail user, CreditCard creditCard){
        CardRequest cardRequest = getCardRequest(creditCard);
        HashMap<String, String> config = new HashMap<>(payzenConfig.getConfig());
        CreatePayment createPayment = PaymentBuilder
                .getBuilder()
                .card(cardRequest)
                .buildCreate();
        CustomerRequest customerRequest = new CustomerRequest();
        BillingDetailsRequest billingDetailsRequest = new BillingDetailsRequest() {{
            setEmail(user.getEmail());
            setFirstName(user.getName());
        }};
        customerRequest.setBillingDetails(billingDetailsRequest);
        createPayment.setCustomerRequest(customerRequest);
        CreateTokenResponse.CreateTokenResult tokenResult = createToken(config, createPayment);
        return tokenResult.getCommonResponse().getPaymentToken();
    }

    private CardRequest getCardRequest(CreditCard creditCard) {
        return CardRequestBuilder
                .create()
                .number(creditCard.getNumber())
                .scheme(creditCard.getCardBrand().name())
                .expiryMonth(Integer.valueOf(creditCard.getExpiryMonth()))
                .expiryYear(Integer.valueOf(creditCard.getExpiryYear()))
                .cardSecurityCode(creditCard.getSecurityCode()).build();
    }

    private CardRequest getCardTokenRequest(CreditCard creditCard) {
        return CardRequestBuilder
                .create()
                .paymentToken(creditCard.getCardReference()).build();
    }

    private PaymentRequestBuilder getPaymentRequestBuilder(Transaction transaction) {
        return PaymentRequestBuilder
                .create()
                .expectedCaptureDate(new Date())
                .amount(transaction.getLongAmountValue())
                .currency(transaction.getAmountCurrencyIsoCode());
    }

    private CreateTokenResponse.CreateTokenResult createToken(Map<String, String> config,
                                                              CreatePayment createPaymentRequest) {
        PaymentAPI api = new ClientV5(config).getPaymentAPIImplPort();
        return api.createToken(
                createPaymentRequest.getCommonRequest(),
                createPaymentRequest.getCardRequest(),
                createPaymentRequest.getCustomerRequest());
    }
}
