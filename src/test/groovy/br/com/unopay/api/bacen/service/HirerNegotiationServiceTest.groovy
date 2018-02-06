package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.HirerNegotiation
import br.com.unopay.api.bacen.util.FixtureCreator
import org.springframework.beans.factory.annotation.Autowired

class HirerNegotiationServiceTest extends SpockApplicationTests{

    @Autowired
    private HirerNegotiationService service
    @Autowired
    private FixtureCreator fixtureCreator

    def 'valid hirer negotiation should be created'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
        }})

        when:
        HirerNegotiation created = service.save(negotiation)
        HirerNegotiation found = service.findById(created.id)

        then:
        found
    }
}
