package br.com.unopay.api.uaa.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.bacen.model.Contractor
import org.apache.commons.lang.RandomStringUtils

class UserDetailTest extends FixtureApplicationTest {

    def 'when create from contractor should define contractor'(){
        given:
        Contractor contractor = Fixture.from(Contractor.class).gimme("physical")

        when:
        def userDetail = new UserDetail(contractor)

        then:
        userDetail.contractor == contractor
    }

    def 'when create from contractor should define name'(){
        given:
        Contractor contractor = Fixture.from(Contractor.class).gimme("physical")

        when:
        def userDetail = new UserDetail(contractor)

        then:
        userDetail.name == contractor.person.shortName
    }

    def 'when create from contractor should define email'(){
        given:
        Contractor contractor = Fixture.from(Contractor.class).gimme("physical")

        when:
        def userDetail = new UserDetail(contractor)

        then:
        userDetail.email == contractor.person.physicalPersonDetail.email
    }

    def 'given id, email and password when initializing then should set properties'() {
        given:
            String id = UUID.randomUUID().toString()
            String email = 'test@unovation.com.br'
            String password = RandomStringUtils.randomAlphabetic(10)
        when:
            UserDetail userDetail = new UserDetail(id, email, password)
        then:
            assert userDetail.id == id
            assert userDetail.email == email
            assert userDetail.password == password
    }

    def 'given an user when updating model then should update all non null properties'() {
        given:
            UserDetail userDetail = new UserDetail()
            UserDetail otherUser = new UserDetail(
                email: 'unovation@unovation.com.br',
                name: 'Test Name',
                type: new UserType(id: UUID.randomUUID().toString()),
            )
        when:
            userDetail.updateMe(otherUser)
        then:
            assert userDetail.email == otherUser.email
            assert userDetail.name == otherUser.name
            assert userDetail.type.id == otherUser.type.id
    }

    def 'given an user when updating model then should not update null properties'() {
        given:
            String userTypeId = UUID.randomUUID().toString()

            UserDetail userDetail = new UserDetail(
                email: 'unovation@unovation.com.br',
                name: 'Test Name',
                type: new UserType(id: userTypeId),
            )
            UserDetail otherUser = new UserDetail()
        when:
            userDetail.updateMe(otherUser)
        then:
            assert userDetail.email == 'unovation@unovation.com.br'
            assert userDetail.name == 'Test Name'
            assert userDetail.type.id == userTypeId
    }

    def 'given an user when adding null list to groups then should do nothing'() {
        given:
            String groupId = UUID.randomUUID().toString()
            UserDetail userDetail = new UserDetail(
                groups: [
                    new Group(
                        id: groupId
                    )
                ]
            )
        when:
            userDetail.addToMyGroups(null as List<Group>)
        then:
            assert userDetail.groups.size() == 1
            assert userDetail.groups.find().id == groupId
    }

    def 'should be equals'(){
        given:
        UserDetail a = Fixture.from(UserDetail.class).gimme("without-group")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        UserDetail a = Fixture.from(UserDetail.class).gimme("without-group")
        UserDetail b = Fixture.from(UserDetail.class).gimme("without-group")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals
    }
}
