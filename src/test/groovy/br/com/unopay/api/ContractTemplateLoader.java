package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractEstablishment;
import br.com.unopay.api.model.ContractInstallment;
import br.com.unopay.api.model.ContractSituation;
import br.com.unopay.api.credit.model.CreditInsertionType;
import br.com.unopay.api.model.PaymentInstrumentType;
import br.com.unopay.api.model.Product;
import java.math.BigDecimal;
import java.util.Arrays;

public class ContractTemplateLoader implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(Contract.class).addTemplate("valid", new Rule(){{
            add("code", uniqueRandom(1l,200l,400l,5000l,3000l,201001l,  88888l, 556666l));
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
            add("documentNumberInvoice", regex("\\d{4}\\w{15}"));
            add("annuity", random(BigDecimal.class, range(100,300)));
            add("membershipFee", random(BigDecimal.class, range(50,150)));
            add("paymentInstallments", random(Integer.class, range(1, 12)));
        }});

        Fixture.of(Contract.class).addTemplate("endedNow").inherits("valid", new Rule() {{
            add("begin", instant("5 day ago"));
            add("end", instant("now"));
        }});

        Fixture.of(ContractEstablishment.class).addTemplate("valid", new Rule() {{
            add("creation", instant("now"));
        }});

        Fixture.of(ContractInstallment.class).addTemplate("valid", new Rule(){{
            add("installmentNumber", random(Integer.class, range(1,12)));
            add("expiration", instant("1 day from now"));
            add("value", random(BigDecimal.class, range(10, 80)));
            add("paymentValue", random(BigDecimal.class, range(10, 80)));
            add("paymentDateTime", instant("2 day from now"));
        }});

    }
}
