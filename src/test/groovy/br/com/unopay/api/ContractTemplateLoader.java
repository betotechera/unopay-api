package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.*;
import br.com.unopay.api.model.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ContractTemplateLoader implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(Contract.class).addTemplate("valid", new Rule(){{
            add("code", uniqueRandom(1,200,400,5000,3000,201001));
            add("name", firstName());
            add("product", one(Product.class, "valid"));
            add("hirer", one(Hirer.class, "valid"));
            add("contractor", one(Contractor.class, "valid"));
            add("paymentInstrumentType", random(PaymentInstrumentType.class));
            add("serviceType", has(2).of(ServiceType.class));
            add("creditInsertionType", random(CreditInsertionType.class));
            add("begin", instant("now"));
            add("end", afterDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), new SimpleDateFormat("yyyy-MM-dd")));
            add("situation", random(ContractSituation.class));
            add("rntrc", random("65647988664", "564654698469479688", "SS454SAF564AS86S4DF"));
            add("documentNumberInvoice", random("SDFDSF65464SDF646SDF", "AAAAAADBBBBBBBBBB654D87498D9S7F97SDF"));
        }});

        Fixture.of(Contract.class).addTemplate("endedNow").inherits("valid", new Rule(){{
            add("begin", instant("5 day ago"));
            add("end", instant("now"));
        }});

    }
}
