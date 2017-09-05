package br.com.unopay.api.billing.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.billing.model.Gateway
import br.com.unopay.api.billing.model.Transaction
import org.springframework.beans.factory.annotation.Autowired

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
        Transaction transaction = Fixture.from(Transaction.class).gimme("valid")

        when:
        service.create(transaction)

        then:
        1 * gatewayMock.createTransaction(transaction)
    }

    def 'when create transaction should saved'(){
        given:
        Transaction transaction = Fixture.from(Transaction.class).gimme("valid")

        when:
        Transaction created = service.create(transaction)
        Transaction result = service.findById(created.id)

        then:
        result != null
    }
}
