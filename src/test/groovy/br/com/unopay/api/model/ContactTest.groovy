package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests

class ContactTest extends SpockApplicationTests {

    def 'should be equals'(){
        given:
        Contact a = Fixture.from(Contact.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        Contact a = Fixture.from(Contact.class).gimme("valid")
        Contact b = Fixture.from(Contact.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}