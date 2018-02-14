package br.com.unopay.api.market.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.model.Contract

class NegotiationBillingDetailTest extends FixtureApplicationTest {

    def 'should be created from contract'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")

        when:
        def detail = new NegotiationBillingDetail(contract)

        then:
        detail.contract.id == contract.id
        detail.freeInstallment == Boolean.FALSE
        timeComparator.compare(detail.createdDateTime, new Date()) == 0
    }

    def 'should be defined from billing'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        NegotiationBilling billing = Fixture.from(NegotiationBilling.class).gimme("valid")
        def detail = new NegotiationBillingDetail(contract)

        when:
        detail.defineBillingInformation(billing)

        then:
        detail.creditValue == billing.defaultCreditValue
        detail.memberCreditValue == billing.defaultMemberCreditValue
        detail.installmentValue == billing.installmentValue
        detail.installmentValueByMember == billing.installmentValueByMember
        detail.negotiationBilling.id == billing.id
    }

    def 'given billing with free installment number should be defined from billing with free installment'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        NegotiationBilling billing = Fixture.from(NegotiationBilling.class).gimme("valid")
        billing.installmentNumber = 3
        billing.freeInstallmentQuantity = 3
        def detail = new NegotiationBillingDetail(contract)

        when:
        detail.defineBillingInformation(billing)

        then:
        detail.freeInstallment == Boolean.TRUE
    }

    def 'should define valid detail value'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        NegotiationBilling billing = Fixture.from(NegotiationBilling.class).gimme("valid")
        def detail = new NegotiationBillingDetail(contract)
        detail.defineBillingInformation(billing)
        detail.memberTotal = members
        billing.setDefaultMemberCreditValue(memberCreditValue as BigDecimal)
        billing.setInstallmentValue(installmentValue)
        billing.setInstallmentValueByMember(installmentValueByMember as BigDecimal)
        billing.setDefaultCreditValue(creditValue)
        billing.setBillingWithCredits(Boolean.TRUE)

        when:
        detail.defineBillingInformation(billing)

        then:
        detail.value == value as BigDecimal

        where:
        memberCreditValue | members | installmentValueByMember | installmentValue | creditValue | value
        5                 | 1       | 5                        | 4                | 2           | 16
        10                | 2       | 20                       | 3                | 5           | 68
        3                 | 8       | 24                       | 5                | 8           | 229
        5.3               | 4       | 21.2                     | 6                | 9           | 121

    }

    def 'given a free installment should define value without installment value'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        NegotiationBilling billing = Fixture.from(NegotiationBilling.class).gimme("valid")
        def detail = new NegotiationBillingDetail(contract)

        detail.memberTotal = members
        billing.setDefaultMemberCreditValue(memberCreditValue as BigDecimal)
        billing.setInstallmentValue(installmentValue)
        billing.setInstallmentValueByMember(installmentValueByMember as BigDecimal)
        billing.setDefaultCreditValue(creditValue)
        billing.setInstallmentNumber(8)
        billing.setFreeInstallmentQuantity(9)
        billing.setBillingWithCredits(Boolean.TRUE)

        when:
        detail.defineBillingInformation(billing)

        then:
        detail.value == value as BigDecimal

        where:
        memberCreditValue | members | installmentValueByMember | installmentValue | creditValue | value
        5                 | 1       | 5                        | 4                | 2           | 7
        10                | 2       | 20                       | 3                | 5           | 25
        3                 | 8       | 24                       | 5                | 8           | 32
        5.3               | 4       | 21.2                     | 6                | 9           | 30.2

    }


    def 'given a billing without credits flag should define value without credit value'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        NegotiationBilling billing = Fixture.from(NegotiationBilling.class).gimme("valid")
        def detail = new NegotiationBillingDetail(contract)

        detail.memberTotal = members
        billing.setDefaultMemberCreditValue(memberCreditValue as BigDecimal)
        billing.setInstallmentValue(installmentValue)
        billing.setInstallmentValueByMember(installmentValueByMember as BigDecimal)
        billing.setDefaultCreditValue(creditValue)
        billing.setBillingWithCredits(Boolean.FALSE)

        when:
        detail.defineBillingInformation(billing)

        then:
        detail.value == value as BigDecimal

        where:
        memberCreditValue | members | installmentValueByMember | installmentValue | creditValue | value
        5                 | 1       | 5                        | 4                | 2           | 9
        10                | 2       | 20                       | 3                | 5           | 43
        3                 | 8       | 24                       | 5                | 8           | 197
        5.3               | 4       | 21.2                     | 6                | 9           | 90.8

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
