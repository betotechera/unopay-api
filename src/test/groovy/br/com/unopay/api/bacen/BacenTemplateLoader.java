package br.com.unopay.api.bacen;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.*;
import br.com.unopay.api.model.BrandFlag;
import br.com.unopay.api.model.Contact;
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

        Fixture.of(Hirer.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("bankAccount", one(BankAccount.class, "persisted"));
        }});

        Fixture.of(Partner.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("bankAccount", one(BankAccount.class, "persisted"));
        }});
        Fixture.of(Hirer.class).addTemplate("persisted", new Rule(){{
            add("id", "1");
            add("person", one(Person.class, "legal"));
            add("bankAccount", one(BankAccount.class, "persisted"));
        }});

        Fixture.of(HirerBranch.class).addTemplate("valid", new Rule(){{
            add("headOffice", one(Hirer.class, "persisted"));
            add("person", one(Person.class, "legal"));
            add("bankAccount", one(BankAccount.class, "persisted"));
        }});

        Fixture.of(Hired.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("rntrc", random("65647988664", "564654698469479688", "SS454SAF564AS8FA6S4DF"));
            add("bankAccount", one(BankAccount.class, "persisted"));
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
            add("checkout", one(Checkout.class,"valid"));
            add("invoiceReceipt", one(InvoiceReceipt.class,"valid"));
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
            add("postPaidPaymentDays", random(Integer.class,range(1,31)));
            add("prePaidPaymentDays", random(Integer.class,range(1,31)));
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
            add("bacenCode", uniqueRandom(341, 1, 33));
            add("name", uniqueRandom("Itau", "Bradesco", "BB"));
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

        Fixture.of(Contact.class).addTemplate("persisted", new Rule(){{
            add("id","1");
            add("name",firstName());
            add("mail", random("${name}@gmail.com", "${name}@uol.com.br", "${name}@new.me"));
            add("cellPhone", random("1199557788", "(11) 99556-8866"));
            add("phone", random("1199557788", "(11) 99556-8866"));
        }});

        Fixture.of(Establishment.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("type", random(EstablishmentType.class));
            add("contactMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("invoiceMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("bachShipmentMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("alternativeMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me", null));
            add("cancellationTolerance", random(Integer.class, range(0, 60)));
            add("tax", random(Double.class, range(0.00, 1)));
            add("network", one(AccreditedNetwork.class, "valid"));
            add("brandFlag", one(BrandFlag.class, "persisted"));
            add("logoUri", "/tmp/path");
            add("operationalContact", one(Contact.class, "persisted"));
            add("administrativeContact", one(Contact.class, "persisted"));
            add("financierContact", one(Contact.class, "persisted"));
            add("technicalContact", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("establishmentPhotoUri", "/tmp/path");
            add("contractUri", "/tmp/path");
            add("gatheringChannel", random(GatheringChannel.class));
            add("bankAccount", one(BankAccount.class, "persisted"));
            add("checkout", one(Checkout.class,"valid"));
        }});

        Fixture.of(Branch.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("headOffice", one(Establishment.class, "valid"));
            add("contactMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("invoiceMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("alternativeMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me", null));
            add("cancellationTolerance", random(Integer.class, range(0, 60)));
            add("tax", random(Double.class, range(0.00, 1)));
            add("technicalContact", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("branchPhotoUri", "/tmp/path");
            add("contractUri", "/tmp/path");
            add("gatheringChannel", random(GatheringChannel.class));
            add("bankAccount", one(BankAccount.class, "persisted"));
            add("checkout", one(Checkout.class,"valid"));
        }});

    }
}
