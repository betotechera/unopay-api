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
        def result  = service.create(person)

        then:
        assert result.id != null
        assert result.address.id != null
    }

    void 'should save LEGAL Person'(){
        given:
        Person person = Fixture.from(Person.class).gimme("legal")

        when:
        def result  = service.create(person)

        then:
        assert result.id != null
        assert result.address.id != null
        assert result.legalPersonDetail.id != null
    }

    void 'given a person without id and an existing document number should update it'(){
        given:
        Person person = Fixture.from(Person.class).gimme(type)
        def expectedName = 'Teste'
        when:
        service.create(person)
        service.create(person.with { id = null; name = expectedName; it })

        def result = service.findByDocument(person.documentNumber())

        then:
        result.documentNumber() == person.documentNumber()
        result.name == expectedName

        where:
        _ | type
        _ | 'legal'
        _ | 'physical'
    }

    void 'given a person without document and an existing id should update it'(){
        given:
        Person person = Fixture.from(Person.class).gimme(type)
        def expectedName = 'Teste'
        when:
        service.create(person)
        service.create(person.with { document.number = null; name = expectedName; it })

        def result = service.findById(person.getId())

        then:
        result.getId() == person.getId()
        result.name == expectedName

        where:
        _ | type
        _ | 'legal'
        _ | 'physical'
    }

    void 'given a document, should find a Person'(){
        given:
        Person person = Fixture.from(Person.class).gimme(type)

        when:
        service.create(person)
        def filter = new PersonFilter(documentType: person.document.type, documentNumber: person.document.number)
        def result = service.findByFilter(filter)

        then:
        assert result.id == person.id
        assert result.document.number == person.document.number

        where:
        _ | type
        _ | 'legal'
        _ | 'physical'
    }

    void 'given a invalid document, should return not found'(){
        given:
        Person person = Fixture.from(Person.class).gimme(type)

        when:
        service.create(person)
        def filter = new PersonFilter(documentType: DocumentType.CNH, documentNumber: person.document.number)
         service.findByFilter(filter)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PERSON_WITH_DOCUMENT_NOT_FOUND'

        where:
        _ | type
        _ | 'legal'
        _ | 'physical'
    }


}
