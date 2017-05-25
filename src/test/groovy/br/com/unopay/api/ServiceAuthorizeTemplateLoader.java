package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.CreditInsertionType;
import static br.com.unopay.api.model.CreditInsertionType.BOLETO;
import static br.com.unopay.api.model.CreditInsertionType.CREDIT_CARD;
import static br.com.unopay.api.model.CreditInsertionType.PAMCARD_SYSTEM;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.TransactionSituation;
import br.com.unopay.api.uaa.model.UserDetail;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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