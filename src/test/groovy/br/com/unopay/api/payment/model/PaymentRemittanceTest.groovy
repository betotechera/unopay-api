package br.com.unopay.api.payment.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class PaymentRemittanceTest extends FixtureApplicationTest{

    def 'should not be equals'(){
        List list = Fixture.from(PaymentRemittance.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }

    def 'if the credit has a credit'(){
        given:
        PaymentRemittance a = Fixture.from(PaymentRemittance.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }
}
