package br.com.unopay.api.uaa.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.flywaydb.test.annotation.FlywayTest
import org.springframework.beans.factory.annotation.Autowired

import static org.hamcrest.Matchers.contains
import static org.hamcrest.Matchers.not
import static spock.util.matcher.HamcrestSupport.that

class UserDetailServiceTests extends SpockApplicationTests {

    @Autowired
    UserDetailService service

    void 'when create user unknown authorities should not be saved'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        when:
        service.create(user)
        UserDetail created = service.getById(user.getId())

        then:
        that created.getAuthorities(), not(contains("ROLE_UNKNOWN"))

    }

    @FlywayTest(invokeCleanDB = true)
    void 'when create user known authorities should be saved'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        when:
        service.create(user)
        UserDetail created = service.getById(user.getId())

        then:
        that created.getAuthorities(), contains("ROLE_ADMIN")
    }


    @FlywayTest(invokeCleanDB = true)
    void 'when update user unknown authorities should not be saved'() {

        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        when:
        service.create(user)
        UserDetail created = service.getById(user.getId())
        user.getAuthorities().add("ROLE_UNKNOWN")
        service.update(created)
        UserDetail updated = service.getById(user.getId())

        then:
        that updated.getAuthorities(), not(contains("ROLE_UNKNOWN"))
    }

    @FlywayTest(invokeCleanDB = true)
    void 'when update user known authorities should be saved'() {

        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        when:
        service.create(user)
        UserDetail created = service.getById(user.getId())
        user.getAuthorities().add("ROLE_USER")
        service.update(created)
        UserDetail updated = service.getById(user.getId())

        then:
        that updated.getAuthorities(), contains("ROLE_ADMIN")
    }

    @FlywayTest(invokeCleanDB = true)
    void 'given user with group should not be created'(){
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("with-group")
        when:
        service.create(user)

        then:
        thrown(UnprocessableEntityException)
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
