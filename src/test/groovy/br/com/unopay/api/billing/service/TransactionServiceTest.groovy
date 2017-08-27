package br.com.unopay.api.billing.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.billing.model.Transaction
import br.com.unopay.api.order.model.CreditOrder
import br.com.unopay.api.order.service.CreditOrderService
import org.springframework.beans.factory.annotation.Autowired

class TransactionServiceTest extends SpockApplicationTests{

    @Autowired
    TransactionService service

    def 'a valid transaction should be created'(){
        given:
        Transaction creditOrder = Fixture.from(Transaction.class).gimme("valid")

        when:
        Transaction created = service.create(creditOrder)
        Transaction result = service.findById(created.id)

        then:
        result != null

    }
}
