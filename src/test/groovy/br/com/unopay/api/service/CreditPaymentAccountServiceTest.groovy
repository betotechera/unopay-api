package br.com.unopay.api.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import static br.com.unopay.api.bacen.model.ServiceType.ELECTRONIC_TOLL
import static br.com.unopay.api.bacen.model.ServiceType.FREIGHT
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.Credit
import br.com.unopay.api.model.CreditInsertionType
import br.com.unopay.api.model.CreditPaymentAccount
import br.com.unopay.bootcommons.exception.NotFoundException
import groovy.time.TimeCategory
import org.springframework.beans.factory.annotation.Autowired

class CreditPaymentAccountServiceTest extends SpockApplicationTests {

    @Autowired
    CreditPaymentAccountService service

    @Autowired
    FixtureCreator fixtureCreator

    @Autowired
    CreditService creditService

    void setup(){
        Integer.mixin(TimeCategory)
    }

    void 'given a  payment account should be created'(){
        given:
        CreditPaymentAccount paymentAccount = fixtureCreator.createCreditPaymentAccount()

        when:
        def created  = service.save(paymentAccount)
        def result = service.findById(created.id)

        then:
        assert result.id != null
    }

    void 'payment account with unknown payment rule group should not be created'(){
        given:
        CreditPaymentAccount paymentAccount = fixtureCreator.createCreditPaymentAccount()
                                                .with { paymentRuleGroup.id = ''; it }

        when:
        service.save(paymentAccount)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_RULE_GROUP_NOT_FOUND'
    }

    void 'payment account with unknown product should not be created'(){
        given:
        CreditPaymentAccount paymentAccount = fixtureCreator.createCreditPaymentAccount().with { product.id = ''; it }

        when:
        service.save(paymentAccount)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }


    void 'payment account with unknown issuer should not be created'(){
        given:
        CreditPaymentAccount paymentAccount = fixtureCreator.createCreditPaymentAccount()
                                                    .with { product.issuer.id = ''; it }

        when:
        service.save(paymentAccount)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ISSUER_NOT_FOUND'
    }

    void 'new payment account should be created from credit'(){
        given:
        Credit create = fixtureCreator.createCredit()
                                    .with { paymentRuleGroup = fixtureCreator.createPaymentRuleGroup(); it}

        when:
        def created  = service.register(create)
        def result = service.findById(created.id)

        then:
        assert result.id != null
    }

    void 'payment account without product should be created'(){
        given:
        Credit create = fixtureCreator.createCredit(null)
                .with { paymentRuleGroup = fixtureCreator.createPaymentRuleGroup(); it}

        when:
        def created  = service.register(create)
        def result = service.findById(created.id)

        then:
        assert result.id != null
    }

    void 'payment account should be created with date time now'(){
        given:
        CreditPaymentAccount paymentAccount = fixtureCreator.createCreditPaymentAccount()

        when:
        def created  = service.save(paymentAccount)
        def result = service.findById(created.id)

        then:
        result.insertionCreatedDateTime > 1.second.ago
        result.insertionCreatedDateTime < 1.second.from.now
    }

    void 'given a credit with existing service credit should update balance when insert without direct debit'(){
        given:
        def knownProduct = fixtureCreator.createProductWithOutDirectDebit()
        Credit credit = fixtureCreator.createCredit(knownProduct)
                .with {
            creditInsertionType = CreditInsertionType.BOLETO
            serviceType = FREIGHT
            it }
        when:
        creditService.insert(credit)
        creditService.insert(credit.with { id = null; it })
        creditService.insert(credit.with { id = null; it })

        then:
        service.findAll().last().availableBalance == (credit.value * 3)
    }

    void 'given a credit without same service credit should insert new credit when insert without direct debit'(){
        given:
        def knownProduct = fixtureCreator.createProductWithOutDirectDebit()
        Credit credit = fixtureCreator.createCredit(knownProduct)
                .with {
            creditInsertionType = CreditInsertionType.BOLETO
            serviceType = FREIGHT
            it }
        when:
        creditService.insert(credit)
        creditService.insert(credit.with { id = null; serviceType = ELECTRONIC_TOLL; it })

        then:
        service.findAll().every { it.availableBalance == credit.value}
    }

