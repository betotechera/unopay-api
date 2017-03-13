package br.com.unopay.api.uaa.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.uaa.model.Group
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import br.com.unopay.bootcommons.jsoncollections.Results
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.flywaydb.test.annotation.FlywayTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import static spock.util.matcher.HamcrestSupport.that

class GroupServiceTest extends SpockApplicationTests {

    @Autowired
    GroupService service

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
        Group group1 = Fixture.from(Group.class).gimme("valid")
        service.create(group1)
        Group group2 = Fixture.from(Group.class).gimme("valid")
        service.create(group2)

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
}
