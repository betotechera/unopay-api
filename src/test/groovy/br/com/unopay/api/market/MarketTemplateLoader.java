package br.com.unopay.api.market;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.AccreditedNetworkIssuer;
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
import br.com.unopay.api.market.model.AuthorizedMember;
import br.com.unopay.api.market.model.AuthorizedMemberCandidate;
import br.com.unopay.api.market.model.BonusBilling;
import br.com.unopay.api.market.model.HirerNegotiation;
import br.com.unopay.api.market.model.NegotiationBilling;
import br.com.unopay.api.market.model.NegotiationBillingDetail;
import br.com.unopay.api.model.BrandFlag;
import br.com.unopay.api.model.Contact;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.Gender;
import br.com.unopay.api.model.IssueInvoiceType;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.Relatedness;
import br.com.unopay.api.order.model.PaymentStatus;
import br.com.unopay.api.uaa.model.UserDetail;
import java.math.BigDecimal;


public class MarketTemplateLoader implements TemplateLoader {
    @Override
    public void load() {

        Fixture.of(HirerNegotiation.class).addTemplate("valid", new Rule(){{
            add("product", one(Product.class, "valid"));
            add("hirer", one(Hirer.class, "valid"));
            add("defaultCreditValue", random(BigDecimal.class, range(2, 300)));
            add("defaultMemberCreditValue", random(BigDecimal.class, range(2, 300)));
            add("paymentDay", random(Integer.class, range(1, 28)));
            add("installments", random(Integer.class, range(6, 24)));
            add("billingWithCredits", random(Boolean.class));
            add("installmentValue", random(BigDecimal.class, range(2, 300)));
            add("installmentValueByMember", random(BigDecimal.class, range(2, 300)));
            add("autoRenewal", random(Boolean.class));
            add("effectiveDate", instant("one day from now"));
            add("freeInstallmentQuantity", 0);
            add("createdDateTime", instant("now"));
            add("active", random(Boolean.class));
        }});

        Fixture.of(AuthorizedMember.class).addTemplate("valid", new Rule(){{
            add("birthDate", instant("18 years ago"));
            add("contract", one(Contract.class, "valid"));
            add("name",  regex("\\w{15}"));
            add("gender",  random(Gender.class));
            add("relatedness",  random(Relatedness.class));
            add("createdDateTime", instant("now"));
            add("paymentInstrument",one(PaymentInstrument.class, "valid"));
        }});

        Fixture.of(AuthorizedMemberCandidate.class).addTemplate("valid", new Rule(){{
            add("birthDate", instant("18 years ago"));
            add("name",  regex("\\w{15}"));
            add("gender",  random(Gender.class));
            add("createdDateTime", instant("now"));
            add("relatedness",  random(Relatedness.class));
        }});

        Fixture.of(HirerNegotiation.class).addTemplate("withFreeInstallments").inherits("valid", new Rule(){{
            add("freeInstallmentQuantity", random(Integer.class, range(1, 31)));
        }});

        Fixture.of(NegotiationBilling.class).addTemplate("valid", new Rule(){{
            add("hirerNegotiation", one(HirerNegotiation.class, "valid"));
            add("installmentNumber", random(Integer.class, range(1, 5)));
            add("installmentExpiration", instant("one day from now"));
            add("installments", random(Integer.class, range(6, 24)));
            add("billingWithCredits", Boolean.TRUE);
            add("installmentValue", random(BigDecimal.class, range(2, 300)));
            add("installmentValueByMember", random(BigDecimal.class, range(2, 300)));
            add("freeInstallmentQuantity", 0);
            add("defaultCreditValue", random(BigDecimal.class, range(2, 300)));
            add("defaultMemberCreditValue", random(BigDecimal.class, range(2, 300)));
            add("createdDateTime", instant("now"));
            add("value",  random(BigDecimal.class, range(2, 300)));
            add("creditValue",  random(BigDecimal.class, range(2, 300)));
            add("status", random(PaymentStatus.class));
        }});

        Fixture.of(NegotiationBillingDetail.class).addTemplate("valid", new Rule(){{
            add("negotiationBilling", one(NegotiationBilling.class, "valid"));
            add("contract", one(Contract.class, "valid"));
            add("installmentValue", random(BigDecimal.class, range(2, 300)));
            add("installmentValueByMember", random(BigDecimal.class, range(2, 300)));
            add("freeInstallment", random(Boolean.class));
            add("creditValue", random(BigDecimal.class, range(2, 300)));
            add("memberCreditValue", random(BigDecimal.class, range(2, 300)));
            add("createdDateTime", instant("now"));
            add("value",  random(BigDecimal.class, range(2, 300)));
            add("memberTotal",  random(Integer.class, range(1, 100)));
        }});

        Fixture.of(BonusBilling.class).addTemplate("valid", new Rule(){{
            add("createdDateTime", instant("now"));
            add("person", one(Person.class, "physical"));
            add("total", random(Double.class, range(2, 300)));
            add("processedAt", instant("now"));
            add("number",  regex("\\w{100}"));
            add("expiration", instant("tomorrow"));
            add("status", random(PaymentStatus.class));
        }});
    }
}
