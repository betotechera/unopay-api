package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests

class HirerTest  extends SpockApplicationTests {

    def 'should be equals'(){
        given:
        Hirer a = Fixture.from(Hirer.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        Hirer a = Fixture.from(Hirer.class).gimme("valid")
        Hirer b = Fixture.from(Hirer.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}