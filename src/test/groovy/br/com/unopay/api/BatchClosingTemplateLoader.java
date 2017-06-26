package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.RecurrencePeriod;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.BatchClosingItem;
import br.com.unopay.api.model.BatchClosingSituation;
import br.com.unopay.api.model.DocumentSituation;
import br.com.unopay.api.model.IssueInvoiceType;
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
            add("paymentId","ASDSD5546D7F88D");
            add("accreditedNetwork", one(AccreditedNetwork.class, "valid"));
            add("establishment", one(Establishment.class, "valid"));
            add("hirer", one(Hirer.class, "valid"));
            add("issuer", one(Issuer.class, "valid"));
        }});

        Fixture.of(BatchClosingItem.class).addTemplate("valid", new Rule() {{
            add("documentNumberInvoice",random("07f2606d-730c-4586-8a2a-88ce821895fd",
                    "29bfe491-c75b-4643-83ec-c8f56cdd5e38", "07c90794-061b-4cff-99ba-cc112da52cf6",
                    "624e9e23-031a-4a79-a3cd-a6d1ed045371", "c9e7f041-ebc0-4adf-9416-9a9c6cc7c6ed"));
            add("invoiceNumber",random("07f2606d-730c-4586-8a2a-88ce821895fd",
                    "29bfe491-c75b-4643-83ec-c8f56cdd5e38", "07c90794-061b-4cff-99ba-cc112da52cf6",
                    "624e9e23-031a-4a79-a3cd-a6d1ed045371", "c9e7f041-ebc0-4adf-9416-9a9c6cc7c6ed"));
            add("invoiceDocumentSituation",random(DocumentSituation.class));
            add("invoiceDocumentUri",random("file://tmp/tmp.jpg", "https://s3.com/nf.pdf"));
            add("issueInvoiceType",random(IssueInvoiceType.class));
        }});

    }
}