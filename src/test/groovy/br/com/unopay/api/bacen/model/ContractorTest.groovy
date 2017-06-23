package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.model.PersonType
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class ContractorTest extends SpockApplicationTests {

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