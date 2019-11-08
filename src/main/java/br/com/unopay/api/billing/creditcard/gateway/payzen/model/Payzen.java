package br.com.unopay.api.billing.creditcard.gateway.payzen.model;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.billing.creditcard.gateway.payzen.config.PayzenConfig;
import br.com.unopay.api.billing.creditcard.model.CreditCard;
import br.com.unopay.api.billing.creditcard.model.StoreCard;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
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

import static br.com.unopay.api.uaa.exception.Errors.PAYZEN_ERROR;
import static eu.payzen.webservices.sdk.Payment.create;

@Component
public class Payzen {

    private PayzenConfig payzenConfig;
    private IssuerService issuerService;

    @Autowired
    public Payzen(PayzenConfig payzenConfig,
                  IssuerService issuerService) {
        this.payzenConfig = payzenConfig;
        this.issuerService = issuerService;
    }

    public ServiceResult createTransaction(Transaction transaction) {
        OrderRequestBuilder orderRequestBuilder
                = OrderRequestBuilder
                .create()
                .orderId(transaction.getOrderId());

        PaymentRequestBuilder paymentRequestBuilder = getPaymentRequestBuilder(transaction);
        CardRequest cardRequest = getCardRequest(transaction);
        try {
            return create(PaymentBuilder
                        .getBuilder()
                        .order(orderRequestBuilder.build())
                        .payment(paymentRequestBuilder.build())
                        .card(cardRequest)
                        .buildCreate(), getStringStringHashMap(transaction.getIssuerDocument()));
        } catch (Exception e){
            throw UnovationExceptions.failedDependency().withErrors(PAYZEN_ERROR.withOnlyArguments(e.getMessage()));
        }
    }

    private CardRequest getCardRequest(Transaction transaction) {
        if(transaction.hasCardToken()) {
            return getCardTokenRequest(transaction.getCreditCard());
        }
        return getCardRequest(transaction.getCreditCard());
    }


    public String storeCard(StoreCard user, CreditCard creditCard){
        CardRequest cardRequest = getCardRequest(creditCard);
        HashMap<String, String> config = getStringStringHashMap(user.getIssuerDocument());
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
                .paymentToken(creditCard.getToken()).build();
    }

    private PaymentRequestBuilder getPaymentRequestBuilder(Transaction transaction) {
        return PaymentRequestBuilder
                .create()
                .expectedCaptureDate(new Date())
                .amount(transaction.getLongAmountValue())
                .currency(transaction.getAmountCurrencyIsoCode());
    }

    private HashMap<String, String> getStringStringHashMap(String issuerDocument) {
        Map<String, String> config = payzenConfig.getConfig();
        Issuer issuer = issuerService.findByDocument(issuerDocument);
        config.put("shopId", issuer.payzenShopId());
        config.put("shopKey", issuer.payzenShopKey());
        return new HashMap<>(config);
    }

    private CreateTokenResponse.CreateTokenResult createToken(Map<String, String> config,
                                                              CreatePayment createPaymentRequest) {
        PaymentAPI api = new ClientV5(config).getPaymentAPIImplPort();
        try {
            return api.createToken(
                    createPaymentRequest.getCommonRequest(),
                    createPaymentRequest.getCardRequest(),
                    createPaymentRequest.getCustomerRequest());
        } catch (Exception e){
            throw UnovationExceptions.failedDependency().withErrors(PAYZEN_ERROR.withOnlyArguments(e.getMessage()));
        }
    }
}
