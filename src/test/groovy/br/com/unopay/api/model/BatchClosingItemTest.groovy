package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class BatchClosingItemTest extends FixtureApplicationTest {

    def 'should be equals'(){
        given:
        BatchClosingItem a = Fixture.from(BatchClosingItem.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        BatchClosingItem a = Fixture.from(BatchClosingItem.class).gimme("valid")
        BatchClosingItem b = Fixture.from(BatchClosingItem.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}
