package br.com.unopay.api.uaa.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.uaa.model.Authority
import br.com.unopay.api.uaa.model.Group
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.uaa.repository.UserDetailRepository
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.flywaydb.test.annotation.FlywayTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import spock.lang.Ignore

import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import static spock.util.matcher.HamcrestSupport.that

class GroupServiceTest extends SpockApplicationTests {

    @Autowired
    GroupService service

    @Autowired
    UserDetailRepository userDetailRepository;

    void 'should create group'(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")

        when:
        service.create(group)
        Group result = service.getById(group.getId())

        then:
        result != null
    }

    void 'given without name group should not bet created'(){
        given:
        Group group = Fixture.from(Group.class).gimme("without-name")

        when:
        service.create(group)

        then:
        thrown(UnprocessableEntityException)
    }

    void 'known group should be deleted'(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")
        service.create(group)
        Group found = service.getById(group.getId())

        when:
        service.delete(group.getId())
        Group result = service.getById(group.getId())

        then:
        found != null
        result == null
    }

    void 'unknown group should not be deleted'(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")

        when:
        service.delete(group.getId())

        then:
        thrown(NotFoundException)
    }

    @FlywayTest(invokeCleanDB = true)
    void 'given known groups should return all'(){
        given:
        List<Group> groupsCreate = Fixture.from(Group.class).gimme(2, "valid")
        groupsCreate.forEach { service.create(it) }

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<Group> groups = service.findAll(page)

        then:
        that groups.content, hasSize(2)
    }

    @FlywayTest(invokeCleanDB = true)
    void 'given unknown groups should return empty list'(){
        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(1)}}
        Page<Group> groups = service.findAll(page)

        then:
        that groups.content, hasSize(0)
    }

    @FlywayTest(invokeCleanDB = true)
    void 'should create group with authorities'(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")
        def result = service.create(group)
        Set<Authority> authorities = Fixture.from(Authority.class).gimme(2, "valid")
        Set<String> authoritiesIds = authorities.collect { it.name }
        when:
        service.addAuthorities(group.getId(), authoritiesIds)
        def page = new UnovationPageRequest() {{ setPage(1); setSize(20) }}
        def members = service.findAuhtorities(result.getId(), page)

        then:
        that members?.content, hasSize(authorities?.size())
        authorities?.any { members.any { m -> m.name == it.name } }

    }

    @FlywayTest(invokeCleanDB = true)
    void 'given unknown authorities when add Authority should return error'(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")
        service.create(group)
        Set<Authority> authorities = Fixture.from(Authority.class).gimme(2, "unknown")
        Set<String> authoritiesIds = authorities.collect { it.name }
        when:
        service.addAuthorities(group.getId(), authoritiesIds)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert  ex.getMessage() == "known authorities required"
    }

    void 'should not add authorities without group id'(){
        given:
        Set<Authority> authorities = Fixture.from(Authority.class).gimme(2, "unknown")
        Set<String> authoritiesIds = authorities.collect { it.name }
        when:
        service.addAuthorities(null, authoritiesIds)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert  ex.getMessage() == "Group required"
    }

    @FlywayTest(invokeCleanDB = true)
    void 'given unknown members when add members should return error'(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")
        service.create(group)
        Set<UserDetail> users = Fixture.from(UserDetail.class).gimme(2, "without-group")
        Set<String> membersIds = users.collect { it.id }
        when:
        service.addMembers(group.getId(), membersIds)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert  ex.getMessage() == "known members required"
    }

    void 'should not add member without group id'(){
        given:
        Set<UserDetail> users = Fixture.from(UserDetail.class).gimme(2, "without-group")
        Set<String> membersIds = users.collect { it.id }
        when:
        service.addMembers(null, membersIds)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert  ex.getMessage() == "Group required"
    }

    @FlywayTest(invokeCleanDB = true)
    void 'should create group with members'(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")
        def result = service.create(group)
        Set<UserDetail> users = Fixture.from(UserDetail.class).gimme(2, "without-group")
        userDetailRepository.save(users)
        Set<String> membersIds = users.collect { it.id }
        when:
        service.addMembers(group.getId(), membersIds)
        def page = new UnovationPageRequest() {{ setPage(1); setSize(20) }}
        def members = service.findMembers(result.getId(), page)

        then:
        that members?.content, hasSize(users?.size())
        users?.any { members.any { m -> m.email == it.email } }
    }

}
