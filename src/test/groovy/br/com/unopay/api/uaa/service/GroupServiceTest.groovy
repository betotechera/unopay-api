package br.com.unopay.api.uaa.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.uaa.model.Authority
import br.com.unopay.api.uaa.model.Group
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.uaa.model.UserType
import br.com.unopay.api.uaa.repository.GroupRepository
import br.com.unopay.api.uaa.repository.UserDetailRepository
import br.com.unopay.api.uaa.repository.UserTypeRepository
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import static spock.util.matcher.HamcrestSupport.that

class GroupServiceTest extends SpockApplicationTests {

    @Autowired
    GroupService service

    @Autowired
    UserDetailRepository userDetailRepository

    @Autowired
    UserTypeRepository userTypeRepository

    @Autowired
    GroupRepository repository

    void 'should create group'(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")

        when:
        service.create(group)
        Group result = service.getById(group.getId())

        then:
        result != null
    }

    void 'should not create group with large name'(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")
        group.name = """In sem justo, commodo ut, suscipit at, pharetra vitae, 
                        orci. Duis sapien nunc, commodo et, interdum suscipit, sollicitudin et, dolor. 
                        Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. 
                        Aliquam id dolor. Class aptent taciti sociosqu ad litora"""

        when:
        service.create(group)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'LARGE_GROUP_NAME'
    }

    void 'should not create group with large description'(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")
        group.description = """In sem justo, commodo ut, suscipit at, pharetra vitae, 
                        orci. Duis sapien nunc, commodo et, interdum suscipit, sollicitudin et, dolor. 
                        Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. 
                        Aliquam id dolor. Class aptent taciti sociosqu ad litora
                        Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. 
                        Aliquam id dolor. Class aptent taciti sociosqu ad litora
                        """

        when:
        service.create(group)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'LARGE_GROUP_DESCRIPTION'
    }

    void 'should create group without description'(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")
        group.description = null
        when:
        def created = service.create(group)

        then:
        created != null
    }

    void 'should not create group with short name'(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")
        group.name = "aa"

        when:
        service.create(group)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'SHORT_GROUP_NAME'
    }

    void 'should not allow create groups with same name'(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")

        when:
        service.create(group)
        service.create(group.with { id = null; it })

        then:
        def ex = thrown(ConflictException)
        ex.errors.first().logref == 'GROUP_NAME_ALREADY_EXISTS'
    }

    void 'given group without name should not bet created'(){
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
        service.getById(group.getId())

        then:
        found != null
        thrown(NotFoundException)
    }

    void 'unknown group should not be deleted'(){
        given:
        repository.deleteAll()
        Group group = Fixture.from(Group.class).gimme("valid")

        when:
        service.delete(group.getId())

        then:
        thrown(NotFoundException)
    }

    void 'given known groups should return all'(){
        given:
        repository.deleteAll()
        List<Group> groupsCreate = Fixture.from(Group.class).gimme(2, "valid")
        groupsCreate.forEach { service.create(it) }

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<Group> groups = service.findAll(page)

        then:
        that groups.content, hasSize(2)
    }

