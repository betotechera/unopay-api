package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.credit.model.CreditInsertionType;
import br.com.unopay.api.model.PaymentInstrumentType;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.ProductSituation;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static br.com.unopay.api.credit.model.CreditInsertionType.BOLETO;
import static br.com.unopay.api.credit.model.CreditInsertionType.CREDIT_CARD;

public class ProductTemplateLoader implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(Product.class).addTemplate("valid", new Rule(){{
            add("code", regex("\\w{2}\\d{2}"));
            add("name",  regex("\\w{15}"));
            add("issuer", one(Issuer.class, "valid"));
            add("paymentRuleGroup", one(PaymentRuleGroup.class, "valid"));
            add("accreditedNetwork", one(AccreditedNetwork.class, "valid"));
            add("paymentInstrumentTypes", has(1).of(PaymentInstrumentType.class));
            add("paymentInstrumentValidDays", random(Integer.class));
            add("situation", random(ProductSituation.class));
            add("membershipFee", random(BigDecimal.class));
            add("creditInsertionFee", random(BigDecimal.class));
            add("paymentInstrumentEmissionFee", random(BigDecimal.class));
            add("paymentInstrumentSecondCopyFee", random(BigDecimal.class));
            add("administrationCreditInsertionFee", random(BigDecimal.class, range(0.00, 1)));
            add("serviceTypes", Arrays.asList(ServiceType.FUEL_ALLOWANCE, ServiceType.FREIGHT_RECEIPT));
            add("creditInsertionTypes", Arrays.asList(CreditInsertionType.values()));
            add("contractValidityDays", 360);
            add("paymentInstallments", random(Integer.class, range(1,12)));
            add("annuity", random(BigDecimal.class, range(100, 250)));
        }});

        Fixture.of(Product.class).addTemplate("creditWithoutDirectDebit").inherits("valid", new Rule(){{
            Set<CreditInsertionType> creditInsertionTypes = new HashSet<CreditInsertionType>(){{{
                add(BOLETO);
                add(CREDIT_CARD);
            }}};
            add("creditInsertionTypes", creditInsertionTypes);
        }});
    }
}
