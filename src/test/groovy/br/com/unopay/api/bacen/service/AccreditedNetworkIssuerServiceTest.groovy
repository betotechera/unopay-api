package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.AccreditedNetworkIssuer
import br.com.unopay.api.bacen.util.FixtureCreator
import org.springframework.beans.factory.annotation.Autowired

class AccreditedNetworkIssuerServiceTest extends SpockApplicationTests {

    @Autowired
    private AccreditedNetworkIssuerService service
    @Autowired
    private FixtureCreator fixtureCreator

    def 'should create AccreditedNetworkIssuerService'(){
        given:
        AccreditedNetworkIssuer networkIssuer = Fixture.from(AccreditedNetworkIssuer).gimme("valid", new Rule(){{
            add("issuer", fixtureCreator.createIssuer())
            add("accreditedNetwork", fixtureCreator.createNetwork())
            add("user", fixtureCreator.createUser())
        }})

        when:
        AccreditedNetworkIssuer created = service.create(networkIssuer)
        AccreditedNetworkIssuer result = service.findById(created.id)

        then:
        result
    }
}
