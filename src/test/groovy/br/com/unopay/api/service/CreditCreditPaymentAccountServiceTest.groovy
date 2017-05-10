package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.Credit
import br.com.unopay.api.model.CreditInsertionType
import br.com.unopay.api.model.CreditPaymentAccount
import br.com.unopay.bootcommons.exception.NotFoundException
import groovy.time.TimeCategory
import org.springframework.beans.factory.annotation.Autowired

import static br.com.unopay.api.bacen.model.ServiceType.*

class CreditCreditPaymentAccountServiceTest extends SpockApplicationTests {

    @Autowired
    CreditPaymentAccountService service

    @Autowired
    SetupCreator setupCreator

    @Autowired
    CreditService creditService

    void setup(){
        Integer.mixin(TimeCategory)
    }

    void 'given a  payment account should be created'(){
        given:
        CreditPaymentAccount paymentAccount = createPaymentAccount()

        when:
        def created  = service.save(paymentAccount)
        def result = service.findById(created.id)

        then:
        assert result.id != null
    }

    void 'payment account with unknown payment rule group should not be created'(){
        given:
        CreditPaymentAccount paymentAccount = createPaymentAccount().with { paymentRuleGroup.id = ''; it }

        when:
        service.save(paymentAccount)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_RULE_GROUP_NOT_FOUND'
    }

    void 'payment account with unknown product should not be created'(){
        given:
        CreditPaymentAccount paymentAccount = createPaymentAccount().with { product.id = ''; it }

        when:
        service.save(paymentAccount)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }


    void 'payment account with unknown issuer should not be created'(){
        given:
        CreditPaymentAccount paymentAccount = createPaymentAccount().with { product.issuer.id = ''; it }

        when:
        service.save(paymentAccount)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ISSUER_NOT_FOUND'
    }

    void 'new payment account should be created from credit'(){
        given:
        Credit create = setupCreator.createCredit()
                                    .with { paymentRuleGroup = setupCreator.createPaymentRuleGroup(); it}

        when:
        def created  = service.register(create)
        def result = service.findById(created.id)

        then:
        assert result.id != null
    }

    void 'payment account without product should be created'(){
        given:
        Credit create = setupCreator.createCredit(null)
                .with { paymentRuleGroup = setupCreator.createPaymentRuleGroup(); it}

        when:
        def created  = service.register(create)
        def result = service.findById(created.id)

        then:
        assert result.id != null
    }

    void 'payment account should be created with date time now'(){
        given:
        CreditPaymentAccount paymentAccount = createPaymentAccount()

        when:
        def created  = service.save(paymentAccount)
        def result = service.findById(created.id)

        then:
        result.insertionCreatedDateTime > 1.second.ago
        result.insertionCreatedDateTime < 1.second.from.now
    }

    void 'given a credit with existing service credit should update balance when insert without direct debit'(){
        given:
        def knownProduct = setupCreator.createProductWithOutDirectDebit()
        Credit credit = setupCreator.createCredit(knownProduct)
                .with {
            creditInsertionType = CreditInsertionType.PAMCARD_SYSTEM
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
        def knownProduct = setupCreator.createProductWithOutDirectDebit()
        Credit credit = setupCreator.createCredit(knownProduct)
                .with {
            creditInsertionType = CreditInsertionType.PAMCARD_SYSTEM
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
        setupCreator.createPaymentRuleGroupDefault()
        def knownProduct = setupCreator.createProductWithOutDirectDebit()
        Credit credit = setupCreator.createCredit(null)
                .with {
                        creditInsertionType = CreditInsertionType.PAMCARD_SYSTEM
                        serviceType = ELECTRONIC_TOLL
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
        setupCreator.createPaymentRuleGroupDefault()
        def knownProduct = setupCreator.createProductWithOutDirectDebit()
        Credit credit = setupCreator.createCredit(knownProduct)
                .with {
            creditInsertionType = CreditInsertionType.PAMCARD_SYSTEM
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

    private CreditPaymentAccount createPaymentAccount() {
        return Fixture.from(CreditPaymentAccount.class).gimme("valid")
                .with {
            product = setupCreator.createProduct()
            issuer = setupCreator.createIssuer()
            paymentRuleGroup = setupCreator.createPaymentRuleGroup()
            it
        }
    }
}