    void 'should update balance grouped by product and service'(){
        given:
        def paymentRuleGroupUnderTest = fixtureCreator.createPaymentRuleGroup()
        def knownProduct = fixtureCreator.createProductWithOutDirectDebit()
        Credit credit = fixtureCreator.createCredit(null)
                .with {
                        creditInsertionType = CreditInsertionType.BOLETO
                        serviceType = ELECTRONIC_TOLL
                        paymentRuleGroup = paymentRuleGroupUnderTest
                it }
        when:
        creditService.insert(credit)
        creditService.insert(credit.with { id = null; serviceType = ELECTRONIC_TOLL; product = knownProduct; it })
        creditService.insert(credit.with { id = null; serviceType = ELECTRONIC_TOLL; product = knownProduct; it })

        then:
        service.findAll().find {
            it.serviceType == ELECTRONIC_TOLL && it.product == null
        }?.availableBalance == credit.value
        service.findAll().find {
            it.serviceType == ELECTRONIC_TOLL && it.product != null
        }?.availableBalance == (credit.value * 2)
    }

    void 'given a credit without service and product should update balance when insert without direct debit'(){
        given:
        fixtureCreator.createPaymentRuleGroupDefault()
        def knownProduct = fixtureCreator.createProductWithOutDirectDebit()
        Credit credit = fixtureCreator.createCredit(knownProduct)
                .with {
            creditInsertionType = CreditInsertionType.BOLETO
            serviceType = ELECTRONIC_TOLL
            it }
        when:
        creditService.insert(credit)
        creditService.insert(credit.with { id = null; serviceType = null; product = null; it })
        creditService.insert(credit.with { id = null; serviceType = null; product = null; it })

        then:
        service.findAll().find {
            it.serviceType == null && it.product == null
        }?.availableBalance == (credit.value * 2)
        service.findAll().find {
            it.serviceType == ELECTRONIC_TOLL && it.product != null
        }?.availableBalance == credit.value
    }

    void 'given a existing payment account with balance when subtract should be subtract credit'(){
        given:
        Credit credit = fixtureCreator.createCredit()
                .with { paymentRuleGroup = fixtureCreator.createPaymentRuleGroup(); it}
        service.register(credit)
        def created  = service.register(credit)
        when:
        service.subtract(credit)
        def result = service.findById(created.id)

        then:
        assert result.availableBalance == credit.availableValue
    }

    void 'given a existing payment account when subtracted with id should be subtracted'(){
        given:
        Credit credit = fixtureCreator.createCredit()
                .with { paymentRuleGroup = fixtureCreator.createPaymentRuleGroup(); it}
        service.register(credit)
        def created  = service.register(credit)
        when:
        service.subtract(created.id, credit.availableValue)
        def result = service.findById(created.id)

        then:
        assert result.availableBalance == credit.availableValue
    }

    void 'when subtract unknown payment account should not ben subtracted'(){

        when:
        service.subtract('', BigDecimal.ONE)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CREDIT_PAYMENT_ACCOUNT_NOT_FOUND'
    }

    void 'given a existing payment account when increment should be incremented'(){
        given:
        Credit credit = fixtureCreator.createCredit()
                .with { paymentRuleGroup = fixtureCreator.createPaymentRuleGroup(); it}
        def created  = service.register(credit)
        when:
        service.giveBack(created.id, credit.availableValue)
        def result = service.findById(created.id)

        then:
        assert result.availableBalance == credit.availableValue * 2
    }

    void 'when increment unknown payment account should not ben incremented'(){

        when:
        service.giveBack('', BigDecimal.ONE)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CREDIT_PAYMENT_ACCOUNT_NOT_FOUND'
    }


}
