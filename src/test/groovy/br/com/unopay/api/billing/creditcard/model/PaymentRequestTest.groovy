package br.com.unopay.api.billing.creditcard.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class PaymentRequestTest extends FixtureApplicationTest {

    def 'should transform to transaction'(){
        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest.class).gimme("valid")

        when:
        Transaction transaction = paymentRequest.toTransaction()

        then:
        paymentRequest.creditCard.expiryMonth == transaction.creditCard.expiryMonth
        paymentRequest.creditCard.expiryYear == transaction.creditCard.expiryYear
        paymentRequest.creditCard.securityCode == transaction.creditCard.securityCode
        paymentRequest.creditCard.number == transaction.creditCard.number
        paymentRequest.orderId == transaction.orderId
        paymentRequest.installments == transaction.installments
        paymentRequest.method == transaction.paymentMethod
        paymentRequest.value == transaction.amount.value

    }

    def 'should be equals'() {
        given:
        PaymentRequest a = Fixture.from(PaymentRequest.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'() {
        List list = Fixture.from(PaymentRequest.class).gimme(2, "valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals

    }
}
