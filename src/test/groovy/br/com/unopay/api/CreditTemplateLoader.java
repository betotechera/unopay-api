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
            add("number", random("9a7f16d6",
                    "a6f76a78-5b9c", "42bebfbd-02a7",
                    "20aeb4c8-5d4a", "62f0f5f4-de11",
                    "d5756a65-cf17", "b39204ad-fc90",
                    "0d029ab6-e8e4", "06f25495-77ec"));
            add("product", one(Product.class, "valid"));
            add("contractor", one(Contractor.class, "valid"));
            add("createdDate", instant("now"));
            add("expirationDate", afterDate(new SimpleDateFormat("yyyy-MM-dd")
                    .format(new Date()), new SimpleDateFormat("yyyy-MM-dd")));
            add("password", random("AABCD555", "!@#$$%#$#KMKK", "@##$$$$$"));
            add("situation", random(PaymentInstrumentSituation.class));
            add("externalNumberId", random("9a7f16d6-a974-4730-a929-b167c0ad30f0",
                    "a6f76a78-5b9c-4083-835f-44ad879a99ea", "42bebfbd-02a7-4094-a123-712d96b1a2d7",
                    "20aeb4c8-5d4a-4d05-8ac5-dc5474ef6699", "62f0f5f4-de11-44e6-afa9-d834c2b33287",
                    "d5756a65-cf17-4bec-82ae-1f2fb389f49f", "b39204ad-fc90-46eb-9bba-b1247bc24874",
                    "0d029ab6-e8e4-4817-ba57-9222c8b9bd01", "06f25495-77ec-4e30-b5aa-aa257d677c4f"));
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
            add("serviceType", random(ServiceType.class));
            add("creditInsertionType",random(CreditInsertionType.class));
            add("creditNumber", random(Long.class));
            add("createdDateTime",beforeDate("24/04/2017", new SimpleDateFormat("dd/MM/yyyy")));
            add("value",random(BigDecimal.class, range(1, 200)));
            add("situation",random(CreditSituation.class));
            add("creditSource", firstName());
            add("cnabId",random("56465456", "78979879897"));
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
            add("value",random(BigDecimal.class, range(1, 200)));
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
            add("value",random(BigDecimal.class, range(1, 200)));
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