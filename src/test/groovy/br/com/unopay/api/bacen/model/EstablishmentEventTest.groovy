package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class EstablishmentEventTest extends FixtureApplicationTest{

    def 'should be equals'(){
        given:
        EstablishmentEvent a = Fixture.from(EstablishmentEvent.class).gimme("withoutReferences")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        EstablishmentEvent a = Fixture.from(EstablishmentEvent.class).gimme("withoutReferences")
        EstablishmentEvent b = Fixture.from(EstablishmentEvent.class).gimme("withoutReferences")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }

}
