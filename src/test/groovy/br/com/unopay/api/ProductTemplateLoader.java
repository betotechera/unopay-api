package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.model.CreditInsertionType;
import br.com.unopay.api.model.PaymentInstrumentType;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.ProductSituation;
import br.com.unopay.api.model.ProductType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static br.com.unopay.api.model.CreditInsertionType.BOLETO;
import static br.com.unopay.api.model.CreditInsertionType.CREDIT_CARD;
import static br.com.unopay.api.model.CreditInsertionType.PAMCARD_SYSTEM;

public class ProductTemplateLoader implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(Product.class).addTemplate("valid", new Rule(){{
            add("code", regex("\\w{2}\\d{2}"));
            add("name",  regex("\\w{15}"));
            add("type", uniqueRandom(ProductType.class));
            add("issuer", one(Issuer.class, "valid"));
            add("paymentRuleGroup", one(PaymentRuleGroup.class, "valid"));
            add("accreditedNetwork", one(AccreditedNetwork.class, "valid"));
            add("paymentInstrumentTypes", has(1).of(PaymentInstrumentType.class));
            add("minimumCreditInsertion", random(BigDecimal.class, range(0.0, 0.1)));
            add("maximumCreditInsertion", random(BigDecimal.class, range(900, 9000000.00)));
            add("paymentInstrumentValidDays", random(Integer.class));
            add("situation", random(ProductSituation.class));
            add("membershipFee", random(BigDecimal.class));
            add("creditInsertionFee", random(BigDecimal.class));
            add("paymentInstrumentEmissionFee", random(BigDecimal.class));
            add("paymentInstrumentSecondCopyFee", random(BigDecimal.class));
            add("administrationCreditInsertionFee", random(BigDecimal.class, range(0.00, 1)));
            add("serviceTypes", Arrays.asList(ServiceType.FUEL_ALLOWANCE, ServiceType.FREIGHT_RECEIPT));
            add("creditInsertionTypes", Arrays.asList(CreditInsertionType.values()));
        }});

        Fixture.of(Product.class).addTemplate("creditWithoutDirectDebit").inherits("valid", new Rule(){{
            Set<CreditInsertionType> creditInsertionTypes = new HashSet<CreditInsertionType>(){{{
                add(BOLETO);
                add(CREDIT_CARD);
                add(PAMCARD_SYSTEM);
            }}};
            add("creditInsertionTypes", creditInsertionTypes);
        }});
    }
}
