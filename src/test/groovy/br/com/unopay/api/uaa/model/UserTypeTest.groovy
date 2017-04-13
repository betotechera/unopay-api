package br.com.unopay.api.uaa.model

import br.com.six2six.fixturefactory.Fixture
import spock.lang.Specification

class UserTypeTest extends Specification {


    def 'should be equals'(){
        given:
        UserType a = Fixture.from(UserType.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        UserType a = Fixture.from(UserType.class).gimme("valid")
        UserType b = Fixture.from(UserType.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals
    }
}