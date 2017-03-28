package br.com.unopay.api.uaa.model

import br.com.unopay.api.bacen.model.PaymentRuleGroup
import org.apache.commons.lang3.RandomStringUtils
import spock.lang.Specification

class UserDetailTest extends Specification {
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
                paymentRuleGroup: new PaymentRuleGroup(id: UUID.randomUUID().toString())
            )
        when:
            userDetail.updateModel(otherUser)
        then:
            assert userDetail.email == otherUser.email
            assert userDetail.name == otherUser.name
            assert userDetail.type.id == otherUser.type.id
            assert userDetail.paymentRuleGroup.id == otherUser.paymentRuleGroup.id
    }

    def 'given an user when updating model then should not update null properties'() {
        given:
            String userTypeId = UUID.randomUUID().toString()
            String paymentRuleGroupId = UUID.randomUUID().toString()

            UserDetail userDetail = new UserDetail(
                email: 'unovation@unovation.com.br',
                name: 'Test Name',
                type: new UserType(id: userTypeId),
                paymentRuleGroup: new PaymentRuleGroup(id: paymentRuleGroupId)
            )
            UserDetail otherUser = new UserDetail()
        when:
            userDetail.updateModel(otherUser)
        then:
            assert userDetail.email == 'unovation@unovation.com.br'
            assert userDetail.name == 'Test Name'
            assert userDetail.type.id == userTypeId
            assert userDetail.paymentRuleGroup.id == paymentRuleGroupId
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
}
