package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.bacen.model.Event
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import groovy.time.TimeCategory
import spock.lang.Unroll

class ServiceAuthorizeTest   extends FixtureApplicationTest {


    @Unroll
    void 'given a event with request quantity when validate event quantity equals #quantityUnderTest should return error'(){
        given:
        def quantity = quantityUnderTest
        Event event = Fixture.from(Event.class).gimme("withRequestQuantity")
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule() {{
            add("eventQuantity", quantity)
        }})
        when:
        serviceAuthorize.validateEvent(event)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EVENT_QUANTITY_GREATER_THAN_ZERO_REQUIRED'

        where:
        _|quantityUnderTest
        _| 0D
        _|-1D

    }

    void 'given a event without request quantity when validate event quantity less than or equals zero should not return error'(){
        given:
        def quantity = quantityUnderTest
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule() {{
            add("event", one(Event.class, "withoutRequestQuantity"))
            add("eventQuantity", quantity)
        }})
        when:
        serviceAuthorize.validateEvent()

        then:
        notThrown(UnprocessableEntityException)

        where:
        _|quantityUnderTest
        _| 0D
        _|-1D
    }

    @Unroll
    void 'given a event with request quantity when validate event value equals #quantityUnderTest should return error'(){
        given:
        def quantity = quantityUnderTest
        def value = valueUnderTest
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule() {{
            add("event", one(Event.class, "withRequestQuantity"))
            add("eventQuantity", quantity)
            add("eventValue", value)
        }})
        when:
        serviceAuthorize.validateEvent()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED'

        where:
        quantityUnderTest | valueUnderTest
        1D                | 0.0
        2D                | -0.1

    }

    void 'given a event without request quantity when validate event value less than or equals zero should return error'(){
        given:
        def quantity = quantityUnderTest
        def value = valueUnderTest
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule() {{
            add("event", one(Event.class, "withoutRequestQuantity"))
            add("eventQuantity", quantity)
            add("eventValue", value)
        }})
        when:
        serviceAuthorize.validateEvent()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED'

        where:
        quantityUnderTest | valueUnderTest
        1D                | 0.0
        2D                | -0.1

    }

    void 'given a event value greater than credit balance when validate event should return error'(){
        given:
        ContractorInstrumentCredit creditUnderTest = Fixture.from(ContractorInstrumentCredit.class)
                                                                        .gimme("allFields", new Rule() {{
            add("availableBalance", 99.9)
        }})
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule() {{
            add("event", one(Event.class, "withoutRequestQuantity"))
            add("eventValue", 100.0)
            add("contractorInstrumentCredit", creditUnderTest)
        }})
        when:
        serviceAuthorize.validateEvent()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE'
    }

    void 'given a event value less than credit balance when validate event should not return error'(){
        given:
        ContractorInstrumentCredit creditUnderTest = Fixture.from(ContractorInstrumentCredit.class)
                .gimme("allFields", new Rule() {{
            add("availableBalance", 100.0)
        }})
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule() {{
            add("event", one(Event.class, "withoutRequestQuantity"))
            add("eventValue", 99.9)
            add("contractorInstrumentCredit", creditUnderTest)
        }})
        when:
        serviceAuthorize.validateEvent()

        then:
        notThrown(UnprocessableEntityException)
    }

    def 'should be equals'(){
        given:
        ServiceAuthorize a = Fixture.from(ServiceAuthorize.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(ServiceAuthorize.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}
