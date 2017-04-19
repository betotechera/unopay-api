package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Partner
import br.com.unopay.api.bacen.model.filter.PartnerFilter
import br.com.unopay.api.bacen.repository.PaymentRuleGroupRepository
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page

class PartnerServiceTest extends SpockApplicationTests {

    @Autowired
    PartnerService service
    @Autowired
    PaymentRuleGroupRepository repository

    void 'should create Partner'(){
        given:
        Partner partner = Fixture.from(Partner.class).gimme("valid")

        when:
        partner =  service.create(partner)
        Partner result  = service.getById(partner.getId())

        then:
        result != null
    }


    void 'should not allow create partner with same person'(){
        given:
        Partner partner = Fixture.from(Partner.class).gimme("valid")

        when:
        service.create(partner)
        service.create(partner.with { id = null; it })

        then:
        def ex = thrown(ConflictException)
        ex.errors.first().logref == 'PERSON_PARTNER_ALREADY_EXISTS'
    }

    void 'known partner should be deleted'(){
        given:
        Partner partner = Fixture.from(Partner.class).gimme("valid")
        service.create(partner)
        Partner found = service.getById(partner.getId())

        when:
        service.delete(found.getId())
        service.getById(found.getId())

        then:
        thrown(NotFoundException)
    }

    void 'unknown partner should not be deleted'(){

        when:
        service.delete('12345')

        then:
        thrown(NotFoundException)
    }

    void 'given known partner should return all'(){
        given:
        List<Partner> partnersCreate = Fixture.from(Partner.class).gimme(2, "valid")
        partnersCreate.forEach { service.create(it) }

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<Partner> Partners = service.findByFilter(new PartnerFilter(), page)

        then:
            assert Partners.content.size() > 1
    }


    void 'should update partner '(){
        given:
        Partner partner = Fixture.from(Partner.class).gimme("valid")
        def created = service.create(partner)

        when:
        partner.person.name = 'Updated'
        partner.person.legalPersonDetail.fantasyName = 'Test Update'
        service.update(created.id,partner)
        def result = service.getById(created.id)
        then:
        result.person.name == 'Updated'
        result.person.legalPersonDetail.fantasyName == 'Test Update'

    }

}
