package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.bacen.model.EstablishmentEvent
import br.com.unopay.api.bacen.model.Event
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class ServiceAuthorizeEventTest extends FixtureApplicationTest{

    void 'given a event without request quantity when validate event value less than or equals zero should return error'(){
        given:
        def value = valueUnderTest
        EstablishmentEvent establishmentEvent = Fixture.from(EstablishmentEvent.class).gimme("withoutReferences", new Rule(){{
            add("value", value)
            add("event", one(Event, "valid"))
        }})

        when:
        new ServiceAuthorizeEvent().defineValidEventValues(establishmentEvent)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED'

        where:
        _ | valueUnderTest
        _ | 0.0
        _ | -0.1

    }
}
