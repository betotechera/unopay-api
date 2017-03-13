package br.com.unopay.api.uaa.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.uaa.service.UserDetailService
import org.flywaydb.test.annotation.FlywayTest
import org.springframework.beans.factory.annotation.Autowired

import static com.google.common.collect.Sets.newHashSet
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric
import static org.hamcrest.Matchers.contains
import static org.hamcrest.Matchers.not
import static spock.util.matcher.HamcrestSupport.that

class UserDetailServiceTests extends SpockApplicationTests {

    @Autowired
    UserDetailService service

    void 'when create user unknown authorities should not be saved'() {
        given:
        UserDetail user = new UserDetail(randomNumeric(5),
                "test@integrationtest.com",
                "123",
                newHashSet("ROLE_UNKNOWN", "ROLE_ADMIN"))
        when:
        service.create(user)
        UserDetail created = service.getById(user.getId())

        then:
        that created.getAuthorities(), not(contains("ROLE_UNKNOWN"))

    }

    @FlywayTest(invokeCleanDB = true)
    void 'when create user known authorities should be saved'() {
        given:
        UserDetail user = new UserDetail(randomNumeric(5),
                "test@integrationtest.com",
                "123",
                newHashSet("ROLE_UNKNOWN", "ROLE_ADMIN"))
        when:
        service.create(user)
        UserDetail created = service.getById(user.getId())

        then:
        that created.getAuthorities(), contains("ROLE_ADMIN")
    }


    @FlywayTest(invokeCleanDB = true)
    void 'when update user unknown authorities should not be saved'() {

        given:
        UserDetail user = new UserDetail(randomNumeric(5),
                "test@integrationtest.com",
                "123",
                newHashSet("ROLE_UNKNOWN", "ROLE_ADMIN"))
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
        UserDetail user = new UserDetail(randomNumeric(5),
                "test@integrationtest.com",
                "123",
                newHashSet("ROLE_UNKNOWN", "ROLE_ADMIN"))
        when:
        service.create(user)
        UserDetail created = service.getById(user.getId())
        user.getAuthorities().add("ROLE_USER")
        service.update(created)
        UserDetail updated = service.getById(user.getId())

        then:
        that updated.getAuthorities(), contains("ROLE_ADMIN")
    }

    void 'user with group should be created'(){

    }

    void 'user without group should be created'(){

    }

    void 'when create user with group should be return authorities'(){

    }

    void 'when create user without group should not be return authorities'(){

    }
}
