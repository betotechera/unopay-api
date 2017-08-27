package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class ContractorTest extends FixtureApplicationTest {

    def 'should be equals'(){
        given:
        Contractor a = Fixture.from(Contractor.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        Contractor a = Fixture.from(Contractor.class).gimme("valid")
        Contractor b = Fixture.from(Contractor.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }

}