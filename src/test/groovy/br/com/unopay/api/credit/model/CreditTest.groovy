package br.com.unopay.api.credit.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.market.model.NegotiationBilling
import br.com.unopay.api.model.ContractOrigin

class CreditTest  extends FixtureApplicationTest {

    def 'should be crated from negotiation billing'(){
        given:
        NegotiationBilling negotiation = Fixture.from(NegotiationBilling.class).gimme("valid")

        when:
        def credit = new Credit(negotiation)

        then:
        credit.value == negotiation.creditValue
        credit.hirer == negotiation.hirer()
        credit.product == negotiation.product()
        credit.creditSource == ContractOrigin.APPLICATION.name()
        credit.creditInsertionType == CreditInsertionType.BOLETO
        !credit.billable

    }

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
        a.hirer.id == b.hirer.id
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
        a.hirer != b.hirer
        a.serviceType != b.serviceType
        a.creditInsertionType != b.creditInsertionType
        a.creditNumber != b.creditNumber
        a.createdDateTime != b.createdDateTime
        a.value != b.value
        a.situation != b.situation
        a.creditSource != b.creditSource
        a.cnabId != b.cnabId
        a.blockedValue != b.blockedValue
    }

    def 'given a processing situation the available credit should be zero'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("allFields", new Rule(){{
            add("situation", CreditSituation.PROCESSING)
            add("creditInsertionType", CreditInsertionType.DIRECT_DEBIT)
        }})

        when:
        credit.defineAvailableValue()

        then:
        credit.availableValue == BigDecimal.ZERO
    }

    def 'given a confirmed situation the credit should be available'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("allFields", new Rule(){{
            add("situation", CreditSituation.CONFIRMED)
            add("creditInsertionType", CreditInsertionType.DIRECT_DEBIT)
        }})

        when:
        credit.defineAvailableValue()

        then:
        credit.availableValue == credit.value
    }

    def 'given a processing situation the credit should be blocked'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("allFields", new Rule(){{
            add("situation", CreditSituation.PROCESSING)
            add("creditInsertionType", CreditInsertionType.DIRECT_DEBIT)
        }})

        when:
        credit.defineBlockedValue()

        then:
        credit.blockedValue == credit.value
    }

    def 'given a confirmed situation the blocked credit should be zero'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("allFields", new Rule(){{
            add("situation", CreditSituation.CONFIRMED)
            add("creditInsertionType", CreditInsertionType.DIRECT_DEBIT)
        }})

        when:
        credit.defineBlockedValue()

        then:
        credit.blockedValue == BigDecimal.ZERO
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

    def 'if the credit has a credit'(){
        given:
        Credit a = Fixture.from(Credit.class).gimme("allFields")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

}
