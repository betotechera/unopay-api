package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests

class BankTest extends SpockApplicationTests {

    def 'should be equals'(){
        given:
        Bank a = Fixture.from(Bank.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        Bank a = Fixture.from(Bank.class).gimme("valid")
        Bank b = Fixture.from(Bank.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}
