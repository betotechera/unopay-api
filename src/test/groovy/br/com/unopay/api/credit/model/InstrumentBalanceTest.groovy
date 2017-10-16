package br.com.unopay.api.credit.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class InstrumentBalanceTest  extends FixtureApplicationTest {

    def 'should be equals'(){
        given:
        InstrumentBalance a = Fixture.from(InstrumentBalance.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(InstrumentBalance.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }

}
