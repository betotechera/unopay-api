package br.com.unopay.api;

import br.com.six2six.fixturefactory.*;
import br.com.six2six.fixturefactory.loader.*;
import br.com.unopay.api.bacen.model.*;
import br.com.unopay.api.model.*;
import br.com.unopay.api.uaa.model.*;
import java.math.*;

public class ServiceAuthorizeTemplateLoader implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(ServiceAuthorize.class).addTemplate("valid", new Rule(){{
            add("authorizationNumber",random(Long.class, range(900, 9000000000.00)));
            add("authorizationDateTime",instant("now"));
            add("establishment", one(Establishment.class, "valid"));
            add("contract",one(Contract.class, "valid"));
            add("contractor",one(Contractor.class, "valid"));
            add("serviceType",uniqueRandom(ServiceType.class));
            add("event",one(Event.class, "valid"));
            add("eventQuantity",random(Double.class, range(1, 200)));
            add("eventValue",random(BigDecimal.class, range(1, 200)));
            add("valueFee",random(BigDecimal.class, range(1, 5500)));
            add("solicitationDateTime",instant("now"));
            add("creditInsertionType",random(CreditInsertionType.class));
            add("contractorInstrumentCredit",one(ContractorInstrumentCredit.class, "allFields"));
            add("lastInstrumentCreditBalance",random(BigDecimal.class, range(1, 200)));
            add("currentInstrumentCreditBalance",random(BigDecimal.class, range(1, 200)));
            add("cancellationDateTime",instant("one day from now"));
            add("transactionLogCode",random(Integer.class, range(1, 200)));
            add("transactionLog",firstName());
            add("user",one(UserDetail.class, "without-group"));
            add("situation",random(TransactionSituation.class));
        }});

        Fixture.of(ServiceAuthorize.class).addTemplate("withoutReferences").inherits("valid", new Rule(){{
            add("establishment",null);
            add("contract",null);
            add("contractor", null);
            add("event",null);
            add("user", null);
        }});
    }
}