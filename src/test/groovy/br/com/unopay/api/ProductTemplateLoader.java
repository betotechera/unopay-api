package br.com.unopay.api;

import br.com.six2six.fixturefactory.*;
import br.com.six2six.fixturefactory.loader.*;
import br.com.unopay.api.bacen.model.*;
import br.com.unopay.api.model.*;
import static br.com.unopay.api.model.CreditInsertionType.*;
import java.math.*;
import java.util.*;

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
