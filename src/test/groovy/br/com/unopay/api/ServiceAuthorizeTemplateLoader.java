package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.EstablishmentEvent;
import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.credit.model.CreditInsertionType;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.ServiceAuthorizeEvent;
import br.com.unopay.api.model.TransactionSituation;
import br.com.unopay.api.uaa.model.UserDetail;
import java.math.BigDecimal;

public class ServiceAuthorizeTemplateLoader implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(ServiceAuthorize.class).addTemplate("valid", new Rule(){{
            add("authorizationDateTime",instant("now"));
            add("establishment", one(Establishment.class, "valid"));
            add("contract",one(Contract.class, "valid"));
            add("contractor",one(Contractor.class, "valid"));
            add("value",random(BigDecimal.class, range(1, 20)));
            add("paymentInstrument",one(PaymentInstrument.class, "valid"));
            add("lastInstrumentCreditBalance",random(BigDecimal.class, range(21, 200)));
            add("currentInstrumentCreditBalance",random(BigDecimal.class, range(21, 200)));
            add("cancellationDateTime",instant("one day from now"));
            add("authorizationNumber", regex("\\w{15}"));
            add("situation", random(TransactionSituation.class));
            add("user",one(UserDetail.class, "without-group"));
        }});

        Fixture.of(ServiceAuthorize.class).addTemplate("withoutReferences").inherits("valid", new Rule(){{
            add("establishment",null);
            add("contract",null);
            add("contractor", null);
            add("event",null);
            add("user", null);
        }});

        Fixture.of(ServiceAuthorizeEvent.class).addTemplate("valid", new Rule(){{
            add("establishmentEvent",one(EstablishmentEvent.class, "withoutReferences"));
            add("serviceType",uniqueRandom(ServiceType.class));
            add("event",one(Event.class, "valid"));
            add("eventValue",random(BigDecimal.class, range(1, 20)));
            add("valueFee", random(BigDecimal.class, range(1, 3)));
            add("createdDateTime", instant("now"));
        }});
    }
}