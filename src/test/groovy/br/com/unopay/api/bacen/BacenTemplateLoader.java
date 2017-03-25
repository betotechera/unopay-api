package br.com.unopay.api.bacen;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.Purpose;
import br.com.unopay.api.bacen.model.Scope;
import br.com.unopay.api.bacen.model.UserRelationship;


public class BacenTemplateLoader implements TemplateLoader {
    @Override
    public void load() {

        Fixture.of(PaymentRuleGroup.class).addTemplate("valid", new Rule(){{
            add("id", "1");
            add("code", uniqueRandom("MASTER", "SUPER"));
            add("name", uniqueRandom("MASTER", "SUPER"));
            add("purpose", Purpose.BUY);
            add("scope", Scope.DOMESTIC);
            add("userRelationship", UserRelationship.POSTPAID);
        }});

    }
}
