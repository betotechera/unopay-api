package br.com.unopay.api.order.receiver

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.billing.boleto.service.TicketService
import br.com.unopay.api.billing.creditcard.model.PaymentMethod
import br.com.unopay.api.billing.creditcard.model.Transaction
import br.com.unopay.api.billing.creditcard.model.TransactionStatus
import br.com.unopay.api.billing.creditcard.service.TransactionService
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.order.model.OrderType
import br.com.unopay.api.order.service.OrderProcessor
import br.com.unopay.api.service.ContractService
import br.com.unopay.api.util.GenericObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Unroll

class OrderReceiverTest extends  FixtureApplicationTest {

    ObjectMapper objectMapper = new ObjectMapper()
    GenericObjectMapper genericObjectMapper = new GenericObjectMapper(objectMapper)
    TransactionService transactionalServiceMock = Mock(TransactionService)
    ContractorInstrumentCreditService instrumentCreditServiceMock = Mock(ContractorInstrumentCreditService)
    OrderProcessor orderProcessorMock = Mock(OrderProcessor)
    ContractService contractServiceMock = Mock(ContractService)
    TicketService boletoServiceMock = Mock(TicketService)

    def 'when receive boleto order should call boleto service'(){
        given:
        def receiver = createOrderReceiver()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("paymentRequest.method", PaymentMethod.BOLETO)
        }})
        def valueAsString = objectMapper.writeValueAsString(creditOrder)
        when:
        receiver.orderCreated(valueAsString)

        then:
        0 * transactionalServiceMock.create(_)
        1 * boletoServiceMock.createForOrder(_)
    }

    def 'when receive credit card order should call transaction service'(){
        given:
        def receiver = createOrderReceiver()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("paymentRequest.method", PaymentMethod.CARD)
        }})
        def valueAsString = objectMapper.writeValueAsString(creditOrder)
        when:
        receiver.orderCreated(valueAsString)

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
        receiver.orderCreated(valueAsString)

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
        receiver.orderCreated(valueAsString)

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
        receiver.orderCreated(valueAsString)

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
        receiver.orderCreated(valueAsString)

        then:
        1 * transactionalServiceMock.create(_) >> new Transaction() {{ setStatus(TransactionStatus.CAPTURED)}}

        then:
        1 * orderProcessorMock.processWithStatus(creditOrder.id, TransactionStatus.CAPTURED)
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
        receiver.orderCreated(valueAsString)

        then:
        1 * transactionalServiceMock.create(_) >> new Transaction() {{ setStatus(TransactionStatus.CAPTURED)}}

        then:
        0 * instrumentCreditServiceMock.processOrder(creditOrder)

        where:
        _ | type
        _ | OrderType.INSTALLMENT_PAYMENT
        _ | OrderType.ADHESION
    }

    def 'when update order should call order service'(){
        given:
        def receiver = createOrderReceiver()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("paymentRequest.method", PaymentMethod.CARD)
            add("type", OrderType.CREDIT)
        }})
        def valueAsString = objectMapper.writeValueAsString(creditOrder)
        when:
        receiver.orderUpdated(valueAsString)

        then:
        0 * transactionalServiceMock.create(_)

        then:
        1 * orderProcessorMock.process(creditOrder)
    }

    private OrderReceiver createOrderReceiver() {
        new OrderReceiver(transactionalServiceMock, genericObjectMapper, orderProcessorMock, boletoServiceMock)
    }
}
