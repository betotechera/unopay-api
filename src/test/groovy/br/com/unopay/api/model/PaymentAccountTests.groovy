package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class PaymentAccountTests  extends FixtureApplicationTest {

    def 'should update me'(){
        given:
        CreditPaymentAccount a = Fixture.from(CreditPaymentAccount.class).gimme("valid")
        CreditPaymentAccount b = Fixture.from(CreditPaymentAccount.class).gimme("valid")
        b.getIssuer().setId('65545')
        b.getProduct().setId('65545')
        b.getPaymentRuleGroup().setId('65545')
        b.getPaymentAccount().setId('65545')

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
        a.availableBalance == b.availableBalance
        a.paymentAccount == b.paymentAccount
    }


    def 'only fields with value should be updated'(){
        given:
        CreditPaymentAccount a = Fixture.from(CreditPaymentAccount.class).gimme("valid")
        CreditPaymentAccount b = new CreditPaymentAccount()

        when:
        a.updateMe(b)

        then:
        a.transactionCreatedDateTime != b.transactionCreatedDateTime
        a.issuer != b.issuer
        a.product != b.product
        a.paymentRuleGroup != b.paymentRuleGroup
        a.hirerDocument != b.hirerDocument
        a.serviceType != b.serviceType
        a.creditInsertionType != b.creditInsertionType
        a.solicitationDateTime != b.solicitationDateTime
        a.creditNumber != b.creditNumber
        a.insertionCreatedDateTime != b.insertionCreatedDateTime
        a.value != b.value
        a.situation != b.situation
        a.creditSource != b.creditSource
        a.availableBalance != b.availableBalance
        a.paymentAccount != b.paymentAccount
    }

    def 'should create me from credit'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("allFields")

        when:
        CreditPaymentAccount paymentAccount = new CreditPaymentAccount(credit)

        then:
        paymentAccount.transactionCreatedDateTime != credit.createdDateTime
        paymentAccount.issuer == credit.product.issuer
        paymentAccount.product == credit.product
        paymentAccount.paymentRuleGroup == credit.paymentRuleGroup
        paymentAccount.hirerDocument == credit.hirerDocument
        paymentAccount.serviceType == credit.serviceType
        paymentAccount.creditInsertionType == credit.creditInsertionType
        paymentAccount.creditNumber == credit.creditNumber
        paymentAccount.value == credit.value
        paymentAccount.situation == credit.situation
        paymentAccount.creditSource == credit.creditSource
        paymentAccount.availableBalance == credit.availableValue
    }

    def 'should be equals'(){
        given:
        CreditPaymentAccount a = Fixture.from(CreditPaymentAccount.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(CreditPaymentAccount.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}
