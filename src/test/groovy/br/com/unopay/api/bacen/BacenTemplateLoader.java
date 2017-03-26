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
            add("code", uniqueRandom("1111","2222","3333","4444","5555","6666","7777","8888","9999"));
            add("name", "Arranjo");
            add("purpose", Purpose.BUY);
            add("scope", Scope.DOMESTIC);
            add("userRelationship", UserRelationship.POSTPAID);
        }});
        Fixture.of(PaymentRuleGroup.class).addTemplate("without-name", new Rule(){{
            add("code", uniqueRandom("1111","2222","3333","4444","5555","6666","7777","8888","9999"));
            add("purpose", Purpose.BUY);
            add("scope", Scope.DOMESTIC);
            add("userRelationship", UserRelationship.POSTPAID);
        }});
        Fixture.of(PaymentRuleGroup.class).addTemplate("without-code", new Rule(){{
            add("name", "Arranjo");
            add("purpose", Purpose.BUY);
            add("scope", Scope.DOMESTIC);
            add("userRelationship", UserRelationship.POSTPAID);
        }});


        Fixture.of(PaymentRuleGroup.class).addTemplate("persisted", new Rule(){{
            add("id", "1");
            add("code", "1234");
            add("name", "Arranjo");
            add("purpose", Purpose.BUY);
            add("scope", Scope.DOMESTIC);
            add("userRelationship", UserRelationship.POSTPAID);
        }});


    }
}
