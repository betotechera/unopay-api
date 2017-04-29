package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.model.DocumentType
import br.com.unopay.api.model.Person
import br.com.unopay.api.model.filter.PersonFilter
import br.com.unopay.api.repository.PersonRepository
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class PersonServiceTest extends SpockApplicationTests {

    @Autowired
    PersonService service

    @Autowired
    PersonRepository repository


    void 'should save PHYSICAL Person'(){
        given:
        Person person = Fixture.from(Person.class).gimme("physical")

        when:
        def result  = service.save(person)

        then:
        assert result.id != null
        assert result.address.id != null
    }


    void 'should save LEGAL Person'(){
        given:
        Person person = Fixture.from(Person.class).gimme("legal")

        when:
        def result  = service.save(person)

        then:
        assert result.id != null
        assert result.address.id != null
        assert result.legalPersonDetail.id != null
    }

    void 'should not allow create Person with same document'(){
        given:
        Person person = Fixture.from(Person.class).gimme("legal")

        when:
        service.save(person)
        service.save(person.with { id = null; it })

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'PERSON_DOCUMENT_ALREADY_EXISTS'
    }

    void 'given a document, should find a Person'(){
        given:
        Person person = Fixture.from(Person.class).gimme("legal")

        when:
        service.save(person)
        def filter = new PersonFilter(documentType: person.document.type, documentNumber: person.document.number)
        def result = service.findByDocument(filter)

        then:
        assert result.id == person.id
        assert result.document.number == person.document.number
    }

    void 'given a invalid document, should return not found'(){
        given:
        Person person = Fixture.from(Person.class).gimme("legal")

        when:
        service.save(person)
        def filter = new PersonFilter(documentType: DocumentType.CNH, documentNumber: person.document.number)
         service.findByDocument(filter)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PERSON_WITH_DOCUMENT_NOT_FOUND'
    }


}
