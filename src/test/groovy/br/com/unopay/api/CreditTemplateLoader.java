package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.*;
import br.com.unopay.api.model.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreditTemplateLoader implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(PaymentInstrument.class).addTemplate("valid", new Rule(){{
            add("type", random(PaymentInstrumentType.class));
            add("number", random("5646416546564654", "ADSFADSF57546646", "AAAAAAAAAAAAAAAAAAAA"));
            add("product", one(Product.class, "valid"));
            add("contractor", one(Contractor.class, "valid"));
            add("createdDate", instant("now"));
            add("expirationDate", afterDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), new SimpleDateFormat("yyyy-MM-dd")));
            add("password", random("AABCD555", "!@#$$%#$#KMKK", "@##$$$$$"));
            add("situation", random(PaymentInstrumentSituation.class));
            add("externalNumberId", random("AAAAAAAAAA22222222222444444", "24d995e3-be96-40e8-870d-bba297375a70", "012346"));
        }});

        Fixture.of(PaymentInstrument.class).addTemplate("without-product").inherits("valid", new Rule(){{
            add("product", null);
        }});

        Fixture.of(PaymentInstrument.class).addTemplate("without-contractor").inherits("valid", new Rule(){{
            add("contractor", null);
        }});

        Fixture.of(Credit.class).addTemplate("allFields", new Rule(){{
            add("product", one(Product.class, "valid"));
            add("paymentRuleGroup",one(PaymentRuleGroup.class, "valid"));
            add("hirerDocument",cnpj());
            add("serviceType", random(ServiceType.class));
            add("creditInsertionType",random(CreditInsertionType.class));
            add("creditNumber", random(Long.class));
            add("createdDateTime",beforeDate("24/04/2017", new SimpleDateFormat("dd/MM/yyyy")));
            add("value",random(BigDecimal.class, range(1, 200)));
            add("situation",random(CreditSituation.class));
            add("creditSource", firstName());
            add("cnabId",random("56465456", "78979879897"));
            add("availableValue", random(BigDecimal.class));
            add("blockedValue",random(BigDecimal.class));
        }});

        Fixture.of(Credit.class).addTemplate("withProduct").inherits("allFields", new Rule(){{
            add("product", null);
        }});

        Fixture.of(Credit.class).addTemplate("withoutPaymentRuleGroup").inherits("allFields", new Rule(){{
            add("paymentRuleGroup", null);
        }});

        Fixture.of(Credit.class).addTemplate("withoutProductAndPaymentRuleGroup").inherits("allFields", new Rule(){{
            add("paymentRuleGroup", null);
            add("product", null);
        }});

        Fixture.of(Credit.class).addTemplate("withoutProductAndCreditInsertionType").inherits("allFields", new Rule(){{
            add("creditInsertionType", null);
            add("product", null);
        }});

        Fixture.of(CreditPaymentAccount.class).addTemplate("valid", new Rule(){{
            add("transactionCreatedDateTime",beforeDate("24/04/2017", new SimpleDateFormat("dd/MM/yyyy")));
            add("issuer", one(Issuer.class, "valid"));
            add("product", one(Product.class, "valid"));
            add("paymentRuleGroup",one(PaymentRuleGroup.class, "valid"));
            add("hirerDocument",cnpj());
            add("serviceType",random(ServiceType.class));
            add("creditInsertionType",random(CreditInsertionType.class));
            add("solicitationDateTime",beforeDate("24/04/2017", new SimpleDateFormat("dd/MM/yyyy")));
            add("creditNumber",random(Long.class));
            add("insertionCreatedDateTime",beforeDate("24/04/2017", new SimpleDateFormat("dd/MM/yyyy")));
            add("value",random(BigDecimal.class));
            add("situation",random(CreditSituation.class));
            add("creditSource", firstName());
            add("availableBalance", random(BigDecimal.class));
            add("paymentAccount", "AAA");
        }});

        Fixture.of(ContractorInstrumentCredit.class).addTemplate("allFields", new Rule(){{
            add("paymentInstrument",one(PaymentInstrument.class, "valid"));
            add("contract",one(Contract.class, "valid"));
            add("serviceType", random(ServiceType.class));
            add("creditInsertionType",random(CreditInsertionType.class));
            add("installmentNumber",random(Long.class));
            add("value",random(BigDecimal.class, range(1, 200)));
            add("expirationDateTime",instant("1 day from now"));
            add("issuerFee",random(BigDecimal.class, range(1, 200)));
            add("situation",random(CreditSituation.class));
            add("availableBalance", random(BigDecimal.class));
            add("blockedBalance",random(BigDecimal.class));
        }});

    }
}