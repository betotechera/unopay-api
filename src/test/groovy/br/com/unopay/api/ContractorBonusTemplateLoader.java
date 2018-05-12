package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.market.model.BonusSituation;
import br.com.unopay.api.market.model.ContractorBonus;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Product;

import java.math.BigDecimal;

public class ContractorBonusTemplateLoader implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(ContractorBonus.class).addTemplate("valid", new Rule(){{
            add("product", one(Product.class, "valid"));
            add("payer", one(Person.class, "legal"));
            add("contractor", one(Contractor.class, "valid"));
            add("earnedBonus", random(BigDecimal.class, range(10,300)));
            add("createdDateTime", instant("now"));
        }});

        Fixture.of(ContractorBonus.class).addTemplate("processed").inherits("valid", new Rule(){{
            add("situation", BonusSituation.PROCESSED);
            add("processedAt", instant("now"));
        }});
    }
}
