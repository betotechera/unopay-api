package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class HirerBranchTest extends FixtureApplicationTest {

    def 'should be equals'(){
        given:
        HirerBranch a = Fixture.from(HirerBranch.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        HirerBranch a = Fixture.from(HirerBranch.class).gimme("valid")
        HirerBranch b = Fixture.from(HirerBranch.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}
