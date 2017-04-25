package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.PaymentInstrumentSituation;
import br.com.unopay.api.model.PaymentInstrumentType;
import br.com.unopay.api.model.Product;
import java.text.SimpleDateFormat;

public class InstrumentTemplateLoader  implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(PaymentInstrument.class).addTemplate("valid", new Rule(){{
            add("type", random(PaymentInstrumentType.class));
            add("number", random("5646416546564654", "ADSFADSF57546646", "AAAAAAAAAAAAAAAAAAAA"));
            add("product", one(Product.class, "valid"));
            add("contractor", one(Contractor.class, "valid"));
            add("createdDate", beforeDate("24/04/2017", new SimpleDateFormat("dd/MM/yyyy")));
            add("expirationDate", afterDate("24/04/2017", new SimpleDateFormat("dd/MM/yyyy")));
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

    }
}