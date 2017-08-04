package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractorCreditType;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.Credit;
import br.com.unopay.api.model.CreditInsertionType;
import br.com.unopay.api.model.CreditPaymentAccount;
import br.com.unopay.api.model.CreditSituation;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.PaymentInstrumentSituation;
import br.com.unopay.api.model.PaymentInstrumentType;
import br.com.unopay.api.model.Product;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreditTemplateLoader implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(PaymentInstrument.class).addTemplate("valid", new Rule(){{
            add("type", random(PaymentInstrumentType.class));
            add("number", regex("\\d{5}\\.\\w{5}"));
            add("product", one(Product.class, "valid"));
            add("contractor", one(Contractor.class, "valid"));
            add("createdDate", instant("now"));
            add("expirationDate", afterDate(new SimpleDateFormat("yyyy-MM-dd")
                    .format(new Date()), new SimpleDateFormat("yyyy-MM-dd")));
            add("password", regex("\\W{3}\\w{3}\\d{2}"));
            add("situation", random(PaymentInstrumentSituation.class));
            add("externalNumberId", regex("\\d{10}\\-\\w{15}\\-\\d{10}"));
        }});

        Fixture.of(PaymentInstrument.class).addTemplate("without-product")
                .inherits("valid", new Rule(){{
            add("product", null);
        }});

        Fixture.of(PaymentInstrument.class).addTemplate("without-contractor")
                .inherits("valid", new Rule(){{
            add("contractor", null);
        }});

        Fixture.of(Credit.class).addTemplate("allFields", new Rule(){{
            add("product", one(Product.class, "valid"));
            add("paymentRuleGroup",one(PaymentRuleGroup.class, "valid"));
            add("hirerDocument",cnpj());
            add("issuerDocument",cnpj());
            add("serviceType", random(ServiceType.class));
            add("creditInsertionType",random(CreditInsertionType.class));
            add("creditNumber", random(Long.class));
            add("createdDateTime",beforeDate("24/04/2017", new SimpleDateFormat("dd/MM/yyyy")));
            add("value",random(BigDecimal.class, range(1, 200)));
            add("situation",random(CreditSituation.class));
            add("creditSource", firstName());
            add("cnabId",regex("\\d{10}"));
            add("availableValue", random(BigDecimal.class, range(1, 200)));
            add("blockedValue",random(BigDecimal.class, range(1, 200)));
        }});

        Fixture.of(Credit.class).addTemplate("withProduct").inherits("allFields", new Rule(){{
            add("product", null);
        }});

        Fixture.of(Credit.class).addTemplate("withoutPaymentRuleGroup")
                .inherits("allFields", new Rule(){{
            add("paymentRuleGroup", null);
        }});

        Fixture.of(Credit.class).addTemplate("withoutProductAndPaymentRuleGroup")
                .inherits("allFields", new Rule(){{
            add("paymentRuleGroup", null);
            add("product", null);
        }});

        Fixture.of(Credit.class).addTemplate("withoutProductAndCreditInsertionType")
                .inherits("allFields", new Rule(){{
            add("creditInsertionType", null);
            add("product", null);
        }});

        Fixture.of(CreditPaymentAccount.class).addTemplate("valid", new Rule(){{
            add("transactionCreatedDateTime",beforeDate("24/04/2017",
                    new SimpleDateFormat("dd/MM/yyyy")));
            add("issuer", one(Issuer.class, "valid"));
            add("product", one(Product.class, "valid"));
            add("paymentRuleGroup",one(PaymentRuleGroup.class, "valid"));
            add("hirerDocument",cnpj());
            add("serviceType",random(ServiceType.class));
            add("creditInsertionType",random(CreditInsertionType.class));
            add("solicitationDateTime",beforeDate("24/04/2017",
                    new SimpleDateFormat("dd/MM/yyyy")));
            add("creditNumber",random(Long.class));
            add("insertionCreatedDateTime",beforeDate("24/04/2017",
                    new SimpleDateFormat("dd/MM/yyyy")));
            add("value",random(BigDecimal.class, range(21, 200)));
            add("situation",random(CreditSituation.class));
            add("creditSource", firstName());
            add("availableBalance", random(BigDecimal.class, range(201, 400)));
            add("paymentAccount", "AAA");
        }});

        Fixture.of(ContractorInstrumentCredit.class).addTemplate("allFields", new Rule(){{
            add("paymentInstrument",one(PaymentInstrument.class, "valid"));
            add("contract",one(Contract.class, "valid"));
            add("serviceType", random(ServiceType.FUEL_ALLOWANCE, ServiceType.FREIGHT_RECEIPT));
            add("creditInsertionType",random(CreditInsertionType.class));
            add("installmentNumber",random(Long.class));
            add("value",random(BigDecimal.class, range(21, 200)));
            add("expirationDateTime",instant("1 day from now"));
            add("issuerFee",random(BigDecimal.class, range(1, 200)));
            add("creditPaymentAccount", one(CreditPaymentAccount.class, "valid"));
            add("situation",CreditSituation.AVAILABLE);
            add("availableBalance", random(BigDecimal.class, range(201, 400)));
            add("blockedBalance",random(BigDecimal.class));
            add("createdDateTime", instant("1 second from now"));
            add("creditType", random(ContractorCreditType.class));
        }});

        Fixture.of(ContractorInstrumentCredit.class).addTemplate("toPersist")
                .inherits("allFields", new Rule(){{
            add("contract", null);
            add("creditPaymentAccount", null);
        }});

    }
}