package br.com.unopay.api.order.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.billing.creditcard.model.PaymentMethod
import br.com.unopay.api.billing.creditcard.model.PaymentRequest
import br.com.unopay.api.billing.creditcard.model.TransactionStatus
import br.com.unopay.bootcommons.exception.UnauthorizedException
import static java.math.BigDecimal.ONE
import org.apache.commons.beanutils.BeanUtils
import spock.lang.Unroll

class OrderTest extends FixtureApplicationTest {

    @Unroll
    'given a #status status the order status should be waiting payment'(){
        def order = new Order()
        order.setValue(ONE)

        when:
        order.defineStatus(status)

        then:
        order.status == PaymentStatus.WAITING_PAYMENT

        where:
        _ | status
        _ | TransactionStatus.AUTHORIZED
        _ | TransactionStatus.IN_ANALYSIS
        _ | TransactionStatus.PENDING
        _ | TransactionStatus.ERROR
    }

    @Unroll
    'given a #status status the order status should be paid'(){
        def order = new Order()
        order.setValue(ONE)

        when:
        order.defineStatus(status)

        then:
        order.status == PaymentStatus.PAID

        where:
        _ | status
        _ | TransactionStatus.CAPTURED
        _ | TransactionStatus.CAPTURE_RECEIVED
    }

    @Unroll
    'given a #status status the order status should be canceled'(){
        def order = new Order()
        order.setValue(ONE)

        when:
        order.defineStatus(status)

        then:
        order.status == PaymentStatus.CANCELED

        where:
        _ | status
        _ | TransactionStatus.CANCELED
        _ | TransactionStatus.CANCEL_PENDING
        _ | TransactionStatus.REFUND
    }

    def 'given a denied status the order status should be payment denied'() {
        def order = new Order()
        order.setValue(ONE)

        when:
        order.defineStatus(TransactionStatus.DENIED)

        then:
        order.status == PaymentStatus.PAYMENT_DENIED
    }

    def 'should create order with pending status'(){
        when:
        def order = new Order()
        order.setValue(ONE)

        then:
        order.status == PaymentStatus.WAITING_PAYMENT
    }

    def 'when update me should update only status'(){
        given:
        Order orderA = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("status", PaymentStatus.WAITING_PAYMENT)
        }})

        Order cloned = BeanUtils.cloneBean(orderA)

        Order orderB = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("status", PaymentStatus.PAID)
        }})

        when:
        orderA.updateOnly(orderB, "status")

        then:
        orderA.status == orderB.status
        orderA.number == cloned.number
        orderA.type == cloned.type
    }

    def 'given a known order with status canceled when trying to validate update should return error' (){
        given:
        Order order = Fixture.from(Order.class)gimme("valid", new Rule() {{
            add("status", PaymentStatus.CANCELED)
        }})

        when:
        order.validateUpdate()

        then:
        def ex = thrown(UnauthorizedException)
        ex.errors.first().logref == 'UNABLE_TO_UPDATE_ORDER_STATUS'

    }

    def 'should be equals'(){
        given:
        Order a = Fixture.from(Order.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'() {
        List list = Fixture.from(Order.class).gimme(2, "valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals

    }

    def 'given an order with payment request hasPaymentRequest should return true'(){

        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("creditCard")
        Order order = Fixture.from(Order).gimme("valid", new Rule(){{
            add("paymentRequest", paymentRequest)
        }})

        when:
        boolean result = order.hasPaymentRequest()

        then:
        result
    }

    def 'given an order without payment request hasPaymentRequest should return false'(){

        given:
        Order order = Fixture.from(Order).gimme("valid", new Rule(){{
            add("paymentRequest", null)
        }})

        when:
        boolean result = order.hasPaymentRequest()

        then:
        !result

    }

    def 'given an order with type other than adhesion and with payment request with storeCard equals true and method equals card shouldStoreCard should return true'(){

        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("creditCard", new Rule() {{
            add("storeCard", true)
            add("method", PaymentMethod.CARD)
        }})
        Order order = Fixture.from(Order).gimme("valid", new Rule(){{
            add("paymentRequest", paymentRequest)
            add("type", OrderType.INSTALLMENT_PAYMENT)
        }})

        when:
        boolean result = order.shouldStoreCard()

        then:
        result

    }

    def 'given an order with type adhesion shouldStoreCard should return false'(){

        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("creditCard", new Rule() {{
            add("storeCard", true)
            add("method", PaymentMethod.CARD)
        }})
        Order order = Fixture.from(Order).gimme("valid", new Rule(){{
            add("paymentRequest", paymentRequest)
            add("type", OrderType.ADHESION)
        }})

        when:
        boolean result = order.shouldStoreCard()

        then:
        result

    }

}
