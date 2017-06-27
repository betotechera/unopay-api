package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.model.filter.PaymentRuleGroupFilter
import br.com.unopay.api.bacen.repository.PaymentRuleGroupRepository
import br.com.unopay.api.uaa.repository.UserDetailRepository
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page

class PaymentRuleGroupServiceTest extends SpockApplicationTests {

    @Autowired
    PaymentRuleGroupService service

    @Autowired
    InstitutionService institutionService
    @Autowired
    PaymentRuleGroupRepository repository

    @Autowired
    UserDetailRepository userDetailRepository

    void 'should create paymentRuleGroup'(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")

        when:
        service.create(group)
        PaymentRuleGroup result = service.getById(group.getId())

        then:
        result != null
    }




    void 'should create paymentRuleGroup without optional values'(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        group.purpose = null
        group.scope = null
        when:
        def created = service.create(group)

        then:
        created != null
    }

    void 'should not allow create paymentRuleGroups with same codes'(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        PaymentRuleGroup second = Fixture.from(PaymentRuleGroup.class).gimme("valid")

        when:
        service.create(group)
        second.code = group.code
        service.create(second)

        then:
        def ex = thrown(ConflictException)
        ex.errors.first().logref == 'PAYMENT_RULE_GROUP_CODE_ALREADY_EXISTS'
    }

    void 'should not allow create paymentRuleGroups with same values'(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")

        when:
        service.create(group)
        service.create(group.with { id = null;code = code+'1'; it })

        then:
        def ex = thrown(ConflictException)
        ex.errors.first().logref == 'PAYMENT_RULE_GROUP_ALREADY_EXISTS'
    }


    void 'given paymentRuleGroups without name should not bet created'(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("without-name")

        when:
        service.create(group)

        then:
        thrown(UnprocessableEntityException)
    }


    void 'given paymentRuleGroups without code should not bet created'(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("without-code")

        when:
        service.create(group)

        then:
        thrown(UnprocessableEntityException)
    }

    void 'known paymentRuleGroup should be deleted'(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        service.create(group)
        PaymentRuleGroup found = service.getById(group.getId())

        when:
        service.delete(group.getId())
        service.getById(group.getId())

        then:
        found != null
        thrown(NotFoundException)
    }

    void 'unknown paymentRuleGroup should not be deleted'(){

        when:
        service.delete('12345')

        then:
        thrown(NotFoundException)
    }

    void 'given known paymentRuleGroup should return all'(){
        given:
        List<PaymentRuleGroup> groupsCreate = Fixture.from(PaymentRuleGroup.class).gimme(2, "valid")
        groupsCreate.forEach { service.create(it) }

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<PaymentRuleGroup> groups = service.findByFilter(new PaymentRuleGroupFilter(), page)

        then:
            assert groups.content.size() > 2
    }

    void 'should delete PaymentRuleGroup '(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        def created = service.create(group)

        when:
        service.delete(created.id)
        service.getById(created.id)
        then:
        thrown(NotFoundException)
    }

    void 'should update paymentRuleGroup '(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        def created = service.create(group)

        when:
        group.name = 'Updated'
        group.code = 'Test Update'
        service.update(created.id,group)
        def result = service.getById(created.id)
        then:
        assert result.name == 'Updated'
        assert result.code == 'Test Update'

    }





}
