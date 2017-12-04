package br.com.unopay.api.bacen.model.csv

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.bacen.model.EstablishmentEvent
import br.com.unopay.api.bacen.model.Event

class EstablishmentEventFeeCsvTest extends FixtureApplicationTest {

    def 'should create event fee'(){
        given:
        def date = new Date()
        Event event = Fixture.from(Event.class).gimme("valid")
        EstablishmentEventFeeCsv csv = new EstablishmentEventFeeCsv() {{
            setValue(BigDecimal.ONE)
            setExpiration(date)
        }}

        when:
        EstablishmentEvent establishmentEvent = csv.toEstablishmentEventFee(event)

        then:
        establishmentEvent.value == BigDecimal.ONE
        establishmentEvent.expiration == date
        establishmentEvent.event == event
    }

    def 'should be equals'(){
        given:
        EstablishmentEventFeeCsv a = new EstablishmentEventFeeCsv() {{ setValue(BigDecimal.ONE)}}

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        EstablishmentEventFeeCsv a = new EstablishmentEventFeeCsv() {{ setValue(BigDecimal.ONE)}}
        EstablishmentEventFeeCsv b = new EstablishmentEventFeeCsv() {{ setValue(BigDecimal.ZERO)}}

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals
    }
}
