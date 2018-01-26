package br.com.unopay.api.credit.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.config.Queues
import br.com.unopay.api.credit.model.Credit
import static br.com.unopay.api.credit.model.CreditInsertionType.BOLETO
import static br.com.unopay.api.credit.model.CreditInsertionType.CREDIT_CARD
import static br.com.unopay.api.credit.model.CreditInsertionType.DIRECT_DEBIT
import br.com.unopay.api.credit.model.CreditProcessed
import br.com.unopay.api.credit.model.CreditSituation
import static br.com.unopay.api.credit.model.CreditTarget.HIRER
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.model.Product
import br.com.unopay.api.util.Rounder
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import groovy.time.TimeCategory
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class CreditServiceTest extends SpockApplicationTests {

    @Autowired
    CreditService service

    @Autowired
    FixtureCreator fixtureCreator
    CreditPaymentAccountService paymentAccountServiceMock = Mock(CreditPaymentAccountService)
    Notifier notifierMock = Mock(Notifier)

    void setup(){
        service.creditPaymentAccountService = paymentAccountServiceMock
        service.notifier = notifierMock
        Integer.mixin(TimeCategory)
    }

    void 'given a invalid value the payment account should not be created'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([DIRECT_DEBIT])
        Credit credit = fixtureCreator.createCredit(knownProduct)
        service.insert(credit)
        def pair = new CreditProcessed(credit.getIssuer().documentNumber(), 0.0, DIRECT_DEBIT, HIRER)

        when:
        service.unblockCredit(pair)

        then:
        0 * paymentAccountServiceMock.register(_)
    }

    void 'when unlock direct debit credit the payment account should be created'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([DIRECT_DEBIT])
        Credit credit = fixtureCreator.createCredit(knownProduct)
        service.insert(credit)
        def pair = new CreditProcessed(credit.issuer.getId(), credit.value, DIRECT_DEBIT, HIRER)

        when:
        service.unblockCredit(pair)

        then:
        1 * paymentAccountServiceMock.register(_)
    }


    void 'when unlock direct debit credit the credit situation should be confirmed'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([DIRECT_DEBIT])
        Credit credit = fixtureCreator.createCredit(knownProduct)
        def inserted  = service.insert(credit)
        def pair = new CreditProcessed(credit.issuer.getId(), credit.value, DIRECT_DEBIT, HIRER)

        when:
        service.unblockCredit(pair)
        def result = service.findById(inserted.id)

        then:
        result.situation == CreditSituation.CONFIRMED
    }

    void 'when unlock direct debit credit the credit should be available'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([DIRECT_DEBIT])
        Credit credit = fixtureCreator.createCredit(knownProduct)
        credit.creditInsertionType = DIRECT_DEBIT
        def inserted  = service.insert(credit)
        def pair = new CreditProcessed(credit.issuer.getId(), credit.value, DIRECT_DEBIT, HIRER)

        when:
        service.unblockCredit(pair)
        def result = service.findById(inserted.id)

        then:
        result.availableValue == credit.value
    }

    void 'when unlock direct debit credit the blocked credit should be zero'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([DIRECT_DEBIT])
        Credit credit = fixtureCreator.createCredit(knownProduct)
        credit.creditInsertionType = DIRECT_DEBIT
        def inserted  = service.insert(credit)
        def pair = new CreditProcessed(credit.issuer.getId(), credit.value, DIRECT_DEBIT, HIRER)

        when:
        service.unblockCredit(pair)
        def result = service.findById(inserted.id)

        then:
        result.blockedValue == BigDecimal.ZERO
    }

    void 'credit with product should be inserted with product payment rule group'(){
        given:
        def knownProduct = fixtureCreator.createProduct()
        Credit credit = fixtureCreator.createCredit(knownProduct)

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
        result.getPaymentRuleGroup() == knownProduct.getPaymentRuleGroup()
    }

    void 'credit with unknown product should not be inserted'(){
        given:
        Product product = fixtureCreator.createProduct()
        Credit credit = fixtureCreator.createCredit(product.with { id = ''; it })

        when:
        service.insert(credit)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }

    void 'credit with unknown payment rule groups should  inserted with product paymentRuleGroup'(){
        given:
        PaymentRuleGroup unknownPaymentRuleGroup = fixtureCreator.createPaymentRuleGroup().with { id = ''; it }
        Credit credit = fixtureCreator.createCredit(null).with{  paymentRuleGroup = unknownPaymentRuleGroup; it }

        when:
        service.insert(credit)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_RULE_GROUP_NOT_FOUND'
    }

    void 'when insert credit should be generate credit number'() {
        given:
        def knownProduct = fixtureCreator.createProduct()
        Credit credit = fixtureCreator.createCredit(knownProduct)
                .with { creditNumber = null; it }

        when:
        def inserted = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.creditNumber != null
    }

    void 'when insert credit then generated credit number should be incremented'() {
        given:
        def knownProduct = fixtureCreator.createProduct()
        Credit credit = fixtureCreator.createCredit(knownProduct)
                .with { creditNumber = null; it }

        when:
        service.insert(credit)
        def inserted = service.insert(credit.with { id = null; it })

        then:
        assert inserted.creditNumber != null
        assert inserted.creditNumber == 2L
    }

    @Unroll
    'when insert credit with #type payment type then payment account should not be created'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([type])
        Credit credit = fixtureCreator.createCredit(knownProduct)

        when:
        service.insert(credit)

        then:
        0 * paymentAccountServiceMock.register(_)

        where:
        _ | type
        _ | DIRECT_DEBIT
        _ | BOLETO
        _ | CREDIT_CARD
    }

    @Unroll
    'when insert credit with #type payment type should notify credit created'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([type])
        Credit credit = fixtureCreator.createCredit(knownProduct)

        when:
        service.insert(credit)

        then:
        1 * notifierMock.notify(Queues.HIRER_CREDIT_CREATED, _)

        where:
        _ | type
        _ | BOLETO
        _ | CREDIT_CARD
    }

    def 'when insert credit with direct debit payment type should not notify credit created'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([DIRECT_DEBIT])
        Credit credit = fixtureCreator.createCredit(knownProduct)

        when:
        service.insert(credit)

        then:
        0 * notifierMock.notify(Queues.HIRER_CREDIT_CREATED, _)
    }

    void 'a credit should be inserted with now date time'(){
        given:
        def knownProduct = fixtureCreator.createProduct()
        def hirer = fixtureCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields", new Rule(){{
            add("hirer", hirer)
            add("product", knownProduct)
            add("issuer", knownProduct.getIssuer())
        }})

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        timeComparator.compare(result.createdDateTime, new Date()) == 0
    }

    @Unroll
    'given a credit with direct #type insertion type should be inserted with processing situation'(){
        given:
        def creditType = type
        def knownProduct = fixtureCreator.createProduct()
        def hirer = fixtureCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields", new Rule(){{
            add("hirer", hirer)
            add("product", knownProduct)
            add("situation", CreditSituation.CONFIRMED)
            add("creditInsertionType", creditType)
            add("issuer", knownProduct.getIssuer())
        }})

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
        result.getSituation() == CreditSituation.PROCESSING

        where:
        _ | type
        _ | DIRECT_DEBIT
        _ | BOLETO
        _ | CREDIT_CARD
    }

    @Unroll
    void 'when insert credits with #insertionType then the blocked balance should be updated'(){
        given:
        def knownProduct = fixtureCreator.createProduct().with { creditInsertionTypes = [insertionType]; it }
        Credit creditA =  fixtureCreator.createCredit(knownProduct)
        Credit creditB =  fixtureCreator.createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.blockedValue == Rounder.round(creditB.value)

        where:
        insertionType | _
        BOLETO        | _
        CREDIT_CARD   | _
        DIRECT_DEBIT   | _
    }

    @Unroll
    void 'when insert credits with #type then the available balance should be zero'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([type])
        Credit creditA =  fixtureCreator.createCredit(knownProduct)
        Credit creditB =  fixtureCreator.createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.availableValue == 0.0

        where:
        _ | type
        _ | DIRECT_DEBIT
        _ | BOLETO
        _ | CREDIT_CARD

    }

    void 'given more of one credit when insert credits the blocked balance should be updated'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([BOLETO])
        Credit creditA =  fixtureCreator.createCredit(knownProduct)
        Credit creditB = fixtureCreator.createCredit(knownProduct)
        Credit creditC =  fixtureCreator.createCredit(knownProduct)

        when:
        service.insert(creditA)
        service.insert(creditB)
        def inserted = service.insert(creditC)
        def result = service.findById(inserted.id)

        then:
        result.blockedValue == Rounder.round(creditC.value)
    }

    @Unroll
    void 'when insert credits with #type the block balance should be updated'(){
        given:
        def knownProduct = fixtureCreator.createProduct()
                .with { creditInsertionTypes = [type]; it }
        Credit creditA = fixtureCreator.createCredit(knownProduct)
        Credit creditB =  fixtureCreator.createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.blockedValue == Rounder.round(creditB.value)

        where:
        _ | type
        _ | DIRECT_DEBIT
        _ | BOLETO
        _ | CREDIT_CARD
    }

    @Unroll
    void 'when insert credits with #insertionType then the available balance should be zero'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([insertionType])
        Credit creditA =  fixtureCreator.createCredit(knownProduct)
        Credit creditB =  fixtureCreator.createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.availableValue == 0.0

        where:
        insertionType | _
        BOLETO        | _
        CREDIT_CARD   | _
        DIRECT_DEBIT  | _
    }

    void 'credit with product should be inserted with product credit insertion type'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([BOLETO])
        Credit credit =  fixtureCreator.createCredit(knownProduct)
        credit.creditInsertionType = CREDIT_CARD

        when:
        service.insert(credit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CREDIT_INSERTION_TYPE_NOT_IN_PRODUCT'

    }

    void 'should not be inserted when do not match payment rule group minimum value restriction'(){
        given:
        def paymentRuleGroup = fixtureCreator.createPaymentRuleGroup()
        def hirer = fixtureCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields", new Rule(){{
            add("hirer", hirer)
            add("paymentRuleGroup", paymentRuleGroup)
            add("value", paymentRuleGroup.minimumCreditInsertion - 1)
            add("product", null)
        }})

        when:
        service.insert(credit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'MINIMUM_PAYMENT_RULE_GROUP_VALUE_NOT_MET'
    }

    void 'should not be inserted when do not match payment rule group maximum value restriction'(){
        given:
        def paymentRuleGroup = fixtureCreator.createPaymentRuleGroup()
        def hirer = fixtureCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields", new Rule(){{
            add("hirer", hirer)
            add("paymentRuleGroup", paymentRuleGroup)
            add("product", null)
            add("value", paymentRuleGroup.maximumCreditInsertion+1)
        }})

        when:
        service.insert(credit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'MAXIMUM_PAYMENT_RULE_GROUP_VALUE_NOT_MET'
    }

    void 'given a credit without product should be inserted with payment rule group'(){
        given:
        def paymentRuleGroup = fixtureCreator.createPaymentRuleGroup()
        def hirer = fixtureCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withProduct", new Rule(){{
            add("hirer", hirer)
            add("paymentRuleGroup", paymentRuleGroup)
            add("issuer", fixtureCreator.createIssuer())
        }})

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
        result.getPaymentRuleGroup().code == paymentRuleGroup.code
        result.getPaymentRuleGroup().id == paymentRuleGroup.id
        result.getPaymentRuleGroup().name == paymentRuleGroup.name
        result.getPaymentRuleGroup().purpose == paymentRuleGroup.purpose
        result.getPaymentRuleGroup().scope == paymentRuleGroup.scope
    }

    void 'given a credit without product should not be inserted without payment rule group'(){
        given:
        def hirer = fixtureCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withProduct", new Rule(){{
            add("hirer", hirer)
            add("paymentRuleGroup", null)
            add("product", null)
        }})

        when:
        service.insert(credit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'PAYMENT_RULE_GROUP_REQUIRED'
    }

    void 'given a credit without product should not be inserted with unknown payment rule group'(){
        given:
        def hirer = fixtureCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withProduct", new Rule(){{
            add("hirer", hirer)
            add("paymentRuleGroup", new PaymentRuleGroup())
        }})

        when:
        service.insert(credit)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_RULE_GROUP_NOT_FOUND'
    }

    void 'given a credit with known hirer document should be inserted'(){
        given:
        Credit credit = createCredit()

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
    }

    void 'given a credit with unknown hirer document should not be inserted'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("withProduct", new Rule(){{
            add("product", fixtureCreator.createProduct())
        }})

        when:
        service.insert(credit)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'HIRER_NOT_FOUND'
    }

    void 'given a credit without payment rule group and product should be inserted'(){
        given:
        def paymentRuleGroup = fixtureCreator.createPaymentRuleGroup()
        def hirer = fixtureCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withoutProductAndPaymentRuleGroup", new Rule(){{
            add("hirer", hirer)
            add("paymentRuleGroup", paymentRuleGroup)
            add("issuer", fixtureCreator.createIssuer())
        }})

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
    }

    void 'given a credit with payment rule group without maximum credit value should be inserted'(){
        given:
        def paymentRuleGroup = Fixture.from(PaymentRuleGroup.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("maximumCreditInsertion", null)
        }})
        def hirer = fixtureCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withoutProductAndPaymentRuleGroup", new Rule(){{
            add("hirer", hirer)
            add("paymentRuleGroup", paymentRuleGroup)
            add("issuer", fixtureCreator.createIssuer())
        }})

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
    }

    void 'given a credit with payment rule group without minimum credit value should be inserted'(){
        given:
        def paymentRuleGroup = Fixture.from(PaymentRuleGroup.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("minimumCreditInsertion", null)
        }})
        def hirer = fixtureCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withoutProductAndPaymentRuleGroup", new Rule(){{
            add("hirer", hirer)
            add("paymentRuleGroup", paymentRuleGroup)
            add("issuer", fixtureCreator.createIssuer())
        }})

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
    }

    void 'given known credit should be canceled'(){
        given:
        Credit credit = createCredit()
        def inserted  = service.insert(credit)

        when:
        service.cancel(inserted.id)
        def result = service.findById(inserted.id)

        then:
        assert result.situation == CreditSituation.CANCELED
    }

    void 'when cancel credit should subtract value of credit payment account'(){
        given:
        Credit credit = createCredit()
        def inserted  = service.insert(credit)

        when:
        service.cancel(inserted.id)

        then:
        1 * paymentAccountServiceMock.subtract(_)
    }

    void 'when cancel credit already canceled should return error'(){
        given:
        Credit credit = createCredit()
        def inserted  = service.insert(credit)
        service.cancel(inserted.id)

        when:
        service.cancel(inserted.id)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CREDIT_ALREADY_CANCELED'
    }

    void 'unknown credit should not be canceled'(){
        when:
        service.cancel('')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'HIRER_CREDIT_NOT_FOUND'
    }

    private Credit createCredit() {
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        Fixture.from(Credit.class).gimme("withProduct", new Rule(){{
            add("product", product)
            add("hirer",hirer)
            add("issuer", product.getIssuer())
        }})
    }

}
