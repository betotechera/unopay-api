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
import br.com.unopay.api.bacen.model.EstablishmentEvent;
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
import br.com.unopay.api.model.IssueInvoiceType;
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
            add("fee", random(Double.class));
            add("paymentAccount", one(PaymentBankAccount.class, "valid"));
            add("movementAccount", one(BankAccount.class, "persisted"));
            add("financierMailForRemittance", "financeiro@gmail.com");
            add("bin", regex("\\d{4}"));

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
            add("bankAgreementNumberForCredit", regex("\\d{20}"));
            add("bankAgreementNumberForDebit", regex("\\d{20}"));
        }});

        Fixture.of(Service.class).addTemplate("valid", new Rule(){{
            add("name", firstName());
            add("code", random(Integer.class));
            add("type", random(ServiceType.class));
            add("feeVal", random(BigDecimal.class));
            add("feePercent", random(Double.class));
        }});

        Fixture.of(Service.class).addTemplate("persisted").inherits("valid", new Rule(){{
            add("id", "1");
        }});

        Fixture.of(Event.class).addTemplate("valid", new Rule(){{
            add("service", one(Service.class, "persisted"));
            add("ncmCode", regex("\\d{15,18}"));
            add("name", regex("\\w{15,18}"));
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

        Fixture.of(Establishment.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("type", random(EstablishmentType.class));
            add("contactMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("invoiceMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("bachShipmentMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("alternativeMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("cancellationTolerance", random(Integer.class, range(0, 60)));
            add("fee", random(Double.class, range(0.00, 1)));
            add("network", one(AccreditedNetwork.class, "valid"));
            add("facadePhotoUri", "/tmp/path");
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
            add("issueInvoiceType", uniqueRandom(IssueInvoiceType.class));
        }});

        Fixture.of(EstablishmentEvent.class).addTemplate("withoutReferences", new Rule(){{
            add("value", random(BigDecimal.class, range(1,200)));
            add("expiration", instant("1 day from now"));
        }});

        Fixture.of(Branch.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("headOffice", one(Establishment.class, "valid"));
            add("contactMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("invoiceMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("alternativeMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me", null));
            add("cancellationTolerance", random(Integer.class, range(0, 60)));
            add("fee", random(Double.class, range(0.00, 1)));
            add("technicalContact", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("branchPhotoUri", "/tmp/path");
            add("contractUri", "/tmp/path");
            add("gatheringChannel", random(GatheringChannel.class));
            add("bankAccount", one(BankAccount.class, "persisted"));
            add("checkout", one(Checkout.class,"valid"));
        }});

    }
}
