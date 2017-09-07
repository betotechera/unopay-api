package br.com.unopay.api.billing.creditcard.gateway.payzen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.billing.creditcard.gateway.payzen.model.Payzen
import br.com.unopay.api.billing.creditcard.gateway.payzen.model.PayzenResponseTranslator
import br.com.unopay.api.billing.creditcard.model.Transaction
import br.com.unopay.api.billing.creditcard.model.TransactionStatus

class PayzenServiceTest  extends FixtureApplicationTest {

    Payzen payzenMock = Mock(Payzen)
    PayzenResponseTranslator translator = Mock(PayzenResponseTranslator)

    def 'a valid transaction should be created'(){
        given:
        def service = new PayzenService(payzenMock, translator)
        Transaction transaction = Fixture.from(Transaction.class).gimme("valid")

        when:
        service.createTransaction(transaction)

        then:
        1 * payzenMock.createTransaction(transaction)
    }

    def 'a return should be translated'(){
        given:
        def service = new PayzenService(payzenMock, translator)
        Transaction transaction = Fixture.from(Transaction.class).gimme("valid")

        when:
        service.createTransaction(transaction)

        then:
        1 * translator.translate(_)
    }

    def 'a set translator return in transaction'(){
        given:
        def service = new PayzenService(payzenMock, translator)
        Transaction transaction = Fixture.from(Transaction.class).gimme("valid")

        when:
        def result = service.createTransaction(transaction)

        then:
        translator.translate(_) >> TransactionStatus.CANCEL_PENDING
        result.status == TransactionStatus.CANCEL_PENDING
    }
}
