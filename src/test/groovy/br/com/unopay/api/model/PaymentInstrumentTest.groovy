package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.joda.time.DateTime

class PaymentInstrumentTest  extends FixtureApplicationTest {

    def 'when create with contractor and product should define product'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")
        when:
        def instrument = new PaymentInstrument(contractor, product)

        then:
        instrument.product == product
    }

    def 'when create with contractor and product should define contractor'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")
        when:
        def instrument = new PaymentInstrument(contractor, product)

        then:
        instrument.contractor == contractor
    }

    def 'when create with contractor and product should define type with digital wallet'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")
        when:
        def instrument = new PaymentInstrument(contractor, product)

        then:
        instrument.type == PaymentInstrumentType.DIGITAL_WALLET
    }

    def 'when create with contractor and product should define active situation'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")
        when:
        def instrument = new PaymentInstrument(contractor, product)

        then:
        instrument.situation == PaymentInstrumentSituation.ACTIVE
    }

    def 'when create with contractor and product should expiration date'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")
        when:
        def instrument = new PaymentInstrument(contractor, product)

        then:
        instrument.expirationDate == new DateTime().plusYears(5).withMillisOfDay(0).toDate()
    }

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
