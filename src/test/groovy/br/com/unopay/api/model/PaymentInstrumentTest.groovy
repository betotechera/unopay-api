package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class PaymentInstrumentTest  extends FixtureApplicationTest {

    def 'should update me'(){
        given:
        PaymentInstrument a = Fixture.from(PaymentInstrument.class).gimme("valid")
        PaymentInstrument b = Fixture.from(PaymentInstrument.class).gimme("valid")
        b.getContractor().setId('65545')
        b.getProduct().setId('65545')

        when:
        a.updateMe(b)

        then:
        a.type == b.type
        a.number == b.number
        a.product == b.product
        a.contractor == b.contractor
        a.createdDate == b.createdDate
        a.expirationDate == b.expirationDate
        a.password == b.password
        a.situation == b.situation
        a.externalNumberId == b.externalNumberId
    }

    def 'should be equals'(){
        given:
        PaymentInstrument a = Fixture.from(PaymentInstrument.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(PaymentInstrument.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}
