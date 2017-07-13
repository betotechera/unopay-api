package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.payment.model.PaymentOperationType;
import br.com.unopay.api.payment.model.PaymentRemittance;
import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import br.com.unopay.api.payment.model.PaymentServiceType;
import br.com.unopay.api.payment.model.PaymentTransferOption;
import br.com.unopay.api.payment.model.RemittanceSituation;
import java.math.BigDecimal;

public class PaymentRemittanceTemplateLoader implements TemplateLoader {

    @Override
    public void load() {

        Fixture.of(PaymentRemittance.class).addTemplate("valid", new Rule() {{
            add("issuerBankCode", random(1, 341));
            add("issuer", one(Issuer.class, "valid"));
            add("number", regex("\\d{10}"));
            add("paymentServiceType", uniqueRandom(PaymentServiceType.class));
            add("transferOption", uniqueRandom(PaymentTransferOption.class));
            add("operationType", uniqueRandom(PaymentOperationType.class));
            add("occurrenceCode", regex("\\d{2}\\w{5}"));
            add("createdDateTime", instant("now"));
            add("submissionDateTime", instant("1 hour from now"));
            add("situation", uniqueRandom(RemittanceSituation.class));
            add("submissionReturnDateTime", instant("2 days from now"));
        }});

        Fixture.of(PaymentRemittance.class).addTemplate("withItems").inherits("valid", new Rule() {{
            add("remittanceItems", has(2).of(PaymentRemittanceItem.class, "valid")
                    .targetAttribute("paymentRemittance"));
        }});

        Fixture.of(PaymentRemittanceItem.class).addTemplate("valid", new Rule() {{
            add("establishment",one(Establishment.class, "valid"));
            add("establishmentBankCode",random(1, 341));
            add("value",random(BigDecimal.class));
            add("situation", random(RemittanceSituation.class));
            add("occurrenceCode",regex("\\d{2}"));
        }});

    }
}
