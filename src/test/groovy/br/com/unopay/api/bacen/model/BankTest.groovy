package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class BankTest extends FixtureApplicationTest {

    def 'should be equals'(){
        given:
        Bank a = Fixture.from(Bank.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        List list = Fixture.from(Bank.class).gimme(2, "valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals

    }
}
