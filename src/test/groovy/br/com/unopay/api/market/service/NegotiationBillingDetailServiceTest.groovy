package br.com.unopay.api.market.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.market.model.NegotiationBilling
import br.com.unopay.api.market.model.NegotiationBillingDetail
import org.springframework.beans.factory.annotation.Autowired

class NegotiationBillingDetailServiceTest extends SpockApplicationTests{

    @Autowired
    private NegotiationBillingDetailService service
    @Autowired
    private FixtureCreator fixtureCreator

    def "given valid negotiation billing detail should be created"(){
        given:
        NegotiationBilling negotiationBilling = Fixture.from(NegotiationBilling).uses(jpaProcessor).gimme("valid")
        NegotiationBillingDetail billingDetail = Fixture.from(NegotiationBillingDetail).gimme("valid", new Rule(){{
            add("contract", fixtureCreator.createPersistedContract())
            add("negotiationBilling", negotiationBilling)
        }})

        when:
        NegotiationBillingDetail created = service.save(billingDetail)
        NegotiationBillingDetail found = service.findById(created.id)

        then:
        found
    }
}
