package br.com.unopay.api.model;

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class PersonTest extends SpockApplicationTests {

   void 'when validating Person with invalid PersonType and Document should return error'() {
       given:
       Person person = Fixture.from(Person.class).gimme("legal")
       Document cpf = Fixture.from(Document.class).gimme("valid-cpf")
       person.setDocument(cpf)
       when:
       person.validate()

       then:
       def ex = thrown(UnprocessableEntityException)
       ex.errors.find()?.logref == 'INVALID_DOCUMENT_TYPE_FOR_USER'
   }

    void 'when validating LEGAL Person without legalPersonDetail should return error'() {
        given:
        Person person = Fixture.from(Person.class).gimme("legal")
        person.legalPersonDetail = null
        when:
        person.validate()

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find()?.logref == 'LEGAL_PERSON_DETAIL_IS_REQUIRED_FOR_LEGAL_PERSON'
    }

    def 'should be equals'(){
        given:
        Person a = Fixture.from(Person.class).gimme("legal")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        Person a = Fixture.from(Person.class).gimme("legal")
        Person b = Fixture.from(Person.class).gimme("legal")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals
    }

}
