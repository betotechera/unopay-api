package br.com.unopay.api.market.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class BonusBillingTest extends FixtureApplicationTest {
    def 'should be equal'(){
        given:
        BonusBilling a = Fixture.from(BonusBilling.class).gimme("valid")

        when:
        def equals = a == a

        then:
        equals
    }

    def 'should not be equal'(){
        given:
        BonusBilling a = Fixture.from(BonusBilling.class).gimme("valid")
        BonusBilling b = Fixture.from(BonusBilling.class).gimme("valid")

        when:
        def equals = a == b

        then:
        !equals
    }
}
