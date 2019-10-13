package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.RecurrencePeriod;
import br.com.unopay.api.billing.remittance.model.PaymentRemittanceItem;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.BatchClosingItem;
import br.com.unopay.api.model.BatchClosingSituation;
import br.com.unopay.api.model.DocumentSituation;
import br.com.unopay.api.model.IssueInvoiceType;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.network.model.Establishment;
import java.math.BigDecimal;

public class BatchClosingTemplateLoader  implements TemplateLoader {

    @Override
    public void load() {

        Fixture.of(BatchClosing.class).addTemplate("valid", new Rule(){{
            add("closingDateTime", instant("1 day from now"));
            add("value",random(BigDecimal.class, range(1,300)));
            add("period",random(RecurrencePeriod.class));
            add("paymentReleaseDateTime", instant("5 days from now"));
            add("situation",random(BatchClosingSituation.class));
            add("paymentDateTime", instant("10 days from now"));
            add("issueInvoice", random(Boolean.class));
            add("accreditedNetwork", one(AccreditedNetwork.class, "valid"));
            add("establishment", one(Establishment.class, "valid"));
            add("hirer", one(Hirer.class, "valid"));
            add("issuer", one(Issuer.class, "valid"));

        }});

        Fixture.of(BatchClosing.class).addTemplate("withPayment").inherits("valid", new Rule(){{
            add("payment",one(PaymentRemittanceItem.class, "valid"));
        }});

        Fixture.of(BatchClosingItem.class).addTemplate("valid", new Rule() {{
            add("documentNumberInvoice",regex("\\w{15}"));
            add("invoiceNumber",regex("\\w{15}"));
            add("invoiceDocumentSituation", DocumentSituation.PENDING);
            add("issueInvoiceType", IssueInvoiceType.BY_BATCH);
            add("invoiceDocumentUri",random("file://tmp/tmp.jpg", "https://s3.com/nf.pdf"));
            add("serviceAuthorize", one(ServiceAuthorize.class, "valid"));
            add("batchClosing", one(BatchClosing.class, "valid"));
        }});

    }
}