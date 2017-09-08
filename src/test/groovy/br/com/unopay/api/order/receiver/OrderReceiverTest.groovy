package br.com.unopay.api.order.receiver

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.billing.creditcard.model.Transaction
import br.com.unopay.api.billing.creditcard.service.TransactionService
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService
import br.com.unopay.api.order.model.CreditOrder
import br.com.unopay.api.util.GenericObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper

class OrderReceiverTest extends  FixtureApplicationTest {

    ObjectMapper objectMapper = new ObjectMapper()
    GenericObjectMapper genericObjectMapper = new GenericObjectMapper(objectMapper)
    TransactionService transactionalServiceMock = Mock(TransactionService)
    ContractorInstrumentCreditService instrumentCreditServiceMock = Mock(ContractorInstrumentCreditService)

    def 'when receive order should call transaction service'(){
        given:
        def receiver = new OrderReceiver(transactionalServiceMock, genericObjectMapper, instrumentCreditServiceMock)
        CreditOrder creditOrder = Fixture.from(CreditOrder.class).gimme("valid", new Rule(){{
            add("product", null)
        }})
        def valueAsString = objectMapper.writeValueAsString(creditOrder)
        when:
        receiver.transactionNotify(valueAsString)

        then:
        1 * transactionalServiceMock.create(_)  >> new Transaction()
    }

    def 'when process transaction should call credit service'(){
        given:
        def receiver = new OrderReceiver(transactionalServiceMock, genericObjectMapper, instrumentCreditServiceMock)
        CreditOrder creditOrder = Fixture.from(CreditOrder.class).gimme("valid", new Rule(){{
            add("product", null)
        }})
        def valueAsString = objectMapper.writeValueAsString(creditOrder)
        when:
        receiver.transactionNotify(valueAsString)

        then:
        1 * transactionalServiceMock.create(_) >> new Transaction()

        then:
        1 * instrumentCreditServiceMock.unlockCredit(creditOrder)
    }
}
