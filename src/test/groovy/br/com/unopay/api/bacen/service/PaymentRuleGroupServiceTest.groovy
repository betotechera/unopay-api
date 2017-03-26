package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.model.PaymentRuleGroupFilter
import br.com.unopay.api.bacen.repository.PaymentRuleGroupRepository
import br.com.unopay.api.uaa.model.Authority
import br.com.unopay.api.uaa.model.Group
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.uaa.model.UserType
import br.com.unopay.api.uaa.repository.GroupRepository
import br.com.unopay.api.uaa.repository.UserDetailRepository
import br.com.unopay.api.uaa.repository.UserTypeRepository
import br.com.unopay.api.uaa.service.GroupService
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page

import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import static spock.util.matcher.HamcrestSupport.that

class PaymentRuleGroupServiceTest extends SpockApplicationTests {

    @Autowired
    PaymentRuleGroupService service

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

    void 'should not create paymentRuleGroup with large name'(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        group.name = """In sem justo, commodo ut, suscipit at, pharetra vitae, 
                        orci. Duis sapien nunc, commodo et, interdum suscipit, sollicitudin et, dolor. 
                        Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. 
                        Aliquam id dolor. Class aptent taciti sociosqu ad litora"""

        when:
        service.create(group)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'LARGE_PAYMENT_RULE_GROUP_NAME'
    }

    void 'should not create paymentRuleGroup with large code'(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        group.code = """In sem justo, commodo ut, suscipit at, pharetra vitae, 
                        orci. Duis sapien nunc, commodo et, interdum suscipit, sollicitudin et, dolor. 
                        Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. 
                        Aliquam id dolor. Class aptent taciti sociosqu ad litora"""

        when:
        service.create(group)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'LARGE_PAYMENT_RULE_GROUP_CODE'
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

    void 'should not create paymentRuleGroup with short name'(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        group.name = "aa"

        when:
        service.create(group)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'SHORT_PAYMENT_RULE_GROUP_NAME'
    }

    void 'should not create paymentRuleGroup with short code'(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        group.code = "aa"

        when:
        service.create(group)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'SHORT_PAYMENT_RULE_GROUP_CODE'
    }

    void 'should not allow create paymentRuleGroups with same codes'(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")

        when:
        service.create(group)
        service.create(group.with { id = null; it })

        then:
        def ex = thrown(ConflictException)
        ex.errors.first().logref == 'PAYMENT_RULE_GROUP_CODE_ALREADY_EXISTS'
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

    void 'should delete group '(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        def created = service.create(group)

        when:
        service.delete(created.id)
        service.getById(created.id)
        then:
        thrown(NotFoundException)
    }

    void 'known paymentRuleGroup should not be deleted if has user associated'(){
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("persisted")

        when:
        service.delete(group.id)
        then:
        def ex = thrown(ConflictException)
        ex.errors.first().logref == 'PAYMENT_RULE_GROUP_WITH_USERS'
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


    void 'when create paymentRuleGroup without userRelationship should return error'() {
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        group.userRelationship = null
        when:
        service.create(group)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find()?.logref == 'USER_RELATIONSHIP_REQUIRED'
    }


}
