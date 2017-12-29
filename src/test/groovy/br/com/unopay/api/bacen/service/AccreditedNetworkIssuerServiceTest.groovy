package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.AccreditedNetworkIssuer
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class AccreditedNetworkIssuerServiceTest extends SpockApplicationTests {

    @Autowired
    private AccreditedNetworkIssuerService service
    @Autowired
    private FixtureCreator fixtureCreator
    private String userEmailUnderTest
    void setup(){
        userEmailUnderTest = fixtureCreator.createUser().email
    }

    def 'should create AccreditedNetworkIssuerService'(){
        given:
        AccreditedNetworkIssuer networkIssuer = Fixture.from(AccreditedNetworkIssuer).gimme("valid", new Rule(){{
            add("issuer", fixtureCreator.createIssuer())
            add("accreditedNetwork", fixtureCreator.createNetwork())
            add("user", fixtureCreator.createUser())
        }})

        when:
        AccreditedNetworkIssuer created = service.create(userEmailUnderTest, networkIssuer)
        AccreditedNetworkIssuer result = service.findById(created.id)

        then:
        result
    }

    def 'given a unknown issuer when create should return error'(){
        given:
        AccreditedNetworkIssuer networkIssuer = Fixture.from(AccreditedNetworkIssuer).gimme("valid", new Rule(){{
            add("accreditedNetwork", fixtureCreator.createNetwork())
            add("user", fixtureCreator.createUser())
        }})

        when:
        service.create(userEmailUnderTest, networkIssuer)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'ISSUER_NOT_FOUND'
    }

    def 'given a unknown network when create should return error'(){
        given:
        AccreditedNetworkIssuer networkIssuer = Fixture.from(AccreditedNetworkIssuer).gimme("valid", new Rule(){{
            add("issuer", fixtureCreator.createIssuer())
            add("user", fixtureCreator.createUser())
        }})

        when:
        service.create(userEmailUnderTest, networkIssuer)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'ACCREDITED_NETWORK_NOT_FOUND'
    }

    def 'given a unknown user when create should return error'(){
        given:
        AccreditedNetworkIssuer networkIssuer = Fixture.from(AccreditedNetworkIssuer).gimme("valid", new Rule(){{
            add("issuer", fixtureCreator.createIssuer())
            add("accreditedNetwork", fixtureCreator.createNetwork())
        }})

        when:
        service.create('', networkIssuer)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'USER_NOT_FOUND'
    }
}
