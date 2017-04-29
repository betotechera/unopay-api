package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.HirerBranch
import br.com.unopay.api.bacen.model.filter.HirerBranchFilter
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page

class HirerBranchServiceTest extends SpockApplicationTests {

    @Autowired
    HirerBranchService service

    void 'should create Hirer'(){
        given:
        HirerBranch hirer = Fixture.from(HirerBranch.class).gimme("valid")

        when:
        hirer =  service.create(hirer)
        HirerBranch result  = service.getById(hirer.getId())

        then:
        result != null
    }


    void 'should not allow create hirer with same person'(){
        given:
        HirerBranch hirer = Fixture.from(HirerBranch.class).gimme("valid")

        when:
        service.create(hirer)
        service.create(hirer.with { id = null; it })

        then:
        def ex = thrown(ConflictException)
        ex.errors.first().logref == 'PERSON_HIRER_ALREADY_EXISTS'
    }

    void 'known hirer should be deleted'(){
        given:
        HirerBranch hirer = Fixture.from(HirerBranch.class).gimme("valid")
        service.create(hirer)
        HirerBranch found = service.getById(hirer.getId())

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
        List<HirerBranch> hirersCreate = Fixture.from(HirerBranch.class).gimme(2, "valid")
        hirersCreate.forEach { service.create(it) }

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<HirerBranch> hirerBranches = service.findByFilter(new HirerBranchFilter(), page)

        then:
            assert hirerBranches.content.size() > 1
    }


    void 'should update hirer '(){
        given:
        HirerBranch hirer = Fixture.from(HirerBranch.class).gimme("valid")
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
