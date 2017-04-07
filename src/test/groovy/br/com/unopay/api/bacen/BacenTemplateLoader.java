package br.com.unopay.api.bacen;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.*;
import br.com.unopay.api.model.Person;

import java.math.BigDecimal;


public class BacenTemplateLoader implements TemplateLoader {
    @Override
    public void load() {

        Fixture.of(PaymentRuleGroup.class).addTemplate("valid", new Rule(){{
            add("code", uniqueRandom("1111","2222","3333","4444","5555","6666","7777","8888","9999"));
            add("name", "Arranjo");
            add("institution", one(Institution.class, "persisted"));
            add("purpose", Purpose.BUY);
            add("scope", Scope.DOMESTIC);
            add("userRelationship", UserRelationship.POSTPAID);
        }});

        Fixture.of(PaymentRuleGroup.class).addTemplate("without-institution", new Rule(){{
            add("code", uniqueRandom("1111","2222","3333","4444","5555","6666","7777","8888","9999"));
            add("name", "Arranjo");
            add("purpose", Purpose.BUY);
            add("scope", Scope.DOMESTIC);
            add("userRelationship", UserRelationship.POSTPAID);
        }});

        Fixture.of(PaymentRuleGroup.class).addTemplate("without-name", new Rule(){{
            add("code", uniqueRandom("1111","2222","3333","4444","5555","6666","7777","8888","9999"));
            add("institution", one(Institution.class, "persisted"));
            add("purpose", Purpose.BUY);
            add("scope", Scope.DOMESTIC);
            add("userRelationship", UserRelationship.POSTPAID);
        }});
        Fixture.of(PaymentRuleGroup.class).addTemplate("without-code", new Rule(){{
            add("name", "Arranjo");
            add("purpose", Purpose.BUY);
            add("institution", one(Institution.class, "persisted"));
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
        }});

        Fixture.of(Institution.class).addTemplate("persisted", new Rule(){{
            add("id", "1");
            add("person", one(Person.class, "legal"));
        }});

        Fixture.of(Institution.class).addTemplate("user", new Rule(){{
            add("id", "2");
            add("person", one(Person.class, "legal"));
        }});


        Fixture.of(Issuer.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("paymentRuleGroups", has(1).of(PaymentRuleGroup.class, "persisted"));
            add("tax", random(Double.class));
            add("paymentAccount", one(PaymentBankAccount.class, "valid"));
            add("movementAccount", one(BankAccount.class, "persisted"));
        }});
        Fixture.of(AccreditedNetwork.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("paymentRuleGroups", has(1).of(PaymentRuleGroup.class, "persisted"));
            add("merchantDiscountRate", random(Double.class,range(0D,1.0D)));
            add("bankAccount", one(BankAccount.class, "persisted"));
            add("type", random((AccreditedNetworkType.class)));
            add("paymentMethod", one(PaymentMethod.class,"valid"));
            add("invoiceReceipt", one(InvoiceReceipt.class,"valid"));
        }});

        Fixture.of(PaymentMethod.class).addTemplate("valid", new Rule(){{
            add("movementPeriod", random(RecurrencePeriod.class));
            add("authorizeTransfer", random(true,false));
            add("minimumDepositValue", random(Double.class,range(1D,1000D)));
            add("closingPaymentDays", random(Integer.class,range(1,31)));
        }});

        Fixture.of(InvoiceReceipt.class).addTemplate("valid", new Rule(){{
            add("period", random(RecurrencePeriod.class));
            add("type", random( InvoiceReceiptType.class));
        }});


        Fixture.of(BankAccount.class).addTemplate("persisted", new Rule(){{
            add("id", random("1", "2"));
            add("bank", one(Bank.class, "valid"));
            add("agency", random("6465", "55794", "004456"));
            add("agencyDigit", random("a2", "1", "A"));
            add("accountNumber", random("1649879", "0021547869", "88564", "2233"));
            add("accountNumberDigit", random("a2", "1", "A"));
            add("type", random(BankAccountType.class));
        }});

        Fixture.of(BankAccount.class).addTemplate("valid", new Rule(){{
            add("bank", one(Bank.class, "valid"));
            add("agency", random("6465", "55794", "004456"));
            add("agencyDigit", random("a2", "1", "A"));
            add("accountNumber", random("1649879", "0021547869", "88564", "2233"));
            add("accountNumberDigit", random("a2", "1", "A"));
            add("type", random(BankAccountType.class));
        }});

        Fixture.of(PaymentBankAccount.class).addTemplate("valid", new Rule(){{
            add("bankAccount", one(BankAccount.class, "persisted"));
            add("authorizeTransfer", random(Boolean.class));
            add("depositPeriod", random(RecurrencePeriod.class));
            add("minimumDepositValue", random(Double.class,range(1D,1000D)));
            add("closingPaymentDays", random(Integer.class,range(1,31)));
        }});

        Fixture.of(Service.class).addTemplate("valid", new Rule(){{
            add("name", firstName());
            add("code", random(Integer.class));
            add("type", random(ServiceType.class));
            add("taxVal", random(BigDecimal.class));
            add("taxPercent", random(Double.class));
        }});

        Fixture.of(Service.class).addTemplate("persisted", new Rule(){{
            add("id", "1");
            add("name", firstName());
            add("type", random(ServiceType.class));
            add("code", random(Integer.class));
            add("taxVal", random(BigDecimal.class));
            add("taxPercent", random(Double.class));
        }});

        Fixture.of(Event.class).addTemplate("valid", new Rule(){{
            add("service", one(Service.class, "persisted"));
            add("ncmCode", random("445661", "879879"));
            add("name", firstName());
            add("requestQuantity",random(Boolean.class));
            add("quantityUnity",random("litros", "arroba"));
        }});

        Fixture.of(Bank.class).addTemplate("valid", new Rule(){{
            add("bacenCode", random(341, 1, 33));
            add("name", random("Itau", "Bradesco", "BB"));
        }});

    }
}
