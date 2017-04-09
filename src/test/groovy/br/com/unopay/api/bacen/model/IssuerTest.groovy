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
        issuerA.tax == issuerB.tax
        issuerA.paymentAccount == issuerB.paymentAccount
        issuerA.movementAccount == issuerB.movementAccount
        issuerA.paymentRuleGroupIds == issuerB.paymentRuleGroupIds
    }
}
