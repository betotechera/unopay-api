package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests

class EventTest  extends SpockApplicationTests {




    def 'should be equals'(){
        given:
        Event a = Fixture.from(Event.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        List list = Fixture.from(Event.class).gimme(2,"allFields")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}
