package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests

class HiredTest extends SpockApplicationTests {

    def 'should be equals'(){
        given:
        Hired a = Fixture.from(Hired.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        Hired a = Fixture.from(Hired.class).gimme("valid")
        Hired b = Fixture.from(Hired.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}