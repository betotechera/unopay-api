package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.model.CargoContract;
import br.com.unopay.api.model.CargoProfile;
import br.com.unopay.api.model.ComplementaryTravelDocument;
import br.com.unopay.api.model.ComplementaryTravelDocumentType;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.DocumentCaveat;
import br.com.unopay.api.model.TravelDocumentSituation;
import br.com.unopay.api.model.PaymentSource;
import br.com.unopay.api.model.ReasonReceiptSituation;
import br.com.unopay.api.model.ReceiptSituation;
import br.com.unopay.api.model.ReceiptStep;
import br.com.unopay.api.model.TravelDocument;
import br.com.unopay.api.model.TravelDocumentType;
import br.com.unopay.api.model.TravelSituation;
import java.util.UUID;

public class FreigthReceiptTemplateLoader implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(TravelDocument.class).addTemplate("valid", new Rule(){{
            add("contract",one(Contract.class, "valid"));
            add("quantity", random(Integer.class));
            add("type",random(TravelDocumentType.class));
            add("documentNumber", random("56465456546", "564646898", "SDDDDF54454554"));
            add("situation", random(TravelDocumentSituation.class));
            add("caveat", random(DocumentCaveat.class));
            add("createdDateTime", instant("this second"));
            add("deliveryDateTime",instant("5 seconds ago"));
            add("cargoWeight", random(Double.class, range(1, 200)));
            add("damagedItems", random(Integer.class,range(1, 200)));
        }});

        Fixture.of(TravelDocument.class).addTemplate("toPersist").inherits("valid", new Rule(){{
            add("contract", null);
        }});

        Fixture.of(ComplementaryTravelDocument.class).addTemplate("valid", new Rule(){{
            add("quantity", random(Integer.class));
            add("type",random(ComplementaryTravelDocumentType.class));
            add("documentNumber", random("56465456546", "564646898", "SDDDDF54454554"));
            add("situation", random(TravelDocumentSituation.class));
            add("caveat", random(DocumentCaveat.class));
            add("createdDateTime", instant("this second"));
            add("deliveryDateTime",instant("5 seconds ago"));
        }});

        Fixture.of(CargoContract.class).addTemplate("valid", new Rule(){{
            add("contract",one(Contract.class, "valid"));
            add("partnerId", UUID.randomUUID().toString());
            add("caveat", random(DocumentCaveat.class));
            add("cargoProfile",random(CargoProfile.class));
            add("receiptObservation", firstName());

            add("receiptStep",random(ReceiptStep.class));
            add("paymentSource", random(PaymentSource.class));
            add("travelSituation",random(TravelSituation.class));
            add("createdDateTime", instant("this second"));
            add("receiptSituation",random(ReceiptSituation.class));
            add("reasonReceiptSituation",random(ReasonReceiptSituation.class));
        }});

        Fixture.of(CargoContract.class).addTemplate("toPersist").inherits("valid", new Rule(){{
            add("contract", null);
        }});
    }
}