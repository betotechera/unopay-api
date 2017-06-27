package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractEstablishment;
import br.com.unopay.api.model.ContractSituation;
import br.com.unopay.api.model.CreditInsertionType;
import br.com.unopay.api.model.PaymentInstrumentType;
import br.com.unopay.api.model.Product;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ContractTemplateLoader implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(Contract.class).addTemplate("valid", new Rule(){{
            add("code", uniqueRandom(1,200,400,5000,3000,201001,  88888, 556666));
            add("name", firstName());
            add("product", one(Product.class, "valid"));
            add("hirer", one(Hirer.class, "valid"));
            add("contractor", one(Contractor.class, "valid"));
            add("paymentInstrumentType", random(PaymentInstrumentType.class));
            add("serviceType",Arrays.asList(ServiceType.values()));
            add("creditInsertionType", random(CreditInsertionType.class));
            add("begin", instant("now"));
            add("end", instant("2 days from now"));
            add("situation", ContractSituation.ACTIVE);
            add("rntrc", regex("\\d{4}\\w{6}"));
            add("documentNumberInvoice", regex("\\d{4}\\w{15}"));
        }});

        Fixture.of(Contract.class).addTemplate("endedNow").inherits("valid", new Rule() {{
            add("begin", instant("5 day ago"));
            add("end", instant("now"));
        }});

        Fixture.of(ContractEstablishment.class).addTemplate("valid", new Rule() {{
            add("creation", instant("now"));
        }});

    }
}
