package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.network.model.EstablishmentEvent
import br.com.unopay.api.network.model.Event
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class ServiceAuthorizeEventTest extends FixtureApplicationTest{

    void 'when validate event value less than or equals zero should return error'(){
        given:
        def value = valueUnderTest
        EstablishmentEvent establishmentEvent = Fixture.from(EstablishmentEvent.class)
                .gimme("withoutReferences", new Rule(){{
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

    void 'should define authorize event from establishment event'(){
        given:
        EstablishmentEvent establishmentEvent = Fixture.from(EstablishmentEvent.class)
                .gimme("withoutReferences", new Rule(){{
            add("event", one(Event, "valid"))
        }})

        when:
        def authorizeEvent = new ServiceAuthorizeEvent()
        authorizeEvent.defineValidEventValues(establishmentEvent)

        then:
        authorizeEvent.valueFee == establishmentEvent.event.serviceFeeVal()
        authorizeEvent.event.id == establishmentEvent.event.id
        authorizeEvent.establishmentEvent.id == establishmentEvent.id
        authorizeEvent.eventValue == establishmentEvent.value
        authorizeEvent.serviceType == establishmentEvent.serviceType()

    }

    def 'should be equals'(){
        given:
        ServiceAuthorizeEvent a = Fixture.from(ServiceAuthorizeEvent.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(ServiceAuthorizeEvent.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}
