package br.com.unopay.api.billing.boleto.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests

class BoletoTest extends SpockApplicationTests {


    def 'should be equals'(){
        given:
        Boleto a = Fixture.from(Boleto.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(Boleto.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}
