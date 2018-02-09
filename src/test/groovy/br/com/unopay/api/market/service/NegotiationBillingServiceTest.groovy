package br.com.unopay.api.market.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.market.model.NegotiationBilling
import org.springframework.beans.factory.annotation.Autowired

class NegotiationBillingServiceTest extends SpockApplicationTests{

    @Autowired
    private NegotiationBillingService service
    @Autowired
    private FixtureCreator fixtureCreator

    def "given valid negotiation billing should be created"(){
        given:
        NegotiationBilling billing = Fixture.from(NegotiationBilling).gimme("valid", new Rule(){{
            add("hirerNegotiation", fixtureCreator.createNegotiation())
        }})

        when:
        NegotiationBilling created = service.save(billing)
        NegotiationBilling found = service.findById(created.id)

        then:
        found
    }
}
