package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class BatchClosingTest extends FixtureApplicationTest {

    def 'should be equals'(){
        given:
        BatchClosing a = Fixture.from(BatchClosing.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        BatchClosing a = Fixture.from(BatchClosing.class).gimme("valid")
        BatchClosing b = Fixture.from(BatchClosing.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}
