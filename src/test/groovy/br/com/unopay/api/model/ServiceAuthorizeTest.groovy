package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.bacen.model.EstablishmentEvent
import br.com.unopay.api.bacen.model.Event
import br.com.unopay.api.credit.model.InstrumentBalance
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import spock.lang.Unroll

class ServiceAuthorizeTest  extends FixtureApplicationTest {


    void 'given a event value greater than credit balance when validate event should return error'(){
        given:
        InstrumentBalance balance = Fixture.from(InstrumentBalance.class).gimme("valid", new Rule(){{
            add("value", 99.9)
        }})
        PaymentInstrument paymentInstrument = Fixture.from(PaymentInstrument.class)
                                                                        .gimme("valid", new Rule() {{
            add("balance", balance)
        }})

        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule() {{
            add("paymentInstrument", paymentInstrument)
        }})
        when:
        serviceAuthorize.validateEvent(100.0)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE'
    }

    void 'given a event value less than credit balance when validate event should not return error'(){
        given:
        InstrumentBalance balance = Fixture.from(InstrumentBalance.class).gimme("valid", new Rule(){{
            add("value", 100.0)
        }})
        PaymentInstrument paymentInstrument = Fixture.from(PaymentInstrument.class)
                .gimme("valid", new Rule() {{
            add("balance", balance)
        }})
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule() {{
            add("paymentInstrument", paymentInstrument)
        }})
        when:
        serviceAuthorize.validateEvent(99.9)

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
