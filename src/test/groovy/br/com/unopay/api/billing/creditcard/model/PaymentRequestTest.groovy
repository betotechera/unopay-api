package br.com.unopay.api.billing.creditcard.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
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

    def 'given a payment request with payment method hasPaymentMethod should return true'(){

        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("valid", new Rule(){{
            add("method", PaymentMethod.CARD)
        }})

        when:
        boolean result = paymentRequest.hasPaymentMethod()

        then:
        result

    }

    def 'given a payment request without payment method hasPaymentMethod should return false'(){

        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("valid", new Rule(){{
            add("method", null)
        }})

        when:
        boolean result = paymentRequest.hasPaymentMethod()

        then:
        !result

    }

    def 'given a payment request with store card hasStoreCard should return true'(){

        given:
        boolean value = valid
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("valid", new Rule(){{
            add("storeCard", value)
        }})

        when:
        boolean result = paymentRequest.hasStoreCard()

        then:
        result

        where:
        _ | valid
        _ | true
        _ | false

    }

    def 'given a payment request without store card hasStoreCard should return false'() {

        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("valid", new Rule() {{
                add("storeCard", null)
        }})

        when:
        boolean result = paymentRequest.hasStoreCard()

        then:
        !result

    }

}
