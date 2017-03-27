package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Institution
import br.com.unopay.api.bacen.model.InstitutionFilter
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.model.PaymentRuleGroupFilter
import br.com.unopay.api.bacen.repository.PaymentRuleGroupRepository
import br.com.unopay.api.uaa.model.Group
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.uaa.repository.UserDetailRepository
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page

class InstitutionServiceTest extends SpockApplicationTests {

    @Autowired
    PaymentRuleGroupService paymentRuleGroupService

    @Autowired
    InstitutionService service
    @Autowired
    PaymentRuleGroupRepository repository

    @Autowired
    UserDetailRepository userDetailRepository

    void 'should create Institution'(){
        given:
        Institution institution = Fixture.from(Institution.class).gimme("valid")

        when:
        institution =  service.create(institution)
        Institution result  = service.getById(institution.getId())

        then:
        result != null
    }


    void 'should not allow create institution with same person'(){
        given:
        Institution institution = Fixture.from(Institution.class).gimme("valid")

        when:
        service.create(institution)
        service.create(institution.with { id = null; it })

        then:
        def ex = thrown(ConflictException)
        ex.errors.first().logref == 'PERSON_INSTITUTION_ALREADY_EXISTS'
    }

    void 'known institution should be deleted'(){
        given:
        Institution institution = Fixture.from(Institution.class).gimme("valid")
        service.create(institution)
        Institution found = service.getById(institution.getId())

        when:
        service.delete(found.getId())
        service.getById(found.getId())

        then:
        thrown(NotFoundException)
    }

    void 'unknown institution should not be deleted'(){

        when:
        service.delete('12345')

        then:
        thrown(NotFoundException)
    }

    void 'given known institution should return all'(){
        given:
        List<Institution> institutionsCreate = Fixture.from(Institution.class).gimme(2, "valid")
        institutionsCreate.forEach { service.create(it) }

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<Institution> Institutions = service.findByFilter(new InstitutionFilter(), page)

        then:
            assert Institutions.content.size() > 1
    }


    void 'known paymentRuleGroup should not be deleted if has user associated'(){
        given:
        Institution institution = Fixture.from(Institution.class).gimme("persisted")

        when:
        service.delete(institution.id)
        then:
        def ex = thrown(ConflictException)
        ex.errors.first().logref == 'INSTITUTION_WITH_USERS'
    }


    void 'should update insitution '(){
        given:
        Institution institution = Fixture.from(Institution.class).gimme("valid")
        def created = service.create(institution)

        when:
        institution.person.name = 'Updated'
        institution.person.legalPersonDetail.fantasyName = 'Test Update'
        service.update(created.id,institution)
        def result = service.getById(created.id)
        then:
        result.person.name == 'Updated'
        result.person.legalPersonDetail.fantasyName == 'Test Update'

    }

}
