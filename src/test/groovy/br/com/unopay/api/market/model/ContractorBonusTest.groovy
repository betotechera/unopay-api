package br.com.unopay.api.market.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class ContractorBonusTest extends FixtureApplicationTest {

    def 'should be equals'() {

        given:
        ContractorBonus a = Fixture.from(ContractorBonus.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(ContractorBonus.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}