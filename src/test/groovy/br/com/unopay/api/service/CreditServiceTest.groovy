package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.Credit
import br.com.unopay.api.model.CreditInsertionType
import br.com.unopay.api.model.CreditSituation
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import groovy.time.TimeCategory
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class CreditServiceTest extends SpockApplicationTests {

    @Autowired
    CreditService service

    @Autowired
    SetupCreator setupCreator

    void setup(){
        Integer.mixin(TimeCategory)
    }

    void 'credit with product should be inserted with product payment rule group'(){
        given:
        def knownProduct = setupCreator.createProduct()
        Credit credit = createCredit(knownProduct)

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
        result.getPaymentRuleGroup() == knownProduct.getPaymentRuleGroup()
    }



    void 'a credit should be inserted with now date time'(){
        given:
        def knownProduct = setupCreator.createProduct()
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields")
                .with {
            hirerDocument = hirer.getDocumentNumber()
            product = knownProduct

            it }

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.createdDateTime > 1.second.ago
    }

    void 'given a credit with direct debit insertion type should be inserted with processing situation'(){
        given:
        def knownProduct = setupCreator.createProduct()
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields")
                .with {
            hirerDocument = hirer.getDocumentNumber()
            product = knownProduct
            situation = CreditSituation.CONFIRMED
            creditInsertionType = CreditInsertionType.DIRECT_DEBIT

            it }

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
        result.getSituation() == CreditSituation.PROCESSING
    }

    @Unroll
    "given a credit with #insertionType insertion type should be inserted with processing situation"(){
        given:
        def knownProduct = setupCreator.createProduct()
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields")
                .with {
            hirerDocument = hirer.getDocumentNumber()
            product = knownProduct
            situation = CreditSituation.CONFIRMED
            creditInsertionType = insertionType

            it }

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
        result.getSituation() == situation

        where:
        insertionType|situation
        CreditInsertionType.BOLETO|CreditSituation.CONFIRMED
        CreditInsertionType.CREDIT_CARD|CreditSituation.CONFIRMED
        CreditInsertionType.PAMCARD_SYSTEM|CreditSituation.CONFIRMED
    }

    @Unroll
    void 'when insert credits with #insertionType available balance should be updated'(){
        given:
        def knownProduct = setupCreator.createProduct().with { creditInsertionType = insertionType; it }
        Credit creditA =  createCredit(knownProduct)
        Credit creditB =  createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.availableBalance == creditB.value.setScale(2, BigDecimal.ROUND_HALF_UP)

        where:
        insertionType|_
        CreditInsertionType.BOLETO|_
        CreditInsertionType.CREDIT_CARD|_
        CreditInsertionType.PAMCARD_SYSTEM|_
    }

    void 'when insert credits with direct debit, available balance should be zero'(){
        given:
        def knownProduct = setupCreator.createProduct().with { creditInsertionType = CreditInsertionType.DIRECT_DEBIT; it }
        Credit creditA =  createCredit(knownProduct)
        Credit creditB =  createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.availableBalance == 0.0

    }

    void 'given more one credit when insert credits available balance should be updated'(){
        given:
        def knownProduct = setupCreator.createProduct().with { creditInsertionType = CreditInsertionType.PAMCARD_SYSTEM; it }
        Credit creditA =  createCredit(knownProduct)
        Credit creditB = createCredit(knownProduct)
        Credit creditC =  createCredit(knownProduct)

        when:
        service.insert(creditA)
        service.insert(creditB)
        def inserted = service.insert(creditC)
        def result = service.findById(inserted.id)

        then:
        result.availableBalance == creditC.value.setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    void 'when insert credits with direct debit, block balance should be updated'(){
        given:
        def knownProduct = setupCreator.createProduct().with { creditInsertionType = CreditInsertionType.DIRECT_DEBIT; it }
        Credit creditA = createCredit(knownProduct)
        Credit creditB =  createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.blockedBalance == creditB.value.setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    @Unroll
    void 'when insert credits with #insertionType, block balance should be zero'(){
        given:
        def knownProduct = setupCreator.createProduct().with { creditInsertionType = insertionType; it }
        Credit creditA =  createCredit(knownProduct)
        Credit creditB =  createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.blockedBalance == 0.0

        where:
        insertionType|_
        CreditInsertionType.BOLETO|_
        CreditInsertionType.CREDIT_CARD|_
        CreditInsertionType.PAMCARD_SYSTEM|_
    }




    void 'credit with product should be inserted with product credit insertion type'(){
        given:
        def knownProduct = setupCreator.createProduct()
        Credit credit =  createCredit(knownProduct)

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
        result.getCreditInsertionType() == knownProduct.getCreditInsertionType()
    }

    void 'credit with product should not be inserted when do not match product minimum value restriction'(){
        given:
        def knownProduct = setupCreator.createProduct()
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields")
                .with {
            hirerDocument = hirer.getDocumentNumber()
            product = knownProduct
            value = knownProduct.minimumCreditInsertion - 1

            it }
        when:
        service.insert(credit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'MINIMUM_PRODUCT_VALUE_NOT_MET'
    }

    void 'credit with product should not be inserted when do not match product maximum value restriction'(){
        given:
        def knownProduct = setupCreator.createProduct()
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields")
                .with {
            hirerDocument = hirer.getDocumentNumber()
            product = knownProduct
            value = knownProduct.maximumCreditInsertion + 1

            it }
        when:
        service.insert(credit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'MAXIMUM_PRODUCT_VALUE_NOT_MET'
    }

    void 'credit without product should not be inserted when value is not greater than zero'(){
        given:
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields")
                .with {
            hirerDocument = hirer.getDocumentNumber()
            product = null
            value = 0

            it }
        when:
        service.insert(credit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'MINIMUM_CREDIT_VALUE_NOT_MET'
    }

    void 'credit without product should be inserted with default credit insert type'(){
        given:
        def hirer = setupCreator.createHirer()
        setupCreator.createPaymentRuleGroupDefault()
        Credit credit = Fixture.from(Credit.class).gimme("withoutProductAndCreditInsertionType")
                .with {
                        hirerDocument = hirer.getDocumentNumber()
                      it }
        when:
        def inserted = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        result.creditInsertionType == CreditInsertionType.PAMCARD_SYSTEM
    }

    void 'given a credit without product should be inserted with default payment rule group'(){
        given:
        def paymentRuleGroup = setupCreator.createPaymentRuleGroupDefault()
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withProduct")
                .with {
                        hirerDocument = hirer.getDocumentNumber()
                    it }

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

    void 'given a credit without product and default payment rule group should not be inserted'(){
        given:
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withProduct")
                .with {
                    hirerDocument = hirer.getDocumentNumber()
                    it }
        def paymentRule = service.defaultPaymentRuleGroup
        service.setDefaultPaymentRuleGroup("")

        when:
        service.insert(credit)
        service.setDefaultPaymentRuleGroup(paymentRule)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'DEFAULT_PAYMENT_RULE_GROUP_NOT_CONFIGURED'
    }

    void 'given a credit with known hirer document should be inserted'(){
        given:
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withProduct")
                .with { product = setupCreator.createProduct()
                        hirerDocument = hirer.getDocumentNumber()
                    it }

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
    }

    void 'given a credit with unknown hirer document should not be inserted'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("withProduct")
                .with { product = setupCreator.createProduct()
            it }

        when:
        service.insert(credit)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'HIRER_DOCUMENT_NOT_FOUND'
    }

    void 'given a credit without payment rule group and product should not be inserted'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("withoutProductAndPaymentRuleGroup")

        when:
        service.insert(credit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'PAYMENT_RULE_GROUP_OR_PRODUCT_REQUIRED'
    }

    private Credit createCredit(knownProduct) {
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields")
                .with {
            hirerDocument = hirer.getDocumentNumber()
            product = knownProduct

            it
        }
        credit
    }

}
