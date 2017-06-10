package br.com.unopay.api.bacen;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.AccreditedNetworkType;
import br.com.unopay.api.bacen.model.Bank;
import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.bacen.model.BankAccountType;
import br.com.unopay.api.bacen.model.Branch;
import br.com.unopay.api.bacen.model.Checkout;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.EstablishmentType;
import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.model.GatheringChannel;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.HirerBranch;
import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.InvoiceReceipt;
import br.com.unopay.api.bacen.model.InvoiceReceiptType;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.Partner;
import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.Purpose;
import br.com.unopay.api.bacen.model.RecurrencePeriod;
import br.com.unopay.api.bacen.model.Scope;
import br.com.unopay.api.bacen.model.Service;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.bacen.model.UserRelationship;
import br.com.unopay.api.model.BrandFlag;
import br.com.unopay.api.model.Contact;
import br.com.unopay.api.model.Person;
import java.math.BigDecimal;


public class BacenTemplateLoader implements TemplateLoader {
    @Override
    public void load() {

        Fixture.of(PaymentRuleGroup.class).addTemplate("valid", new Rule(){{
            add("code", uniqueRandom("48e57f50","54a2","4870","b9a9","9e9fde29cc9e", "2323d2c1",
                    "7093","45cf","92de","bf27eb84ffeb", "42b0f8d8","d78a","4930","9b8c","68d67b767d1a", "f9d78e11",
                    "2818","4e1e","a3d5","c782fe30c6d1", "9e2d5d0c","7a2c","47e5","97af","0d110b67a0bc", "6a15b32c",
                    "b987","4d94","a89d","61f5a7ee9d54", "0f6cb79c","8041","4f27","bd3b","466200472c53", "0dbd37b9",
                    "1194","43c6","a189","e6dd0aaf4fa4", "15d640fd","3e82","4202","82c8","7f1b376238a7", "d218f535",
                    "6bb3","4531","a019","41fd38e85035", "e0e0c385","d4d9","4a16","aeeb","460a2d48f5cb", "8b42853b",
                    "3306","4ac6","a625","4c6c4d076e4a", "6aab6237","bb60","4f82","8ea7","ef60f0f98d7d", "e2f3a995",
                    "3414","4245","9fac","90456f114620", "1e47e9d2","d79f","4155","8ecc","fe2fdc1ce035", "171f4ba7",
                    "58e6","4918","9bbb","ae7899f8211a", "18fbf9b3","528e","4085","b317","f911cba5ef0c", "8892f90b",
                    "24dc","45d8","800a","8c6d64b14767", "fa3f6eed","accf","4f0a","9256","b2dd3ac7aacc", "6fab43e6",
                    "f201","4692","8d7c","5f6e4359a9ee"));
            add("name", "Arranjo");
            add("institution", one(Institution.class, "persisted"));
            add("purpose", Purpose.BUY);
            add("scope", Scope.DOMESTIC);
            add("userRelationship", UserRelationship.POSTPAID);
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
        }});

        Fixture.of(Partner.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("bankAccount", one(BankAccount.class, "persisted"));
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
            add("rntrc", random("65647988664", "564654698469479688", "SS454SAF564AS8FA6S4DF"));
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

        Fixture.of(BankAccount.class).addTemplate("valid", new Rule(){{
            add("bank", one(Bank.class, "valid"));
            add("agency", random("6465", "55794", "004456"));
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
        }});

        Fixture.of(Service.class).addTemplate("valid", new Rule(){{
            add("name", firstName());
            add("code", random(Integer.class));
            add("type", random(ServiceType.class));
            add("taxVal", random(BigDecimal.class));
            add("taxPercent", random(Double.class));
        }});

        Fixture.of(Service.class).addTemplate("persisted").inherits("valid", new Rule(){{
            add("id", "1");
        }});

        Fixture.of(Event.class).addTemplate("valid", new Rule(){{
            add("service", one(Service.class, "persisted"));
            add("ncmCode", random("445661", "879879"));
            add("name", firstName());
            add("requestQuantity",random(Boolean.class));
            add("quantityUnity",random("litros", "arroba"));
        }});

        Fixture.of(Event.class).addTemplate("withRequestQuantity").inherits("valid", new Rule(){{
            add("requestQuantity",true);
        }});

        Fixture.of(Event.class).addTemplate("withoutRequestQuantity").inherits("valid", new Rule(){{
            add("requestQuantity",false);
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

        Fixture.of(Contact.class).addTemplate("persisted").inherits("valid", new Rule(){{
            add("id", "1");
        }});

        Fixture.of(Establishment.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("type", random(EstablishmentType.class));
            add("contactMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("invoiceMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("bachShipmentMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("alternativeMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
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
            add("gatheringChannels", has(2).of(GatheringChannel.class));
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
