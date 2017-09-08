package br.com.unopay.api.order.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.billing.creditcard.model.TransactionStatus
import spock.lang.Unroll

class CreditOrderTest extends FixtureApplicationTest {

    @Unroll
    'given a #status status the order status should be waiting payment'(){
        def order = new CreditOrder()

        when:
        order.defineStatus(status)

        then:
        order.status == OrderStatus.WAITING_PAYMENT

        where:
        _ | status
        _ | TransactionStatus.AUTHORIZED
        _ | TransactionStatus.IN_ANALYSIS
        _ | TransactionStatus.PENDING
        _ | TransactionStatus.ERROR
    }

    @Unroll
    'given a #status status the order status should be paid'(){
        def order = new CreditOrder()

        when:
        order.defineStatus(status)

        then:
        order.status == OrderStatus.PAID

        where:
        _ | status
        _ | TransactionStatus.CAPTURED
        _ | TransactionStatus.CAPTURE_RECEIVED
    }

    @Unroll
    'given a #status status the order status should be canceled'(){
        def order = new CreditOrder()

        when:
        order.defineStatus(status)

        then:
        order.status == OrderStatus.CANCELED

        where:
        _ | status
        _ | TransactionStatus.CANCELED
        _ | TransactionStatus.CANCEL_PENDING
        _ | TransactionStatus.REFUND
    }

    def 'given a denied status the order status should be payment denied'() {
        def order = new CreditOrder()

        when:
        order.defineStatus(TransactionStatus.DENIED)

        then:
        order.status == OrderStatus.PAYMENT_DENIED
    }


    def 'given a previous number should increment number'(){
        given:
        def order = new CreditOrder()

        when:
        order.incrementNumber("0000000001")

        then:
        order.number == '0000000002'
    }

    def 'should create order with pending status'(){
        when:
        def order = new CreditOrder()

        then:
        order.status == OrderStatus.WAITING_PAYMENT
    }

    def 'should increment number without previous number'(){
        given:
        def order = new CreditOrder()

        when:
        order.incrementNumber(null)

        then:
        order.number == '0000000001'
    }

    def 'should be equals'(){
        given:
        CreditOrder a = Fixture.from(CreditOrder.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'() {
        List list = Fixture.from(CreditOrder.class).gimme(2, "valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals

    }
}
