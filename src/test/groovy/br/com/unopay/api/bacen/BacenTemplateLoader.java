package br.com.unopay.api.bacen;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.*;
import br.com.unopay.api.model.Document;
import br.com.unopay.api.model.Person;

import java.util.Random;


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
        Fixture.of(Institution.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("paymentRuleGroup", one(PaymentRuleGroup.class, "persisted"));
        }});

        Fixture.of(Institution.class).addTemplate("persisted", new Rule(){{
            add("id", "1");
            add("person", one(Person.class, "legal"));
            add("paymentRuleGroups", one(PaymentRuleGroup.class, "persisted"));
        }});

        Fixture.of(Issuer.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("paymentRuleGroups", has(1).of(PaymentRuleGroup.class, "persisted"));
            add("tax", random(Double.class));
            add("paymentAccount", one(BankAccount.class, "persisted"));
            add("movementAccount", one(BankAccount.class, "persisted"));
        }});

        Fixture.of(BankAccount.class).addTemplate("persisted", new Rule(){{
            add("id", random("1", "2"));
            add("bank", one(Bank.class, "valid"));
            add("agency", random("6465", "55794", "004456"));
            add("dvAgency", random("a2", "1", "A"));
            add("accountNumber", random("1649879", "0021547869", "88564", "2233"));
            add("dvAccountNumber", random("a2", "1", "A"));
            add("type", random(BankAccountType.class));
        }});

        Fixture.of(BankAccount.class).addTemplate("valid", new Rule(){{
            add("bank", one(Bank.class, "valid"));
            add("agency", random("6465", "55794", "004456"));
            add("dvAgency", random("a2", "1", "A"));
            add("accountNumber", random("1649879", "0021547869", "88564", "2233"));
            add("dvAccountNumber", random("a2", "1", "A"));
            add("type", random(BankAccountType.class));
        }});


        Fixture.of(Bank.class).addTemplate("valid", new Rule(){{
            add("bacenCode", random(341, 1, 753,555));
            add("name", random("Itau", "Bradesco", "BB"));
        }});

    }
}
