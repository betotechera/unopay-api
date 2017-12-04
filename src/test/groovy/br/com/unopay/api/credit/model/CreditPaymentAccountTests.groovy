package br.com.unopay.api.credit.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.order.model.Order
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class CreditPaymentAccountTests extends FixtureApplicationTest {

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
        Credit credit = Fixture.from(Credit.class).gimme("allFields", new Rule() {{
            add("product", null)
        }})

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


    def 'should create Credit Payment Account from order'(){
        given:
        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        CreditPaymentAccount paymentAccount = new CreditPaymentAccount(order)

        then:
        paymentAccount.transactionCreatedDateTime != order.createDateTime
        paymentAccount.issuer == order.getProduct().getIssuer()
        paymentAccount.product == order.getProduct()
        paymentAccount.paymentRuleGroup == order.getProduct().getPaymentRuleGroup()
        paymentAccount.hirerDocument == order.getContract().getHirer().documentNumber
        paymentAccount.creditInsertionType == CreditInsertionType.DIRECT_DEBIT
        paymentAccount.creditNumber != null
        paymentAccount.value == order.value
        paymentAccount.situation == CreditSituation.AVAILABLE
        paymentAccount.creditSource == InstrumentCreditSource.CLIENT.name()
        paymentAccount.availableBalance == order.value
    }

    def 'should not create Credit Payment Account from null credit'(){
        when:
        CreditPaymentAccount paymentAccount = new CreditPaymentAccount()

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

    def 'existing balance should be subtracted'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("allFields")
        def creditPaymentAccount = new CreditPaymentAccount()
        creditPaymentAccount.updateMyBalance(credit)
        creditPaymentAccount.updateMyBalance(credit)

        when:
        creditPaymentAccount.subtract(credit)

        then:
        creditPaymentAccount.availableBalance == credit.availableValue
    }

    def 'should return error when try subtract without credit'(){
        given:
        def creditPaymentAccount = new CreditPaymentAccount()

        when:
        creditPaymentAccount.subtract(null)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find()?.logref == 'CREDIT_REQUIRED_WHEN_SUBTRACT_BALANCE'
    }

    def 'given existing balance when try subtract value greater than available balance should not be subtracted'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("allFields")
        def creditPaymentAccount = new CreditPaymentAccount()
        creditPaymentAccount.updateMyBalance(credit)
        def balance = credit.availableValue + 0.01

        when:
        creditPaymentAccount.subtract(credit.with { availableValue = balance; it })

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find()?.logref == 'VALUE_GREATER_THEN_AVAILABLE_BALANCE'
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
