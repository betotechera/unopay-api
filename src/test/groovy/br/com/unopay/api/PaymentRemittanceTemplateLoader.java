package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.Bank;
import br.com.unopay.api.payment.model.PaymentOperationType;
import br.com.unopay.api.payment.model.PaymentRemittance;
import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import br.com.unopay.api.payment.model.PaymentServiceType;
import br.com.unopay.api.payment.model.PaymentTransferOption;
import br.com.unopay.api.payment.model.RemittancePayee;
import br.com.unopay.api.payment.model.RemittancePayer;
import br.com.unopay.api.payment.model.RemittanceSituation;
import java.math.BigDecimal;

import static br.com.unopay.api.model.State.SP;

public class PaymentRemittanceTemplateLoader implements TemplateLoader {

    @Override
    public void load() {

        Fixture.of(PaymentRemittance.class).addTemplate("valid", new Rule() {{
            add("payer", one(RemittancePayer.class, "valid"));
            add("number", regex("\\d{6}"));
            add("paymentServiceType", uniqueRandom(PaymentServiceType.class));
            add("operationType", uniqueRandom(PaymentOperationType.class));
            add("occurrenceCode", regex("\\w{2}"));
            add("createdDateTime", instant("now"));
            add("submissionDateTime", instant("1 hour from now"));
            add("situation", uniqueRandom(RemittanceSituation.class));
            add("submissionReturnDateTime", instant("2 days from now"));
        }});

        Fixture.of(PaymentRemittance.class).addTemplate("withItems").inherits("valid", new Rule() {{
            add("remittanceItems", has(2).of(PaymentRemittanceItem.class, "valid"));
        }});

        Fixture.of(PaymentRemittanceItem.class).addTemplate("valid", new Rule() {{
            add("payee",one(RemittancePayee.class, "valid"));
            add("value",random(BigDecimal.class));
            add("transferOption", uniqueRandom(PaymentTransferOption.class));
            add("situation", random(RemittanceSituation.class));
            add("occurrenceCode",regex("\\d{2}"));
        }});

        Fixture.of(RemittancePayer.class).addTemplate("valid", new Rule() {{
            add("agency", regex("\\d{4}"));
            add("agencyDigit", random("a2", "1", "A"));
            add("accountNumber", random("1649879", "0021547869", "88564", "2233"));
            add("accountNumberDigit", random("a2", "1", "A"));
            add("zipCode", "05302030");
            add("streetName", "Rua aaaa");
            add("number", "12344");
            add("district", "ADDCA");
            add("city", "dadad");
            add("state", SP);
            add("bankAgreementNumberForCredit", regex("\\d{8}"));
            add("bankAgreementNumberForDebit", regex("\\d{8}"));
            add("documentNumber", regex("\\d{14}"));
            add("bankCode", uniqueRandom(341, 318, 33));
            add("name", firstName());
            add("bankName", firstName());
        }});

        Fixture.of(RemittancePayee.class).addTemplate("valid", new Rule() {{
            add("agency", regex("\\d{4}"));
            add("documentNumber", regex("\\d{14}"));
            add("bankCode", uniqueRandom(341, 318, 33));
            add("payerBankCode", uniqueRandom(341, 318, 33));
            add("agencyDigit", random("a2", "1", "A"));
            add("accountNumber", random("1649879", "0021547869", "88564", "2233"));
            add("accountNumberDigit", random("a2", "1", "A"));
            add("zipCode", "05302030");
            add("streetName", "Rua aaaa");
            add("number", "12344");
            add("district", "ADDCA");
            add("city", "dadad");
            add("state", SP);
            add("name", firstName());
            add("receivable", random(BigDecimal.class, range(1D,200D)));
        }});

    }
}
