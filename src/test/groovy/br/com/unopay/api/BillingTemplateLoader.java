package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.billing.model.Amount;
import br.com.unopay.api.billing.model.CreditCard;
import br.com.unopay.api.billing.model.CurrencyCode;
import br.com.unopay.api.billing.model.PaymentMethod;
import br.com.unopay.api.billing.model.PaymentRequest;
import br.com.unopay.api.billing.model.Transaction;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.order.model.CreditOrder;
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
        }});

        Fixture.of(PaymentRequest.class).addTemplate("valid", new Rule() {{
            add("userId", regex("\\d{5}"));
            add("method", PaymentMethod.CARD);
            add("value", random(BigDecimal.class, range(40,1000)));
            add("orderId", regex("\\d{8}"));
            add("creditCard", one(CreditCard.class, "payzenCard"));
        }});

        Fixture.of(Transaction.class).addTemplate("withCard").inherits("valid", new Rule() {{
            add("paymentMethod", PaymentMethod.CARD);
        }});

        Fixture.of(Transaction.class).addTemplate("withTicket").inherits("valid", new Rule() {{
            add("paymentMethod", PaymentMethod.CARD);
        }});

        Fixture.of(Amount.class).addTemplate("valid", new Rule() {{
            add("currency", CurrencyCode.BRL);
            add("value", random(BigDecimal.class, range(30, 500)));
        }});

        Fixture.of(CreditOrder.class).addTemplate("valid", new Rule() {{
            add("createDateTime", instant("now"));
            add("email", random("jose@gmail.com", "bento@terra.com.br", "tereza@yahoo.com"));
            add("product", one(Product.class, "valid"));
            add("person", one(Person.class, "physical"));
            add("number", regex("\\d{10}"));
        }});

        Fixture.of(CreditCard.class).addTemplate("payzenCard", new Rule() {{
            add("expiryMonth", random("08", "12", "02"));
            add("expiryYear", random("2025", "2020", "2030"));
            add("holderName", firstName());
            add("number", random("36000000000008", "378282000000008"));
            add("securityCode", regex("\\d{3}"));
        }});
    }
}
