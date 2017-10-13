package br.com.unopay.api.order.receiver

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.billing.boleto.service.BoletoService
import br.com.unopay.api.billing.creditcard.model.PaymentMethod
import br.com.unopay.api.billing.creditcard.model.Transaction
import br.com.unopay.api.billing.creditcard.model.TransactionStatus
import br.com.unopay.api.billing.creditcard.service.TransactionService
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.order.model.OrderType
import br.com.unopay.api.order.service.OrderService
import br.com.unopay.api.service.ContractService
import br.com.unopay.api.util.GenericObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Unroll

class OrderReceiverTest extends  FixtureApplicationTest {

    ObjectMapper objectMapper = new ObjectMapper()
    GenericObjectMapper genericObjectMapper = new GenericObjectMapper(objectMapper)
    TransactionService transactionalServiceMock = Mock(TransactionService)
    ContractorInstrumentCreditService instrumentCreditServiceMock = Mock(ContractorInstrumentCreditService)
    OrderService orderServiceMock = Mock(OrderService)
    ContractService contractServiceMock = Mock(ContractService)
    BoletoService boletoServiceMock = Mock(BoletoService)

    def setup(){
        orderServiceMock.findById(_) >> Fixture.from(Order.class).gimme("valid")
    }

    def 'when receive boleto order should call boleto service'(){
        given:
        def receiver = createOrderReceiver()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("paymentRequest.method", PaymentMethod.BOLETO)
        }})
        def valueAsString = objectMapper.writeValueAsString(creditOrder)
        when:
        receiver.transactionNotify(valueAsString)

        then:
        0 * transactionalServiceMock.create(_)
        1 * boletoServiceMock.create(_)
    }

    def 'when receive credit card order should call transaction service'(){
        given:
        def receiver = createOrderReceiver()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("paymentRequest.method", PaymentMethod.CARD)
        }})
        def valueAsString = objectMapper.writeValueAsString(creditOrder)
        when:
        receiver.transactionNotify(valueAsString)

        then:
        1 * transactionalServiceMock.create(_)  >> new Transaction()
    }

    @Unroll
    'when receive order with #method method should not call transaction service'(){
        given:
        def receiver = createOrderReceiver()
        def paymentMethod = method
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("paymentRequest.method", paymentMethod)
        }})
        def valueAsString = objectMapper.writeValueAsString(creditOrder)
        when:
        receiver.transactionNotify(valueAsString)

        then:
        0 * transactionalServiceMock.create(_)  >> new Transaction()

        where:
        _ | method
        _ | PaymentMethod.BOLETO
        _ | PaymentMethod.DIRECT_DEBIT
    }

    def 'given a payment installment order without paid status should not call installment service'(){
        given:
        def receiver = createOrderReceiver()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("paymentRequest.method", PaymentMethod.CARD)
            add("type", OrderType.CREDIT)
        }})
        transactionalServiceMock.create(_) >> new Transaction() {{ setStatus(TransactionStatus.DENIED)}}
        def valueAsString = objectMapper.writeValueAsString(creditOrder)

        when:
        receiver.transactionNotify(valueAsString)

        then:
        0 * contractServiceMock.markInstallmentAsPaidFrom(creditOrder)
    }

    def 'given a credit order without paid status should not call credit service'(){
        given:
        def receiver = createOrderReceiver()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("paymentRequest.method", PaymentMethod.CARD)
            add("type", OrderType.CREDIT)
        }})
        transactionalServiceMock.create(_) >> new Transaction() {{ setStatus(TransactionStatus.DENIED)}}
        def valueAsString = objectMapper.writeValueAsString(creditOrder)
        when:
        receiver.transactionNotify(valueAsString)

        then:
        0 * instrumentCreditServiceMock.processOrder(creditOrder)
    }

    def 'given a credit order with paid status and credit type should call order service'(){
        given:
        def receiver = createOrderReceiver()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("paymentRequest.method", PaymentMethod.CARD)
            add("type", OrderType.CREDIT)
        }})
        def valueAsString = objectMapper.writeValueAsString(creditOrder)
        when:
        receiver.transactionNotify(valueAsString)

        then:
        1 * transactionalServiceMock.create(_) >> new Transaction() {{ setStatus(TransactionStatus.CAPTURED)}}

        then:
        1 * orderServiceMock.process(creditOrder)
    }

    @Unroll
    'given a transaction with #type type and paid status should not call credit service'(){
        given:
        def orderType = type
        def receiver = createOrderReceiver()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("paymentRequest.method", PaymentMethod.CARD)
            add("type", orderType)
        }})
        def valueAsString = objectMapper.writeValueAsString(creditOrder)
        when:
        receiver.transactionNotify(valueAsString)

        then:
        1 * transactionalServiceMock.create(_) >> new Transaction() {{ setStatus(TransactionStatus.CAPTURED)}}

        then:
        0 * instrumentCreditServiceMock.processOrder(creditOrder)

        where:
        _ | type
        _ | OrderType.INSTALLMENT_PAYMENT
        _ | OrderType.ADHESION
    }

    def 'given a credit card order should ever save order'(){
        given:
        def receiver = createOrderReceiver()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("paymentRequest.method", PaymentMethod.CARD)
        }})
        def valueAsString = objectMapper.writeValueAsString(creditOrder)
        def paymentStatus = status

        when:
        receiver.transactionNotify(valueAsString)

        then:
        1 * transactionalServiceMock.create(_) >> new Transaction() {{ setStatus(paymentStatus)}}

        then:
        1 * orderServiceMock.save(creditOrder)

        where:
        _ | status
        _ | TransactionStatus.CAPTURED
        _ | TransactionStatus.DENIED
    }

    private OrderReceiver createOrderReceiver() {
        new OrderReceiver(transactionalServiceMock, genericObjectMapper, orderServiceMock, boletoServiceMock)
    }
}
