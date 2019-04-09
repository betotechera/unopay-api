package br.com.unopay.api.network;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.AccreditedNetworkIssuer;
import br.com.unopay.api.bacen.model.Bank;
import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.bacen.model.BankAccountType;
import br.com.unopay.api.bacen.model.Checkout;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.GatheringChannel;
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
import br.com.unopay.api.model.Address;
import br.com.unopay.api.model.BrandFlag;
import br.com.unopay.api.model.Contact;
import br.com.unopay.api.model.IssueInvoiceType;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.network.model.Branch;
import br.com.unopay.api.network.model.BranchServicePeriod;
import br.com.unopay.api.network.model.BranchSituation;
import br.com.unopay.api.network.model.Establishment;
import br.com.unopay.api.network.model.EstablishmentEvent;
import br.com.unopay.api.network.model.EstablishmentType;
import br.com.unopay.api.network.model.Event;
import br.com.unopay.api.network.model.Partner;
import br.com.unopay.api.network.model.Service;
import br.com.unopay.api.network.model.ServicePeriodSituation;
import br.com.unopay.api.network.model.ServiceType;
import br.com.unopay.api.network.model.Weekday;
import br.com.unopay.api.uaa.model.UserDetail;
import java.math.BigDecimal;


public class NetworkTemplateLoader implements TemplateLoader {
    @Override
    public void load() {

        Fixture.of(Partner.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("bankAccount", one(BankAccount.class, "persisted"));
        }});

        Fixture.of(AccreditedNetwork.class).addTemplate("valid", new Rule(){{
            add("person", one(Person.class, "legal"));
            add("paymentRuleGroups", has(1).of(PaymentRuleGroup.class, "persisted"));
            add("merchantDiscountRate", random(Double.class,range(0D,1.0D)));
            add("bankAccount", one(BankAccount.class, "persisted"));
            add("checkout", one(Checkout.class,"valid"));
            add("invoiceReceipt", one(InvoiceReceipt.class,"valid"));
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
            add("fantasyName", name());
            add("name", firstName());
            add("shortName", firstName());
            add("address", one(Address.class, "address"));
            add("situation", random(BranchSituation.class));
            add("headOffice", one(Establishment.class, "valid"));
            add("contactMail", random("teste@teste.com", "joao@gmail.com.br", "david@terra.com.br", "ze@org.me"));
            add("branchPhotoUri", "/tmp/path");
            add("gatheringChannels", has(2).of(GatheringChannel.class));
            add("services", has(2).of(Service.class, "valid"));
        }});

        Fixture.of(AccreditedNetworkIssuer.class).addTemplate("valid", new Rule(){{
            add("accreditedNetwork", one(AccreditedNetwork.class, "valid"));
            add("issuer", one(Issuer.class, "valid"));
            add("user", one(UserDetail.class, "with-group"));
            add("createdDateTime", instant("now"));
            add("active", random(Boolean.class));
        }});

        Fixture.of(BranchServicePeriod.class).addTemplate("valid", new Rule(){{
            add("beginServiceTime", instant("1 day from now"));
            add("endServiceTime", instant("2 day from now"));
            add("situation", random(ServicePeriodSituation.class));
            add("weekday", random(Weekday.class));
            add("createdDateTime", instant("now"));
        }});
    }
}