    void 'given unknown groups should return empty list'(){
        given:
        repository.deleteAll()

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(1)}}
        Page<Group> groups = service.findAll(page)

        then:
        that groups.content, hasSize(0)
    }

    void 'should create group with authorities'(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")
        def result = service.create(group)
        Set<Authority> authorities = Fixture.from(Authority.class).gimme(2, "valid")
        Set<String> authoritiesIds = authorities.collect { it.name }
        when:
        service.addAuthorities(group.getId(), authoritiesIds)
        List<Authority> groupAuthorities = service.findAuthorities(result.getId())

        then:
        that groupAuthorities, hasSize(groupAuthorities?.size())
        groupAuthorities?.any { groupAuthorities.any { m -> m.name == it.name } }

    }

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
        assert  ex.errors.first().logref == "KNOWN_AUTHORITIES_REQUIRED"
    }

    void 'should not add authorities without group id'(){
        given:
        repository.deleteAll()
        Set<Authority> authorities = Fixture.from(Authority.class).gimme(2, "unknown")
        Set<String> authoritiesIds = authorities.collect { it.name }
        when:
        service.addAuthorities(null, authoritiesIds)

        then:
        def ex = thrown(NotFoundException)
        assert  ex.errors.find()?.logref == 'GROUP_NOT_FOUND'
    }

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
        assert  ex.errors.first().logref == "KNOWN_MEMBERS_REQUIRED"
    }

    void 'should not add member without group id'(){
        given:
        repository.deleteAll()
        Set<UserDetail> users = Fixture.from(UserDetail.class).gimme(2, "without-group")
        Set<String> membersIds = users.collect { it.id }
        when:
        service.addMembers(null, membersIds)

        then:
        def ex = thrown(NotFoundException)
        assert  ex.errors.find()?.logref == "GROUP_NOT_FOUND"
    }

    void 'should add known members to known group'(){
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


    void 'when find authority with unknown group id should return empty result'(){
        when:
        def members = service.findAuthorities('1111')

        then:
        that members, hasSize(0)
    }

    void 'when find authority without group id should return error'(){
        when:
        service.findAuthorities(null)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'GROUP_ID_REQUIRED'
    }

    void 'when find member with unknown group id should return empty result'(){
        when:
        def page = new UnovationPageRequest() {{ setPage(1); setSize(20) }}
        def members = service.findMembers('1111', page)

        then:
        that members?.content, hasSize(0)
    }

    void 'when find member without group id should return error'(){
        when:
        def page = new UnovationPageRequest() {{ setPage(1); setSize(20) }}
        service.findMembers(null, page)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'GROUP_ID_REQUIRED'
    }


    void 'should associate known groups to known member'(){
        given:
        Set<Group> groups = Fixture.from(Group.class).gimme(2, "valid")
        repository.save(groups)
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        def result = userDetailRepository.save(user)
        Set<String> groupsIds = groups.collect { it.id }
        when:
        service.associateUserWithGroups(user.getId(), groupsIds)
        def userGroups = service.findUserGroups(result.getId())

        then:
        that userGroups, hasSize(groups?.size())
        groups?.any { userGroups.any { m -> m.name == it.name } }
    }

    void 'should not be associate any group when unknown groups found'(){
        given:
        Set<Group> groups = Fixture.from(Group.class).gimme(2, "valid")
        repository.save(groups)
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        userDetailRepository.save(user)
        Set<String> groupsIds = groups.collect { it.id }
        groupsIds.add('id_not_found')
        when:
        service.associateUserWithGroups(user.getId(), groupsIds)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find().logref == 'UNKNOWN_GROUP_FOUND'
        assert '[id_not_found]' in ex.errors.find().arguments.find()
    }

    void 'given unknown groups when associate to user should return error'(){
        given:
        Set<Group> groups = Fixture.from(Group.class).gimme(2, "with-id")
        Set<String> groupsIds = groups.collect { it.id }
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        userDetailRepository.save(user)
        when:
        service.associateUserWithGroups(user.getId(), groupsIds)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert  ex.errors.first().logref == "KNOWN_GROUPS_REQUIRED"
    }

    void 'should not add group without user id'(){
        given:
        Set<Group> groups = Fixture.from(Group.class).gimme(2, "valid")
        Set<String> groupsIds = groups.collect { it.id }
        when:
        service.associateUserWithGroups(null, groupsIds)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert  ex.errors.first().logref == "USER_REQUIRED"
    }

    void 'when find group with unknown user email should return empty result'(){
        when:
        def groups = service.findUserGroups('asdf@sadf')

        then:
        that groups, hasSize(0)
    }

    void 'when find group without user email should return error'(){
        when:
        service.findUserGroups(null)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'USER_REQUIRED'
    }

    void 'should delete group '(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")
        def created = service.create(group)

        when:
        service.delete(created.id)
        service.getById(created.id)
        then:
        thrown(NotFoundException)
    }


    void 'should update group '(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")
        def created = service.create(group)

        when:
        group.name = 'Updated'
        group.description = 'Test Update'
        service.update(created.id,group)
        def result = service.getById(created.id)
        then:
        assert result.name == 'Updated'
        assert result.description == 'Test Update'

    }

    void 'should not delete group when has known members'(){
        given:
        Group group = Fixture.from(Group.class).gimme("valid")
        def result = service.create(group)
        Set<UserDetail> users = Fixture.from(UserDetail.class).gimme(2, "without-group")
        userDetailRepository.save(users)
        Set<String> membersIds = users.collect { it.id }
        when:
        service.addMembers(group.getId(), membersIds)
        service.delete(result.id)
        then:
        def ex = thrown(ConflictException)
        ex.errors.first().logref == 'GROUP_WITH_MEMBERS'
    }

    void 'should create group with existing userType'() {
        given:
        Group group = Fixture.from(Group.class).gimme("valid")

        UserType userType = Fixture.from(UserType.class).gimme("valid")
        userTypeRepository.save(userType)
        group.userType = userType

        when:
        def created =  service.create(group)

        then:
        assert created.userType != null
    }

    void 'when create user without user type should return error'() {
        given:
        Group group = Fixture.from(Group.class).gimme("valid")
        group.userType = null
        when:
        service.create(group)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find()?.logref == 'USER_TYPE_REQUIRED'
    }

    void 'when create user with unknown user type should return error'() {
        given:
        Group group = Fixture.from(Group.class).gimme("valid")
        UserType userType = Fixture.from(UserType.class).gimme("valid")
        group.userType = userType.with { id = null; it }
        when:
        service.create(group)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find()?.logref == 'USER_TYPE_NOT_FOUND'
    }

}
