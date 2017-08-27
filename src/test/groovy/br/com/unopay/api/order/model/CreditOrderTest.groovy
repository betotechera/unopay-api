package br.com.unopay.api.order.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class CreditOrderTest extends FixtureApplicationTest {

    def 'should be equals'(){
        given:
        CreditOrder a = Fixture.from(CreditOrder.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'() {
        List list = Fixture.from(CreditOrder.class).gimme(2, "valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals

    }
}
