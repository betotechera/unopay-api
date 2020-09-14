package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.billing.boleto.model.Ticket;
import br.com.unopay.api.billing.boleto.model.TicketPaymentSource;
import br.com.unopay.api.billing.creditcard.model.Amount;
import br.com.unopay.api.billing.creditcard.model.CardBrand;
import br.com.unopay.api.billing.creditcard.model.CreditCard;
import br.com.unopay.api.billing.creditcard.model.CurrencyCode;
import br.com.unopay.api.billing.creditcard.model.GatewaySource;
import br.com.unopay.api.billing.creditcard.model.PaymentMethod;
import br.com.unopay.api.billing.creditcard.model.PaymentRequest;
import br.com.unopay.api.billing.creditcard.model.PersonCreditCard;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.model.OrderType;
import br.com.unopay.api.order.model.PaymentStatus;
import java.math.BigDecimal;

public class BillingTemplateLoader  implements TemplateLoader {

    @Override
    public void load() {

        Fixture.of(Transaction.class).addTemplate("valid", new Rule() {{
            add("createDateTime", instant("now"));
            add("paymentMethod", random(PaymentMethod.class));
            add("amount", one(Amount.class, "valid"));
            add("orderId", regex("\\d{8}"));
            add("creditCard", one(CreditCard.class, "payzenCard"));
            add("issuerDocument", cnpj());
        }});

        Fixture.of(PaymentRequest.class).addTemplate("creditCard", new Rule() {{
            add("method", PaymentMethod.CARD);
            add("value", random(BigDecimal.class, range(40,1000)));
            add("orderId", regex("\\d{8}"));
            add("creditCard", one(CreditCard.class, "payzenCard"));
            add("issuerDocument", cnpj());
        }});

        Fixture.of(PaymentRequest.class).addTemplate("ticket").inherits("creditCard", new Rule() {{
            add("method", PaymentMethod.BOLETO);
        }});

        Fixture.of(Transaction.class).addTemplate("ticket").inherits("valid", new Rule() {{
            add("paymentMethod", PaymentMethod.BOLETO);
        }});

        Fixture.of(Transaction.class).addTemplate("directDebit").inherits("valid", new Rule() {{
            add("paymentMethod", PaymentMethod.DIRECT_DEBIT);
        }});

        Fixture.of(Amount.class).addTemplate("valid", new Rule() {{
            add("currency", CurrencyCode.BRL);
            add("value", random(BigDecimal.class, range(30, 500)));
        }});

        Fixture.of(Order.class).addTemplate("valid", new Rule() {{
            add("createDateTime", instant("now"));
            add("paymentInstrument", one(PaymentInstrument.class, "valid"));
            add("product", one(Product.class, "valid"));
            add("person", one(Person.class, "physical"));
            add("number", regex("\\d{10}"));
            add("paymentRequest", one(PaymentRequest.class, "ticket"));
            add("type", random(OrderType.class));
            add("status", random(PaymentStatus.class));
            add("value", random(BigDecimal.class, range(0.1, 500)));
            add("fee", BigDecimal.ZERO);
            add("contract", one(Contract.class, "valid"));
        }});



        Fixture.of(CreditCard.class).addTemplate("payzenCard", new Rule() {{
            add("expiryMonth", random("8", "12", "2"));
            add("expiryYear", random("2025", "2020", "2030"));
            add("holderName", firstName());
            add("number", random("36000000000008", "378282000000008"));
            add("securityCode", regex("\\d{3}"));
            add("token", regex("\\w{64}"));
        }});

        Fixture.of(Ticket.class).addTemplate("valid", new Rule() {{
            add("sourceId", regex("\\d{8}"));
            add("issuerDocument", cnpj());
            add("payerDocument", cnpj());
            add("expirationDateTime", instant("3 days from now"));
            add("value", random(BigDecimal.class, range(0.1, 500)));
            add("createDateTime", instant("2 days from now"));
            add("paymentPenaltyValue", random(BigDecimal.class, range(0.1, 10.1)));
            add("uri", regex("\\w{10}"));
            add("typingCode", regex("\\d{44}"));
            add("number", random("123456", "65469798", "9878977", "84456546"));
            add("ourNumber", regex("\\d{8}"));
            add("paymentSource", random(TicketPaymentSource.class));
        }});

        Fixture.of(PersonCreditCard.class).addTemplate("valid", new Rule() {{
            add("holderName", regex("\\w{16}"));
            add("brand", random(CardBrand.class));
            add("lastFourDigits", regex("\\d{4}"));
            add("expirationMonth", random("1","2","3","4","5","6","7","8","9","10","11","12"));
            add("expirationYear", random("2028", "2050", "2030", "2028"));
            add("gatewaySource", random(GatewaySource.class));
            add("gatewayToken", regex("\\w{64}"));
            add("createdDateTime", instant("1 second from now"));
        }});
    }
}
