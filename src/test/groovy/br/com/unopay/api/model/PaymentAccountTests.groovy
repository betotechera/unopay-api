package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class PaymentAccountTests  extends FixtureApplicationTest {

    def 'should update me'(){
        given:
        PaymentAccount a = Fixture.from(PaymentAccount.class).gimme("valid")
        PaymentAccount b = Fixture.from(PaymentAccount.class).gimme("valid")
        b.getIssuer().setId('65545')
        b.getProduct().setId('65545')
        b.getPaymentRuleGroup().setId('65545')
        b.getPaymentBankAccount().setId('65545')

        when:
        a.updateMe(b)

        then:
        a.transactionCreatedDateTime == b.transactionCreatedDateTime
        a.issuer == b.issuer
        a.product == b.product
        a.paymentRuleGroup == b.paymentRuleGroup
        a.hirerDocument == b.hirerDocument
        a.serviceType == b.serviceType
        a.creditInsertionType == b.creditInsertionType
        a.solicitationDateTime == b.solicitationDateTime
        a.creditNumber == b.creditNumber
        a.insertionCreatedDateTime == b.insertionCreatedDateTime
        a.value == b.value
        a.situation == b.situation
        a.creditSource == b.creditSource
        a.cnabId == b.cnabId
        a.availableBalance == b.availableBalance
        a.paymentBankAccount == b.paymentBankAccount
    }

    def 'should be equals'(){
        given:
        PaymentAccount a = Fixture.from(PaymentAccount.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(PaymentAccount.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}
