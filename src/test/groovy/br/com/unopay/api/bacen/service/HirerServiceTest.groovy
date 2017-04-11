package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.model.filter.HirerFilter
import br.com.unopay.api.bacen.repository.PaymentRuleGroupRepository
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page

class HirerServiceTest extends SpockApplicationTests {

    @Autowired
    HirerService service
    @Autowired
    PaymentRuleGroupRepository repository

    void 'should create Hirer'(){
        given:
        Hirer hirer = Fixture.from(Hirer.class).gimme("valid")

        when:
        hirer =  service.create(hirer)
        Hirer result  = service.getById(hirer.getId())

        then:
        result != null
    }


    void 'should not allow create hirer with same person'(){
        given:
        Hirer hirer = Fixture.from(Hirer.class).gimme("valid")

        when:
        service.create(hirer)
        service.create(hirer.with { id = null; it })

        then:
        def ex = thrown(ConflictException)
        ex.errors.first().logref == 'PERSON_HIRER_ALREADY_EXISTS'
    }

    void 'known hirer should be deleted'(){
        given:
        Hirer hirer = Fixture.from(Hirer.class).gimme("valid")
        service.create(hirer)
        Hirer found = service.getById(hirer.getId())

        when:
        service.delete(found.getId())
        service.getById(found.getId())

        then:
        thrown(NotFoundException)
    }

    void 'unknown hirer should not be deleted'(){

        when:
        service.delete('12345')

        then:
        thrown(NotFoundException)
    }

    void 'given known hirer should return all'(){
        given:
        List<Hirer> hirersCreate = Fixture.from(Hirer.class).gimme(2, "valid")
        hirersCreate.forEach { service.create(it) }

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<Hirer> Hirers = service.findByFilter(new HirerFilter(), page)

        then:
            assert Hirers.content.size() > 1
    }


    void 'should update hirer '(){
        given:
        Hirer hirer = Fixture.from(Hirer.class).gimme("valid")
        def created = service.create(hirer)

        when:
        hirer.person.name = 'Updated'
        hirer.person.legalPersonDetail.fantasyName = 'Test Update'
        service.update(created.id,hirer)
        def result = service.getById(created.id)
        then:
        result.person.name == 'Updated'
        result.person.legalPersonDetail.fantasyName == 'Test Update'

    }

}
