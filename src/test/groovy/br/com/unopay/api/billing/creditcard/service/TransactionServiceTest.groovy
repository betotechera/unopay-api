package br.com.unopay.api.billing.creditcard.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.billing.creditcard.model.Gateway
import br.com.unopay.api.billing.creditcard.model.PaymentRequest
import br.com.unopay.api.billing.creditcard.model.Transaction
import br.com.unopay.api.billing.creditcard.model.TransactionStatus
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class TransactionServiceTest extends SpockApplicationTests{

    @Autowired
    TransactionService service

    Gateway gatewayMock = Mock(Gateway)

    def setup(){
        service.gateway = gatewayMock
    }

    def 'a valid transaction should be created'(){
        given:
        Transaction transaction = Fixture.from(Transaction.class).gimme("valid")

        when:
        Transaction created = service.save(transaction)
        Transaction result = service.findById(created.id)

        then:
        result != null
    }

    def 'when create transaction should use payment gateway'(){
        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest.class).gimme("creditCard")

        when:
        service.create(paymentRequest)

        then:
        1 * gatewayMock.createTransaction(_)
    }

    def 'given a payment request without the issuer document should not be processed'(){
        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest.class).gimme("creditCard", new Rule(){{
            add("issuerDocument", null)
        }})

        when:
        service.create(paymentRequest)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ISSUER_REQUIRED'
    }



    def 'when create transaction should saved'(){
        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest.class).gimme("creditCard")

        when:
        Transaction created = service.create(paymentRequest)
        Transaction result = service.findById(created.id)

        then:
        result != null
    }

    def 'given a order with pending transaction should not be created again'(){
        given:
        Transaction transaction = Fixture.from(Transaction.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("status", TransactionStatus.PENDING)
        }})
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest.class).gimme("creditCard", new Rule(){{
            add("orderId", transaction.orderId)
        }})
        when:
        service.create(paymentRequest)

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'ORDER_WITH_PENDING_TRANSACTION'
    }

    @Unroll
    def 'given a order with processed transaction and status #status should not be created again'(){
        given:
        def st = status
        Transaction transaction = Fixture.from(Transaction.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("status", st)
        }})
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest.class).gimme("creditCard", new Rule(){{
            add("orderId", transaction.orderId)
        }})
        when:
        service.create(paymentRequest)

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'ORDER_WITH_PROCESSED_TRANSACTION'

        where:
        _ | status
        _ | TransactionStatus.AUTHORIZED
        _ | TransactionStatus.CAPTURED
    }


    def 'given a payment request without order id should not be created'(){
        given:
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest.class).gimme("creditCard", new Rule(){{
            add("orderId", null)
        }})
        when:
        service.create(paymentRequest)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ORDER_REQUIRED'
    }

    def 'a transaction without payment request should not be created'(){
        given:
        when:
        service.create(null)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'PAYMENT_REQUEST_REQUIRED'
    }

    @Unroll
    'given a transaction with #invalidValue value should not be processed'(){
        given:
        def value = invalidValue
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest.class).gimme("creditCard", new Rule(){{
            add("value", value)
        }})
        when:
        service.create(paymentRequest)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INVALID_PAYMENT_VALUE'

        where:
        _ | invalidValue
        _ | 0.0
        _ | null
        _ | -1.0
    }
}
