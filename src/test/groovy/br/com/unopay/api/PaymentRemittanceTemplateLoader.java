package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.model.BatchClosingItem;
import br.com.unopay.api.payment.model.PaymentOperationType;
import br.com.unopay.api.payment.model.PaymentRemittance;
import br.com.unopay.api.payment.model.PaymentServiceType;
import br.com.unopay.api.payment.model.PaymentTransferOption;
import br.com.unopay.api.payment.model.RemittanceSituation;
import java.math.BigDecimal;

public class PaymentRemittanceTemplateLoader implements TemplateLoader {

    @Override
    public void load() {

        Fixture.of(PaymentRemittance.class).addTemplate("valid", new Rule() {{
            add("issuerBankCode", regex("\\W{4}"));
            add("remittanceNumber", regex("\\d{10}"));
            add("serviceType", uniqueRandom(PaymentServiceType.class));
            add("transferOption", uniqueRandom(PaymentTransferOption.class));
            add("operationType", uniqueRandom(PaymentOperationType.class));
            add("occurrenceCode", regex("\\d{2}\\W{4}"));
            add("createdDateTime", instant("now"));
            add("submissionDateTime", instant("1 hour from now"));
            add("situation", uniqueRandom(RemittanceSituation.class));
            add("submissionReturnDateTime", instant("2 days from now"));
        }});

        Fixture.of(BatchClosingItem.class).addTemplate("valid", new Rule() {{
            add("paymentRemittance", one(PaymentRemittance.class, "valid"));
            add("establishment",one(Establishment.class, "valid"));
            add("establishmentBankCode",regex("\\W{4}"));
            add("value",random(BigDecimal.class, range(1,10)));
            add("situation", random(RemittanceSituation.class));
            add("occurrenceCode",regex("\\d{2}"));
        }});

    }
}
