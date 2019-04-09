package br.com.unopay.api.bacen;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.Bank;
import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.bacen.model.BankAccountType;
import br.com.unopay.api.bacen.model.Checkout;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.HirerBranch;
import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.InvoiceReceipt;
import br.com.unopay.api.bacen.model.InvoiceReceiptType;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.Purpose;
import br.com.unopay.api.bacen.model.RecurrencePeriod;
import br.com.unopay.api.bacen.model.Scope;
import br.com.unopay.api.bacen.model.UserRelationship;
import br.com.unopay.api.model.BrandFlag;
import br.com.unopay.api.model.Contact;
import br.com.unopay.api.model.Person;
import java.math.BigDecimal;


public class BacenTemplateLoader implements TemplateLoader {
    @Override
    public void load() {

        Fixture.of(PaymentRuleGroup.class).addTemplate("valid", new Rule(){{
            add("code", regex("\\d{8,10}"));
            add("name", "Arranjo");
            add("institution", one(Institution.class, "persisted"));
            add("purpose", uniqueRandom(Purpose.class));
            add("scope", uniqueRandom(Scope.class));
            add("minimumCreditInsertion", new BigDecimal(0.1));
            add("maximumCreditInsertion", new BigDecimal(9000000.00));
            add("userRelationship", uniqueRandom(UserRelationship.class));
        }});

        Fixture.of(PaymentRuleGroup.class).addTemplate("default").inherits("valid", new Rule(){{
            add("code", "01");
        }});

        Fixture.of(PaymentRuleGroup.class).addTemplate("without-institution").inherits("valid", new Rule(){{
            add("institution", null);
        }});

        Fixture.of(PaymentRuleGroup.class).addTemplate("without-name").inherits("valid", new Rule(){{
            add("name", null);
        }});

        Fixture.of(PaymentRuleGroup.class).addTemplate("without-code").inherits("valid", new Rule(){{
            add("code", null);
        }});

        Fixture.of(PaymentRuleGroup.class).addTemplate("persisted").inherits("valid", new Rule(){{
            add("id", "1");
        }});

        Fixture.of(Institution.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
        }});

        Fixture.of(Hirer.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("bankAccount", one(BankAccount.class, "persisted"));
            add("financierMail", "nome@teste.com");
            add("creditRecurrencePeriod", random(RecurrencePeriod.class));
        }});

        Fixture.of(Hirer.class).addTemplate("persisted").inherits("valid", new Rule(){{
            add("id", "1");
        }});

        Fixture.of(HirerBranch.class).addTemplate("valid", new Rule(){{
            add("headOffice", one(Hirer.class, "persisted"));
            add("person", one(Person.class, "legal"));
            add("bankAccount", one(BankAccount.class, "persisted"));
        }});

        Fixture.of(Contractor.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("bankAccount", one(BankAccount.class, "persisted"));
        }});

        Fixture.of(Contractor.class).addTemplate("physical").inherits("valid", new Rule(){{
            add("person", one(Person.class, "physical"));
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
            add("creditCardFee", new BigDecimal(0.1));
            add("paymentRuleGroups", has(1).of(PaymentRuleGroup.class, "persisted"));
            add("fee", random(Double.class));
            add("paymentAccount", one(PaymentBankAccount.class, "valid"));
            add("movementAccount", one(BankAccount.class, "persisted"));
            add("financierMailForRemittance", "financeiro@gmail.com");
            add("bin", regex("\\d{4}"));

        }});

        Fixture.of(Checkout.class).addTemplate("valid", new Rule(){{
            add("period", random(RecurrencePeriod.class));
            add("authorizeTransfer", random(Boolean.class));
            add("minimumDepositValue", random(Double.class,range(1D,1000D)));
            add("closingPaymentDays", random(Integer.class,range(1,31)));
        }});

        Fixture.of(InvoiceReceipt.class).addTemplate("valid", new Rule(){{
            add("period", uniqueRandom(RecurrencePeriod.class));
            add("type", uniqueRandom(InvoiceReceiptType.class));
        }});

        Fixture.of(BankAccount.class).addTemplate("valid", new Rule(){{
            add("bank", one(Bank.class, "valid"));
            add("agency", regex("\\d{4}"));
            add("agencyDigit", random("a2", "1", "A"));
            add("accountNumber", random("1649879", "0021547869", "88564", "2233"));
            add("accountNumberDigit", random("a2", "1", "A"));
            add("type", random(BankAccountType.class));
        }});

        Fixture.of(BankAccount.class).addTemplate("persisted").inherits("valid", new Rule(){{
            add("id", random("1", "2"));
        }});

        Fixture.of(PaymentBankAccount.class).addTemplate("valid", new Rule(){{
            add("bankAccount", one(BankAccount.class, "persisted"));
            add("authorizeTransfer", random(Boolean.class));
            add("depositPeriod", random(RecurrencePeriod.class));
            add("postPaidPaymentDays", random(Integer.class,range(1,31)));
            add("prePaidPaymentDays", random(Integer.class,range(1,31)));
            add("bankAgreementNumberForCredit", regex("\\d{7}"));
            add("bankAgreementNumberForDebit", regex("\\d{7}"));
            add("station", "2XHI");
            add("walletNumber", regex("\\d{3}"));
        }});

        Fixture.of(Bank.class).addTemplate("valid", new Rule(){{
            add("bacenCode", uniqueRandom(341, 318, 33));
            add("name", uniqueRandom("Itau", "BMG", "SANTANDER"));
        }});

        Fixture.of(BrandFlag.class).addTemplate("persisted", new Rule(){{
            add("id", "1");
            add("name", random("Visa", "Master", "Elo"));
            add("description", firstName());
        }});

        Fixture.of(Contact.class).addTemplate("valid", new Rule(){{
            add("name",firstName());
            add("mail", random("${name}@gmail.com", "${name}@uol.com.br", "${name}@new.me"));
            add("cellPhone", random("1199557788", "(11) 99556-8866"));
            add("phone", random("1199557788", "(11) 99556-8866"));
        }});

        Fixture.of(Contact.class).addTemplate("persisted").inherits("valid", new Rule(){{
            add("id", "1");
        }});

    }
}
