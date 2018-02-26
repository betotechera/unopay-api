package br.com.unopay.api.billing.creditcard.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import spock.lang.Unroll

class PaymentRequestTest extends FixtureApplicationTest {

    def 'should transform to transaction'() {
        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest.class).gimme("creditCard")

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

    }

    def 'should be equals'() {
        given:
        PaymentRequest a = Fixture.from(PaymentRequest.class).gimme("creditCard")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'() {
        List list = Fixture.from(PaymentRequest.class).gimme(2, "creditCard")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals

    }

    def 'given a payment request with payment method hasPaymentMethod should return true'() {

        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("creditCard", new Rule(){{
            add("method", PaymentMethod.CARD)
        }})

        when:
        boolean result = paymentRequest.hasPaymentMethod()

        then:
        result

    }

    def 'given a payment request without payment method hasPaymentMethod should return false'() {

        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("creditCard", new Rule(){{
            add("method", null)
        }})

        when:
        boolean result = paymentRequest.hasPaymentMethod()

        then:
        !result

    }

    def 'given a payment request with store card hasStoreCard should return true'() {

        given:
        boolean value = valid
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("creditCard", new Rule(){{
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
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("creditCard", new Rule(){{
            add("storeCard", null)
        }})

        when:
        boolean result = paymentRequest.hasStoreCard()

        then:
        !result

    }

    def 'given a PaymentRequest with Method equals card and storeCard equals true, shouldStoreCard should return true'() {

        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("creditCard", new Rule(){{
            add("method", PaymentMethod.CARD)
            add("storeCard", true)
        }})

        when:
        boolean result = paymentRequest.shouldStoreCard()

        then:
        result

    }

    def 'given a PaymentRequest without Method, shouldStoreCard should return false'() {

        given:
        boolean value = trueOrFalse
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("creditCard", new Rule(){{
            add("method", null)
            add("storeCard", value)
        }})

        when:
        boolean result = paymentRequest.shouldStoreCard()

        then:
        !result

        where:
        _ | trueOrFalse
        _ | true
        _ | false

    }

    @Unroll
    def 'given a PaymentRequest with Method equals "#notCard", shouldStoreCard should return false'() {

        given:
        boolean value = trueOrFalse
        PaymentMethod paymentMethod = notCard
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("creditCard", new Rule(){{
            add("method", paymentMethod)
            add("storeCard", value)
        }})

        when:
        boolean result = paymentRequest.shouldStoreCard()

        then:
        !result

        where:
        notCard                    | trueOrFalse
        PaymentMethod.BOLETO       | true
        PaymentMethod.DIRECT_DEBIT | false

    }

    def 'given a PaymentRequest without storeCard, shouldStoreCard should return false'() {

        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("creditCard", new Rule(){{
            add("storeCard", null)
        }})

        when:
        boolean result = paymentRequest.shouldStoreCard()

        then:
        !result

    }

    def 'given a PaymentRequest with storeCard equals false, shouldStoreCard should return false'() {

        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("creditCard", new Rule(){{
            add("storeCard", false)
        }})

        when:
        boolean result = paymentRequest.shouldStoreCard()

        then:
        !result

    }

}