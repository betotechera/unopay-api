package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class PersonTest extends FixtureApplicationTest {

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

    void 'given cellphone without pattern should be normalized'(){
        given:
        def numberSent = sent
        Person person = Fixture.from(Person.class).gimme("physical", new Rule(){{
            add("cellPhone", numberSent)
        }})

        when:
        person.normalize()

        then:
        person.cellPhone == expected

        where:
        sent               | expected
        '(011) 99559-8877' | '011995598877'
        '011-41559988'     | '01141559988'
    }

    void 'given physical document number without pattern should be normalized'(){
        given:
        def numberSent = sent
        Person person = Fixture.from(Person.class).gimme("physical", new Rule(){{
            add("document.number", numberSent)
            add("document.type", DocumentType.CPF)
        }})

        when:
        person.normalize()

        then:
        person.documentNumber() == expected

        where:
        sent              | expected
        '436.215.648-88'  | '43621564888'
        '57,6.208-88/551' | '57620888551'
    }

    void 'given legal document number without pattern should be normalized'(){
        given:
        def numberSent = sent
        Person person = Fixture.from(Person.class).gimme("legal", new Rule(){{
            add("document.number", numberSent)
            add("document.type", DocumentType.CNPJ)
        }})

        when:
        person.normalize()

        then:
        person.documentNumber() == expected

        where:
        sent                   | expected
        '38.788.782/0001-48'   | '38788782000148'
        '656_29%795;0,001/-47' | '65629795000147'
    }

    void 'given telephone without pattern should be normalized'(){
        given:
        def numberSent = sent
        Person person = Fixture.from(Person.class).gimme("physical", new Rule(){{
            add("telephone", numberSent)
        }})

        when:
        person.normalize()

        then:
        person.telephone == expected

        where:
        sent               | expected
        '(011) 99559-8877' | '011995598877'
        '011-41559988'     | '01141559988'
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
