package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.SpockApplicationTests

class CheckoutTest extends FixtureApplicationTest {

    def 'should be equals'(){
        given:
        Checkout a = Fixture.from(Checkout.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        Checkout a = Fixture.from(Checkout.class).gimme("valid")
        Checkout b = Fixture.from(Checkout.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}
