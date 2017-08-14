package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.SpockApplicationTests

class PartnerTest  extends FixtureApplicationTest {

    def 'should be equals'(){
        given:
        Partner a = Fixture.from(Partner.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        Partner a = Fixture.from(Partner.class).gimme("valid")
        Partner b = Fixture.from(Partner.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}
