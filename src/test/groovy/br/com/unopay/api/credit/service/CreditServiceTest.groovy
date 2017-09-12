package br.com.unopay.api.credit.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.credit.model.Credit
import static br.com.unopay.api.credit.model.CreditInsertionType.BOLETO
import static br.com.unopay.api.credit.model.CreditInsertionType.CREDIT_CARD
import static br.com.unopay.api.credit.model.CreditInsertionType.DIRECT_DEBIT
import br.com.unopay.api.credit.model.CreditProcessed
import br.com.unopay.api.credit.model.CreditSituation
import static br.com.unopay.api.credit.model.CreditTarget.HIRER
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

    void setup(){
        service.creditPaymentAccountService = paymentAccountServiceMock
        Integer.mixin(TimeCategory)
    }

    void 'given a invalid value the payment account should not be created'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([DIRECT_DEBIT])
        Credit credit = fixtureCreator.createCredit(knownProduct)
        service.insert(credit)
        def pair = new CreditProcessed(credit.issuerDocument, 0.0, DIRECT_DEBIT, HIRER)

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
        def pair = new CreditProcessed(credit.issuerDocument, credit.value, DIRECT_DEBIT, HIRER)

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
        def pair = new CreditProcessed(credit.issuerDocument, credit.value, DIRECT_DEBIT, HIRER)

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
        def inserted  = service.insert(credit)
        def pair = new CreditProcessed(credit.issuerDocument, credit.value, DIRECT_DEBIT, HIRER)

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
        def inserted  = service.insert(credit)
        def pair = new CreditProcessed(credit.issuerDocument, credit.value, DIRECT_DEBIT, HIRER)

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

    void 'when insert credit without direct debit payment type then payment account should be created'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([BOLETO])
        Credit credit = fixtureCreator.createCredit(knownProduct)

        when:
        service.insert(credit)

        then:
        1 * paymentAccountServiceMock.register(_)
    }

    void 'when insert credit with direct debit payment type then payment account should not be created'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([DIRECT_DEBIT])
        Credit credit = fixtureCreator.createCredit(knownProduct)

        when:
        service.insert(credit)

        then:
        0 * paymentAccountServiceMock.register(_)
    }

    void 'a credit should be inserted with now date time'(){
        given:
        def knownProduct = fixtureCreator.createProduct()
        def hirer = fixtureCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields", new Rule(){{
            add("hirerDocument", hirer.getDocumentNumber())
            add("product", knownProduct)
        }})

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.createdDateTime > 1.second.ago
        assert result.createdDateTime < 1.second.from.now
    }

    void 'given a credit with direct debit insertion type should be inserted with processing situation'(){
        given:
        def knownProduct = fixtureCreator.createProduct()
        def hirer = fixtureCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields", new Rule(){{
            add("hirerDocument", hirer.getDocumentNumber())
            add("product", knownProduct)
            add("situation", CreditSituation.CONFIRMED)
            add("creditInsertionType", DIRECT_DEBIT)
        }})

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
        result.getSituation() == CreditSituation.PROCESSING
    }

    @Unroll
    "given a credit with #insertionType insertion type should be inserted with confirmed situation"(){
        given:
        def knownProduct = fixtureCreator.createProduct()
        def hirer = fixtureCreator.createHirer()
        def creditInsertionType = insertionType
        Credit credit = Fixture.from(Credit.class).gimme("allFields", new Rule(){{
            add("hirerDocument", hirer.getDocumentNumber())
            add("product", knownProduct)
            add("situation", CreditSituation.CONFIRMED)
            add("creditInsertionType", creditInsertionType)
        }})

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
        result.getSituation() == situation

        where:
        insertionType                      | situation
        BOLETO         | CreditSituation.CONFIRMED
        CREDIT_CARD    | CreditSituation.CONFIRMED
    }

    @Unroll
    void 'when insert credits with #insertionType then the available balance should be updated'(){
        given:
        def knownProduct = fixtureCreator.createProduct().with { creditInsertionTypes = [insertionType]; it }
        Credit creditA =  fixtureCreator.createCredit(knownProduct)
        Credit creditB =  fixtureCreator.createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.availableValue == Rounder.round(creditB.value)

        where:
        insertionType                      | _
        BOLETO         | _
        CREDIT_CARD    | _
    }

    void 'when insert credits with direct debit then the available balance should be zero'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([DIRECT_DEBIT])
        Credit creditA =  fixtureCreator.createCredit(knownProduct)
        Credit creditB =  fixtureCreator.createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.availableValue == 0.0

    }

    void 'given more of one credit when insert credits the available balance should be updated'(){
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
        result.availableValue == Rounder.round(creditC.value)
    }

    void 'when insert credits with direct debit the block balance should be updated'(){
        given:
        def knownProduct = fixtureCreator.createProduct()
                .with { creditInsertionTypes = [DIRECT_DEBIT]; it }
        Credit creditA = fixtureCreator.createCredit(knownProduct)
        Credit creditB =  fixtureCreator.createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.blockedValue == Rounder.round(creditB.value)
    }

    @Unroll
    void 'when insert credits with #insertionType then the block balance should be zero'(){
        given:
        def knownProduct = fixtureCreator.createProductWithCreditInsertionType([insertionType])
        Credit creditA =  fixtureCreator.createCredit(knownProduct)
        Credit creditB =  fixtureCreator.createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.blockedValue == 0.0

        where:
        insertionType                      | _
        BOLETO         | _
        CREDIT_CARD    | _
    }

    void 'credit with product should be inserted with product credit insertion type'(){
        given:
        def knownProduct = fixtureCreator.createProduct()
        Credit credit =  fixtureCreator.createCredit(knownProduct)

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
        knownProduct.creditInsertionTypes.contains(result.creditInsertionType)
    }

    void 'should not be inserted when do not match payment rule group minimum value restriction'(){
        given:
        def paymentRuleGroup = fixtureCreator.createPaymentRuleGroup()
        def hirer = fixtureCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields", new Rule(){{
            add("hirerDocument", hirer.getDocumentNumber())
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
            add("hirerDocument", hirer.getDocumentNumber())
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

    void 'credit without product should be inserted with default credit insert type'(){
        given:
        def paymentRuleGroup = fixtureCreator.createPaymentRuleGroup()
        def hirer = fixtureCreator.createHirer()
        fixtureCreator.createPaymentRuleGroupDefault()
        Credit credit = Fixture.from(Credit.class).gimme("withoutProductAndCreditInsertionType", new Rule(){{
            add("hirerDocument", hirer.getDocumentNumber())
            add("paymentRuleGroup", paymentRuleGroup)
        }})

        when:
        def inserted = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        result.creditInsertionType == BOLETO
    }

    void 'given a credit without product should be inserted with payment rule group'(){
        given:
        def paymentRuleGroup = fixtureCreator.createPaymentRuleGroup()
        def hirer = fixtureCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withProduct", new Rule(){{
            add("hirerDocument", hirer.getDocumentNumber())
            add("paymentRuleGroup", paymentRuleGroup)
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
            add("hirerDocument", hirer.getDocumentNumber())
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
            add("hirerDocument", hirer.getDocumentNumber())
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
        assert ex.errors.first().logref == 'HIRER_DOCUMENT_NOT_FOUND'
    }

    void 'given a credit without payment rule group and product should be inserted'(){
        given:
        def paymentRuleGroup = fixtureCreator.createPaymentRuleGroup()
        def hirer = fixtureCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withoutProductAndPaymentRuleGroup", new Rule(){{
            add("hirerDocument", hirer.getDocumentNumber())
            add("paymentRuleGroup", paymentRuleGroup)
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
        Fixture.from(Credit.class).gimme("withProduct", new Rule(){{
            add("product", fixtureCreator.createProduct())
            add("hirerDocument",hirer.getDocumentNumber() )
        }})
    }

}
