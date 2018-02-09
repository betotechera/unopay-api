package br.com.unopay.api.market.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.order.model.PaymentStatus

class NegotiationBillingTest extends FixtureApplicationTest {

    def 'should be created from hirer negotiation'(){
        given:
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation.class).gimme("valid")
        def installmentNumber = 30
        when:
        def billing = new NegotiationBilling(negotiation, installmentNumber)

        then:
        billing.billingWithCredits == negotiation.billingWithCredits
        billing.defaultCreditValue == negotiation.defaultCreditValue
        billing.defaultMemberCreditValue == negotiation.defaultMemberCreditValue
        billing.freeInstallmentQuantity == negotiation.freeInstallmentQuantity
        billing.installmentValueByMember == negotiation.installmentValueByMember
        billing.installmentValue == negotiation.installmentValue
        billing.installments == negotiation.installments
        billing.hirerNegotiation.id == negotiation.id
        billing.installmentNumber == installmentNumber
        billing.status == PaymentStatus.WAITING_PAYMENT
        billing.billingWithCredits == negotiation.billingWithCredits
        timeComparator.compare(billing.createdDateTime, new Date()) == 0
    }

    def """given billing with installment number less than negotiation free installments
            should be created from hirer negotiation"""(){
        given:
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation.class).gimme("valid")
        negotiation.freeInstallmentQuantity = 5
        def installmentNumber = 5
        def billing = new NegotiationBilling(negotiation, installmentNumber)

        when:
        def withFreeInstallment = billing.withFreeInstallment()

        then:
        withFreeInstallment

    }

    def 'should be equals'(){
        given:
        NegotiationBilling a = Fixture.from(NegotiationBilling.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(NegotiationBilling.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}
