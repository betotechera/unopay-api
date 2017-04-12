package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Hired
import br.com.unopay.api.bacen.model.filter.HiredFilter
import br.com.unopay.api.bacen.repository.PaymentRuleGroupRepository
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page

class HiredServiceTest extends SpockApplicationTests {

    @Autowired
    HiredService service
    @Autowired
    PaymentRuleGroupRepository repository

    void 'should create Hired'(){
        given:
        Hired hired = Fixture.from(Hired.class).gimme("valid")

        when:
        hired =  service.create(hired)
        Hired result  = service.getById(hired.getId())

        then:
        result != null
    }


    void 'should not allow create hired with same person'(){
        given:
        Hired hired = Fixture.from(Hired.class).gimme("valid")

        when:
        service.create(hired)
        service.create(hired.with { id = null; it })

        then:
        def ex = thrown(ConflictException)
        ex.errors.first().logref == 'PERSON_HIRED_ALREADY_EXISTS'
    }

    void 'known hired should be deleted'(){
        given:
        Hired hired = Fixture.from(Hired.class).gimme("valid")
        service.create(hired)
        Hired found = service.getById(hired.getId())

        when:
        service.delete(found.getId())
        service.getById(found.getId())

        then:
        thrown(NotFoundException)
    }

    void 'unknown hired should not be deleted'(){

        when:
        service.delete('12345')

        then:
        thrown(NotFoundException)
    }

    void 'given known hired should return all'(){
        given:
        List<Hired> hiredsCreate = Fixture.from(Hired.class).gimme(2, "valid")
        hiredsCreate.forEach { service.create(it) }

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<Hired> Hireds = service.findByFilter(new HiredFilter(), page)

        then:
            assert Hireds.content.size() > 1
    }


    void 'should update hired '(){
        given:
        Hired hired = Fixture.from(Hired.class).gimme("valid")
        def created = service.create(hired)

        when:
        hired.person.name = 'Updated'
        hired.person.legalPersonDetail.fantasyName = 'Test Update'
        service.update(created.id,hired)
        def result = service.getById(created.id)
        then:
        result.person.name == 'Updated'
        result.person.legalPersonDetail.fantasyName == 'Test Update'

    }

}
