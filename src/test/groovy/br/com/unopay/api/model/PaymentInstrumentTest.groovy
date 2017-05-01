package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

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

    void 'given paymentInstrument with creation date after expiration date it should throw error'(){
        given:
        PaymentInstrument a = Fixture.from(PaymentInstrument.class).gimme("valid")
        a = a.with {
            createdDate = expirationDate + 1
            it }

        when:
        a.validate()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EXPIRATION_IS_BEFORE_CREATION'
    }

}
