package br.com.unopay.api.uaa.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.network.model.AccreditedNetwork
import br.com.unopay.api.network.model.Establishment
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.model.Institution
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.notification.model.EventType
import br.com.unopay.api.notification.service.NotificationService
import br.com.unopay.api.uaa.infra.PasswordTokenService
import br.com.unopay.api.uaa.model.Group
import br.com.unopay.api.uaa.model.NewPassword
import br.com.unopay.api.uaa.model.RequestOrigin
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.uaa.model.UserType
import br.com.unopay.api.uaa.model.filter.UserFilter
import br.com.unopay.api.uaa.repository.UserTypeRepository
import br.com.unopay.bootcommons.exception.BadRequestException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import static org.hamcrest.Matchers.contains
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.not
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.security.crypto.password.PasswordEncoder
import static spock.util.matcher.HamcrestSupport.that

class UserDetailServiceTests extends SpockApplicationTests {

    @Autowired
    UserDetailService service

    @Autowired
    UserTypeRepository userTypeRepository

    @Autowired
    GroupService groupService

    NotificationService notificationService = Mock(NotificationService)
    PasswordEncoder passwordEncoder = Mock(PasswordEncoder)
    PasswordTokenService passwordTokenService = Mock(PasswordTokenService)

    def setup(){
        service.notificationService = notificationService
        service.passwordEncoder = passwordEncoder
        service.passwordTokenService = passwordTokenService
    }

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

    void 'when create user should send password notification'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")

        when:
        service.create(user)

        then:
        1 * notificationService.sendNewPassword(user, _)
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


    void 'when get user should return all groups authorities'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        Group group1 = Fixture.from(Group.class).gimme("valid")
        Group group2 = Fixture.from(Group.class).gimme("valid")
        group1.getAuthorities().find().name = 'ROLE_ADMIN'
        group2.getAuthorities().find().name = 'ROLE_USER'
        groupService.create(group1)
        groupService.create(group2)
        user.addToMyGroups(group1)
        user.addToMyGroups(group2)

        when:
        service.create(user)
        def userResult = service.getById(user.getId())

        then:
        userResult.getGroupsAuthorities()?.any { it.name == "ROLE_ADMIN" }
        userResult.getGroupsAuthorities()?.any { it.name == "ROLE_USER" }
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

    void 'when update user adding userTypes should set userTypes'() {

        given:
        UserDetail user = Fixture.from(UserDetail.class).uses(jpaProcessor).gimme("without-group", new Rule() {{
            add("hirer", Fixture.from(Hirer.class).uses(jpaProcessor).gimme("valid"))
        }})

        def institution = Fixture.from(Institution.class).uses(jpaProcessor).gimme("valid")
        user.institution = institution
        def issuer = Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid")
        user.issuer = issuer
        def accreditedNetwork = Fixture.from(AccreditedNetwork.class).uses(jpaProcessor).gimme("valid")
        user.accreditedNetwork = accreditedNetwork
        def establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        user.establishment = establishment
        user.hirer = null

        when:
        def updated = service.update(user)

        then:
        updated.institution.id == institution.id
        updated.issuer.id == issuer.id
        updated.accreditedNetwork.id == accreditedNetwork.id
        updated.establishment.id == establishment.id
        updated.hirer == null
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

    void 'when create user should return all required attributes'(){
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        when:
        service.create(user)
        UserDetail created = service.getById(user.getId())

        then:
        created.id != null
        created.email != null
        created.name != null
    }

    void 'when create user with password should not send a password notification mail'(){
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        def password = '123456'
        user.password = password

        when:
        service.create(user)

        then:
        1 * passwordEncoder.encode(_) >> password
        0 * notificationService.sendNewPassword(_,_)
    }

    void 'should create user with existing userType'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        UserType userType = Fixture.from(UserType.class).gimme("valid")
        userTypeRepository.save(userType)
        user.type = userType

        when:
        def created = service.create(user)

        then:
        assert created.type != null
    }

    void 'should create user without password'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        user.password = null

        when:
        def created = service.create(user)

