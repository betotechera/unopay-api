package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class CreditTest  extends FixtureApplicationTest {

    def 'should update me'(){
        given:
        Credit a = Fixture.from(Credit.class).gimme("allFields")
        Credit b = Fixture.from(Credit.class).gimme("allFields")
        b.getProduct().setId('65545')
        b.getPaymentRuleGroup().setId('65545')

        when:
        a.updateMe(b)

        then:
        a.product == b.product
        a.paymentRuleGroup == b.paymentRuleGroup
        a.hirerDocument == b.hirerDocument
        a.serviceType == b.serviceType
        a.creditInsertionType == b.creditInsertionType
        a.creditNumber == b.creditNumber
        a.createdDateTime == b.createdDateTime
        a.value == b.value
        a.situation == b.situation
        a.creditSource == b.creditSource
        a.cnabId == b.cnabId
        a.availableValue == b.availableValue
        a.blockedValue == b.blockedValue
    }

    def 'only fields with value should be updated'(){
        given:
        Credit a = Fixture.from(Credit.class).gimme("allFields")
        Credit b = new Credit()

        when:
        a.updateMe(b)

        then:
        a.product != b.product
        a.paymentRuleGroup != b.paymentRuleGroup
        a.hirerDocument != b.hirerDocument
        a.serviceType != b.serviceType
        a.creditInsertionType != b.creditInsertionType
        a.creditNumber != b.creditNumber
        a.createdDateTime != b.createdDateTime
        a.value != b.value
        a.situation != b.situation
        a.creditSource != b.creditSource
        a.cnabId != b.cnabId
        a.availableValue != b.availableValue
        a.blockedValue != b.blockedValue
    }

    def 'should be equals'(){
        given:
        Credit a = Fixture.from(Credit.class).gimme("allFields")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(Credit.class).gimme(2,"allFields")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}
