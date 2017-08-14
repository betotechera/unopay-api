package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.SpockApplicationTests

class PaymentBankAccountTest extends FixtureApplicationTest {

    def 'should be equals'(){
        given:
        PaymentBankAccount a = Fixture.from(PaymentBankAccount.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        PaymentBankAccount a = Fixture.from(PaymentBankAccount.class).gimme("valid")
        PaymentBankAccount b = Fixture.from(PaymentBankAccount.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}
