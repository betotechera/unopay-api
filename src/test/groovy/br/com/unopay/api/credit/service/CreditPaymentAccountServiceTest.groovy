package br.com.unopay.api.credit.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.network.model.ServiceType
import static br.com.unopay.api.network.model.ServiceType.DOCTORS_APPOINTMENTS
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.credit.model.Credit
import br.com.unopay.api.credit.model.CreditInsertionType
import br.com.unopay.api.credit.model.CreditPaymentAccount
import br.com.unopay.bootcommons.exception.NotFoundException
import groovy.time.TimeCategory
import org.springframework.beans.factory.annotation.Autowired

class CreditPaymentAccountServiceTest extends SpockApplicationTests {

    @Autowired
    CreditPaymentAccountService service

    @Autowired
    FixtureCreator fixtureCreator

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
        timeComparator.compare(result.insertionCreatedDateTime, new Date()) == 0
    }

    void 'given a credit with existing service credit should update balance when insert without direct debit'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def knownProduct = fixtureCreator.createProductWithOutDirectDebit()
        BigDecimal value = 10.5
        List<Credit> credits = Fixture.from(Credit.class).uses(jpaProcessor).gimme(3, "allFields", new Rule() {{
            add("hirer", hirer)
            add("value", value)
            add("product", knownProduct)
            add("availableValue", value)
            add("creditInsertionType", CreditInsertionType.BOLETO)
            add("serviceType", ServiceType.MEDICINES)
        }})
        when:
        credits.each { service.register(it)}

        then:
        service.findAll().last().availableBalance == (value * 3)
    }

    void 'given a credit without same service credit should insert new credit when insert without direct debit'(){
        given:
        def hirer = fixtureCreator.createHirer()
        BigDecimal value = 10.5
        Credit credit = Fixture.from(Credit.class).uses(jpaProcessor).gimme("allFields", new Rule() {{
            add("hirer", hirer)
            add("value", value)
            add("availableValue", value)
            add("creditInsertionType", CreditInsertionType.BOLETO)
            add("serviceType", DOCTORS_APPOINTMENTS)
        }})
        List<Credit> credits = Fixture.from(Credit.class).uses(jpaProcessor).gimme(2, "allFields", new Rule() {{
            add("hirer", hirer)
            add("value", value)
            add("availableValue", value)
            add("creditInsertionType", CreditInsertionType.BOLETO)
            add("serviceType", DOCTORS_APPOINTMENTS)
        }})
        when:
        credits.each { service.register(it)}
        service.register(credit)

        then:
        service.findAll().every { it.availableBalance == credit.value}
    }

    void 'should update balance grouped by product and service'(){
        given:
        def paymentRuleGroupUnderTest = fixtureCreator.createPaymentRuleGroup()
        def knownProduct = fixtureCreator.createProductWithOutDirectDebit()
        def hirer = fixtureCreator.createHirer()
        BigDecimal value = 10.5
        Credit credit = Fixture.from(Credit.class).uses(jpaProcessor).gimme("allFields", new Rule() {{
            add("hirer", hirer)
            add("paymentRuleGroup", paymentRuleGroupUnderTest)
            add("value", value)
            add("product", null)
            add("availableValue", value)
            add("creditInsertionType", CreditInsertionType.BOLETO)
            add("serviceType", DOCTORS_APPOINTMENTS)
        }})
        List<Credit> credits = Fixture.from(Credit.class).uses(jpaProcessor).gimme(2, "allFields", new Rule() {{
            add("hirer", hirer)
            add("product", knownProduct)
            add("value", value)
            add("availableValue", value)
            add("creditInsertionType", CreditInsertionType.BOLETO)
            add("serviceType", DOCTORS_APPOINTMENTS)
        }})
        when:
        credits.each { service.register(it)}
        service.register(credit)

        then:
        service.findAll().find {
            it.serviceType == DOCTORS_APPOINTMENTS && it.product == null
        }?.availableBalance == credit.value
        service.findAll().find {
            it.serviceType == DOCTORS_APPOINTMENTS && it.product != null
        }?.availableBalance == (credit.value * 2)
    }

    void 'given a credit without service and product should update balance when insert without direct debit'(){
        given:
        fixtureCreator.createPaymentRuleGroupDefault()
        def hirer = fixtureCreator.createHirer()
        def knownProduct = fixtureCreator.createProductWithOutDirectDebit()
        BigDecimal value = 10.5
        Credit credit = Fixture.from(Credit.class).uses(jpaProcessor).gimme("allFields", new Rule() {{
            add("hirer", hirer)
            add("product", knownProduct)
            add("value", value)
            add("availableValue", value)
            add("creditInsertionType", CreditInsertionType.BOLETO)
            add("serviceType", DOCTORS_APPOINTMENTS)
        }})
        List<Credit> credits = Fixture.from(Credit.class).uses(jpaProcessor).gimme(2, "allFields", new Rule() {{
            add("hirer", hirer)
            add("product", null)
            add("value", value)
            add("availableValue", value)
            add("creditInsertionType", CreditInsertionType.BOLETO)
            add("serviceType", null)
        }})
        when:
        credits.each { service.register(it)}
        service.register(credit)

        then:
        service.findAll().find {
            it.serviceType == null && it.product == null
        }?.availableBalance == (value * 2)
        service.findAll().find {
            it.serviceType == DOCTORS_APPOINTMENTS && it.product != null
        }?.availableBalance == value
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
