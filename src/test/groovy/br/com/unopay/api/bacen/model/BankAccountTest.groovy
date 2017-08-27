package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class BankAccountTest  extends FixtureApplicationTest {

    def 'should be equals'(){
        given:
        BankAccount a = Fixture.from(BankAccount.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        BankAccount a = Fixture.from(BankAccount.class).gimme("valid")
        BankAccount b = Fixture.from(BankAccount.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}
