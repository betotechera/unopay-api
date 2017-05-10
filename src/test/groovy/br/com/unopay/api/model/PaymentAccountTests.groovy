package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class PaymentAccountTests  extends FixtureApplicationTest {

    def 'should update Credit Payment Account'(){
        given:
        CreditPaymentAccount a = Fixture.from(CreditPaymentAccount.class).gimme("valid")
        CreditPaymentAccount b = Fixture.from(CreditPaymentAccount.class).gimme("valid")
        b.getIssuer().setId('65545')
        b.getProduct().setId('65545')
        b.getPaymentRuleGroup().setId('65545')

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

    def 'should create Credit Payment Account from credit'(){
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

    def 'should create Credit Payment Account from credit without product'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("allFields").with { product = null; it }

        when:
        CreditPaymentAccount paymentAccount = new CreditPaymentAccount(credit)

        then:
        paymentAccount.transactionCreatedDateTime != credit.createdDateTime
        paymentAccount.issuer == null
        paymentAccount.product == null
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

    def 'should not create Credit Payment Account from null credit'(){
        when:
        CreditPaymentAccount paymentAccount = new CreditPaymentAccount(null)

        then:
        paymentAccount.transactionCreatedDateTime == null
        paymentAccount.issuer == null
        paymentAccount.product == null
        paymentAccount.paymentRuleGroup == null
        paymentAccount.hirerDocument == null
        paymentAccount.serviceType == null
        paymentAccount.creditInsertionType == null
        paymentAccount.creditNumber == null
        paymentAccount.value == null
        paymentAccount.situation == null
        paymentAccount.creditSource == null
        paymentAccount.availableBalance == BigDecimal.ZERO
    }

    def 'should update my balance from credit'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("allFields")
        def creditPaymentAccount = new CreditPaymentAccount()

        when:
        creditPaymentAccount.updateMyBalance(credit)

        then:
        creditPaymentAccount.availableBalance == credit.availableValue
    }

    def 'should return error when try update without credit'(){
        given:
        def creditPaymentAccount = new CreditPaymentAccount()

        when:
        creditPaymentAccount.updateMyBalance(null)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find()?.logref == 'CREDIT_REQUIRED_WHEN_UPDATE_BALANCE'
    }

    def 'existing balance should be incremented'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("allFields")
        def creditPaymentAccount = new CreditPaymentAccount()

        when:
        creditPaymentAccount.updateMyBalance(credit)
        creditPaymentAccount.updateMyBalance(credit)

        then:
        creditPaymentAccount.availableBalance == (credit.availableValue * 2)
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
