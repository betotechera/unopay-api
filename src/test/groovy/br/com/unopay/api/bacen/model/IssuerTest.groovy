package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class IssuerTest extends FixtureApplicationTest{

    def 'should update all fields'(){
        given:
        Issuer issuerA = Fixture.from(Issuer.class).gimme("valid")
        Issuer issuerB = Fixture.from(Issuer.class).gimme("valid")

        when:
        issuerA.updateMe(issuerB)

        then:
        issuerA.fee == issuerB.fee
        issuerA.paymentAccount == issuerB.paymentAccount
        issuerA.movementAccount == issuerB.movementAccount
        issuerA.paymentRuleGroupIds == issuerB.paymentRuleGroupIds
    }

    def 'should be equals'(){
        given:
        Issuer a = Fixture.from(Issuer.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        Issuer a = Fixture.from(Issuer.class).gimme("valid")
        Issuer b = Fixture.from(Issuer.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}
