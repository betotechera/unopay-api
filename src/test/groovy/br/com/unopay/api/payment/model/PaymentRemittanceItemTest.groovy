package br.com.unopay.api.payment.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class PaymentRemittanceItemTest extends FixtureApplicationTest{

    def 'should not be equals'(){
        List list = Fixture.from(PaymentRemittanceItem.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }

    def 'if the credit has a credit'(){
        given:
        PaymentRemittanceItem a = Fixture.from(PaymentRemittanceItem.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }
}
