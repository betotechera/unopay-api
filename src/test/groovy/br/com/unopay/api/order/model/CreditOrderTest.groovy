package br.com.unopay.api.order.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class CreditOrderTest extends FixtureApplicationTest {

    def 'given a previous number should increment number'(){
        given:
        def order = new CreditOrder()

        when:
        order.incrementNumber("0000000001")

        then:
        order.number == '0000000002'
    }

    def 'should increment number without previous number'(){
        given:
        def order = new CreditOrder()

        when:
        order.incrementNumber(null)

        then:
        order.number == '0000000001'
    }

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
