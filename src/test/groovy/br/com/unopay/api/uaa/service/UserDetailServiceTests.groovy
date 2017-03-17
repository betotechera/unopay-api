package br.com.unopay.api.uaa.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.uaa.model.Group
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.flywaydb.test.annotation.FlywayTest
import org.springframework.beans.factory.annotation.Autowired

import javax.transaction.Transaction
import javax.transaction.Transactional

import static org.hamcrest.Matchers.contains
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.not
import static spock.util.matcher.HamcrestSupport.that

class UserDetailServiceTests extends SpockApplicationTests {

    @Autowired
    UserDetailService service

    @Autowired
    GroupService groupService

    void 'when create user unknown authorities should not be saved'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("group-with-unknown-role")
        when:
        service.create(user)
        service.getById(user.getId())
        def userGroups = groupService.findUserGroups(user.getId())

        then:
        that user.getAuthoritiesNames(userGroups), not(contains("ROLE_UNKNOWN"))

    }

    void 'should create user with existing group'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        Group group = Fixture.from(Group.class).gimme("valid")
        groupService.create(group)
        user.addToMyGroups(group)

        when:
        service.create(user)
        def userGroups = groupService.findUserGroups(user.getId())

        then:
        that userGroups, hasSize(1)
    }

    
    void 'when create user known authorities should be saved'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        Group group = Fixture.from(Group.class).gimme("valid")
        groupService.create(group)
        user.addToMyGroups(group)

        when:
        service.create(user)
        def userGroups = groupService.findUserGroups(user.getId())

        then:
        that user.getAuthoritiesNames(userGroups), contains("ROLE_ADMIN")
    }


    
    void 'when update user unknown authorities should not be saved'() {

        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("group-with-unknown-role")
        when:
        service.create(user)
        UserDetail created = service.getById(user.getId())
        service.update(created)
        UserDetail updated = service.getById(user.getId())

        then:
        that updated.getAuthoritiesNames(), not(contains("ROLE_UNKNOWN"))
    }


    void 'given user without group should be created'(){
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        when:
        service.create(user)
        UserDetail created = service.getById(user.getId())

        then:
        created != null
    }
}
