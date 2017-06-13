package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.model.CreditInsertionType;
import static br.com.unopay.api.model.CreditInsertionType.BOLETO;
import static br.com.unopay.api.model.CreditInsertionType.CREDIT_CARD;
import static br.com.unopay.api.model.CreditInsertionType.PAMCARD_SYSTEM;
import br.com.unopay.api.model.PaymentInstrumentType;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.ProductSituation;
import br.com.unopay.api.model.ProductType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ProductTemplateLoader implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(Product.class).addTemplate("valid", new Rule(){{
            Set<CreditInsertionType> creditInsertionTypes = new HashSet<CreditInsertionType>(){{{
                addAll(Arrays.asList(CreditInsertionType.values()));
            }}};

            Set<ServiceType> serviceTypes = new HashSet<ServiceType>(){{{
                addAll(Arrays.asList(ServiceType.values()));
            }}};

            Set<PaymentInstrumentType> paymentInstrumentTypes = new HashSet<PaymentInstrumentType>(){{{
                addAll(Arrays.asList(PaymentInstrumentType.values()));
            }}};

            add("code", random("AB12", "C124", "ABC1", "CC24", "CD24", "DC24", "AVC4", "AAD1"));
            add("name", firstName());
            add("type", uniqueRandom(ProductType.class));
            add("issuer", one(Issuer.class, "valid"));
            add("paymentRuleGroup", one(PaymentRuleGroup.class, "valid"));
            add("accreditedNetwork", one(AccreditedNetwork.class, "valid"));
            add("paymentInstrumentTypes", paymentInstrumentTypes);
            add("serviceTypes", serviceTypes);
            add("creditInsertionTypes", creditInsertionTypes);
            add("minimumCreditInsertion", random(BigDecimal.class, range(0.0, 0.1)));
            add("maximumCreditInsertion", random(BigDecimal.class, range(900, 9000000.00)));
            add("paymentInstrumentValidDays", random(Integer.class));
            add("situation", random(ProductSituation.class));
            add("membershipFee", random(BigDecimal.class));
            add("creditInsertionFee", random(BigDecimal.class));
            add("paymentInstrumentEmissionFee", random(BigDecimal.class));
            add("paymentInstrumentSecondCopyFee", random(BigDecimal.class));
            add("administrationCreditInsertionFee", random(BigDecimal.class, range(0.00, 1)));
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
