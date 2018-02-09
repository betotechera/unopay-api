package br.com.unopay.api.market.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.model.Contract

class NegotiationBillingDetailTest extends FixtureApplicationTest {

    def 'should be created from contract'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        NegotiationBilling billing = Fixture.from(NegotiationBilling.class).gimme("valid")

        when:
        def detail = new NegotiationBillingDetail(contract, billing)

        then:
        detail.contract.id == contract.id
        detail.creditValue == billing.defaultCreditValue
        detail.memberCreditValue == billing.defaultMemberCreditValue
        detail.installmentValue == billing.installmentValue
        detail.installmentValueByMember == billing.installmentValueByMember
        detail.negotiationBilling.id == billing.id
        detail.freeInstallment == Boolean.FALSE
        timeComparator.compare(detail.createdDateTime, new Date()) == 0
    }

    def 'should define valid detail value'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        NegotiationBilling billing = Fixture.from(NegotiationBilling.class).gimme("valid")
        def detail = new NegotiationBillingDetail(contract, billing)
        detail.memberTotal = members
        detail.setMemberCreditValue(memberCreditValue as BigDecimal)
        detail.setInstallmentValue(installmentValue)
        detail.setInstallmentValueByMember(installmentValueByMember as BigDecimal)
        detail.setCreditValue(creditValue)

        when:
        detail.defineValue()

        then:
        detail.value == value as BigDecimal

        where:
        memberCreditValue | members | installmentValueByMember | installmentValue | creditValue | value
        5                 | 1       | 5                        | 4                | 2           | 16
        10                | 2       | 20                       | 3                | 5           | 68
        3                 | 8       | 24                       | 5                | 8           | 229
        5.3               | 4       | 21.2                     | 6                | 9           | 121

    }

    def 'given a free installment should define valid without installment value'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        NegotiationBilling billing = Fixture.from(NegotiationBilling.class).gimme("valid")
        def detail = new NegotiationBillingDetail(contract, billing)
        detail.memberTotal = members
        detail.setMemberCreditValue(memberCreditValue as BigDecimal)
        detail.setInstallmentValue(installmentValue)
        detail.setInstallmentValueByMember(installmentValueByMember as BigDecimal)
        detail.setCreditValue(creditValue)
        detail.setFreeInstallment(true)

        when:
        detail.defineValue()

        then:
        detail.value == value as BigDecimal

        where:
        memberCreditValue | members | installmentValueByMember | installmentValue | creditValue | value
        5                 | 1       | 5                        | 4                | 2           | 7
        10                | 2       | 20                       | 3                | 5           | 25
        3                 | 8       | 24                       | 5                | 8           | 32
        5.3               | 4       | 21.2                     | 6                | 9           | 30.2

    }

    def 'should be equals'(){
        given:
        NegotiationBillingDetail a = Fixture.from(NegotiationBillingDetail.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(NegotiationBillingDetail.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}
