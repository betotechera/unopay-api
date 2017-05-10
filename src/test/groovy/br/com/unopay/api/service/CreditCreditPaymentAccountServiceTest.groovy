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

    void 'given a credit with existing service credit should update balance when insert credit without direct debit'(){
        given:
        Credit credit = setupCreator.createCredit()
                .with {
            creditInsertionType = CreditInsertionType.PAMCARD_SYSTEM
            serviceType = FREIGHT
            it }
        when:
        creditService.insert(credit)
        creditService.insert(credit.with { id = null; it })
        creditService.insert(credit.with { id = null; it })

        then:
        then:
        service.findAll().any { it.serviceType == FREIGHT && it.availableBalance == (credit.value * 3)}
    }

    void 'given a credit without service credit should insert new credit when insert credit without direct debit'(){
        given:
        Credit credit = setupCreator.createCredit()
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