        then:
        assert created.type != null
    }

    void 'when create user without user type should create with default type'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        user.type = null
        when:
        def created = service.create(user)

        then:
        assert created.type != null
    }
    void 'when create user with type INSTITUIDOR should have a INSTITUTION'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-institution")
        when:
        service.create(user)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find()?.logref == 'USER_TYPE_MUST_SET_AN_INSTITUTION'
    }

    void 'success creating user with type INSTITUIDOR and a Insitution'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("with-institution")
        when:
        def created = service.create(user)

        then:
            assert  created.id
    }

    void 'when create user with unknown user type should return error'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        UserType userType = Fixture.from(UserType.class).gimme("valid")
        user.type = userType.with { id = null; it }
        when:
        service.create(user)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find()?.logref == 'USER_TYPE_NOT_FOUND'
    }


    void 'when find user by known name should return'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        service.create(user)
        def userSearch = new UserFilter().with { name = user.name; it }

        when:
        def page = new UnovationPageRequest() {{ setPage(1); setSize(20) }}
        Page<UserDetail> users = service.findByFilter(userSearch, page)

        then:
        that users.content, hasSize(1)
    }

    void 'when find user by known email should return'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        service.create(user)
        def userSearch = new UserFilter().with { email = user.email; it }

        when:
        def page = new UnovationPageRequest() {{ setPage(1); setSize(20) }}
        Page<UserDetail> users = service.findByFilter(userSearch, page)

        then:
        that users.content, hasSize(1)
    }

    void 'when find user by known group name should return'() {
        given:
        List<UserDetail> users = Fixture.from(UserDetail.class).gimme(2, "without-group")
        List<Group> groups = Fixture.from(Group.class).gimme(2, "valid")
        groups.each { groupService.create(it) }
        def user1 = users.find()
        def user2 = users.last()
        user1.addToMyGroups(groups)
        user2.addToMyGroups(groups.last())
        service.create(user1)
        service.create(user2)
        def userSearch = new UserFilter().with { groupName = groups.find().name; it }

        when:
        def page = new UnovationPageRequest() {{ setPage(1); setSize(20) }}
        Page<UserDetail> usersFound = service.findByFilter(userSearch, page)

        then:
        that usersFound.content, hasSize(1)
    }

    void 'when find user by unknown name should not be returned'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        service.create(user)
        def userSearch = new UserFilter().with { name = '12oooouuu'; it }

        when:
        def page = new UnovationPageRequest() {{ setPage(1); setSize(20) }}
        Page<UserDetail> users = service.findByFilter(userSearch, page)

        then:
        that users.content, hasSize(0)
    }

    void 'should delete known user'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        def created = service.create(user)

        when:
        service.delete(created.getId())
        def result = userTypeRepository.findById(created.getId())

        then:
        result == null
    }

    void 'when delete unknown user should return error'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")

        when:
        service.delete(user.getId())

        then:
        thrown(NotFoundException)
    }


    void 'given a known user should update password by email'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        def created = service.create(user)
        def newPassword = 'teste'
        def pwd = new NewPassword(password: newPassword)

        when:
        service.updatePasswordByEmail(created.getEmail(), pwd)
        def result = service.getById(created.getId())

        then:
        1 * passwordEncoder.encode(_) >> newPassword
        result.password == newPassword
    }

    void 'given a unknown user when update password by email should return error'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")

        when:
        def pwd = new NewPassword(password: 'teste')
        service.updatePasswordByEmail(user.getId(), pwd)

        then:
        thrown(NotFoundException)
    }

    void 'given a known user should send reset password by email'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        def created = service.create(user)
        when:
        service.resetPasswordByEmail(created.getEmail(), RequestOrigin.UNOPAY.name())

        then:
        1 * notificationService.sendNewPassword(_, EventType.PASSWORD_RESET,_)
    }

    void 'given a unknown user when set reset password by email should return error'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")

        when:
        service.resetPasswordByEmail(user.getEmail(), RequestOrigin.UNOPAY.name())

        then:
        thrown(NotFoundException)
    }

    void 'given a known user should send reset password by id'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        def created = service.create(user)
        when:
        service.resetPasswordById(created.getId())

        then:
        1 * notificationService.sendNewPassword(_,EventType.PASSWORD_RESET,_)
    }

    void 'given a unknown user when set reset password by id should return error'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")

        when:
        service.resetPasswordById(user.getEmail())

        then:
        thrown(NotFoundException)
    }

    void 'when reset password by email without requestOrigin should return error'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        service.create(user)

        when:
        service.resetPasswordByEmail(user.getEmail(), null)

        then:
        thrown(BadRequestException)

    }

    void 'given a known user when update password by token with valid token should updated'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        def created = service.create(user)
        def newPassword = 'teste'
        def token = 'ADSKIRFE'
        def pwd = new NewPassword(password: newPassword, token: token)

        when:
        service.updatePasswordByToken(pwd)
        def result = service.getById(created.getId())

        then:
        1 * passwordTokenService.getUserIdByToken(token) >> created.id
        1 * passwordEncoder.encode(_) >> newPassword
        1 * passwordTokenService.remove(token)
        result.password == newPassword
    }

    void 'given a known user when update password by token with invalid token should updated'() {
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        service.create(user)
        def newPassword = 'teste'
        def token = 'ADSKIRFE'
        def pwd = new NewPassword(password: newPassword, token: token)

        when:
        service.updatePasswordByToken(pwd)

        then:
        1 * passwordTokenService.getUserIdByToken(token) >> null
        0 * passwordEncoder.encode(_) >> newPassword
        0 * passwordTokenService.remove(token)
        thrown(NotFoundException)
    }

}
