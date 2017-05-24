package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.Credit
import br.com.unopay.api.model.CreditInsertionType
import br.com.unopay.api.model.CreditSituation
import br.com.unopay.api.model.Product
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
    CreditPaymentAccountService paymentAccountServiceMock = Mock(CreditPaymentAccountService)

    void setup(){
        service.creditPaymentAccountService = paymentAccountServiceMock
        Integer.mixin(TimeCategory)
    }

    void 'credit with product should be inserted with product payment rule group'(){
        given:
        def knownProduct = setupCreator.createProduct()
        Credit credit = setupCreator.createCredit(knownProduct)

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
        result.getPaymentRuleGroup() == knownProduct.getPaymentRuleGroup()
    }

    void 'credit with unknown product should not be inserted'(){
        given:
        Product product = setupCreator.createProduct()
        Credit credit = setupCreator.createCredit(product.with { id = ''; it })

        when:
        service.insert(credit)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }

    void 'credit with unknown payment rule groups should not be inserted'(){
        given:
        PaymentRuleGroup unknownPaymentRuleGroup = setupCreator.createPaymentRuleGroup().with { id = ''; it }
        def knownProduct = setupCreator.createProduct().with { paymentRuleGroup = unknownPaymentRuleGroup; it }
        Credit credit = setupCreator.createCredit(knownProduct)

        when:
        service.insert(credit)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_RULE_GROUP_NOT_FOUND'
    }

    void 'when insert credit should be generate credit number'() {
        given:
        def knownProduct = setupCreator.createProduct()
        Credit credit = setupCreator.createCredit(knownProduct)
                .with { creditNumber = null; it }

        when:
        def inserted = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.creditNumber != null
    }

    void 'when insert credit then generated credit number should be incremented'() {
        given:
        def knownProduct = setupCreator.createProduct()
        Credit credit = setupCreator.createCredit(knownProduct)
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
        def knownProduct = setupCreator.createProductWithCreditInsertionType([CreditInsertionType.PAMCARD_SYSTEM])
        Credit credit = setupCreator.createCredit(knownProduct)

        when:
        service.insert(credit)

        then:
        1 * paymentAccountServiceMock.register(_)
    }

    void 'when insert credit with direct debit payment type then payment account should not be created'(){
        given:
        def knownProduct = setupCreator.createProductWithCreditInsertionType([CreditInsertionType.DIRECT_DEBIT])
        Credit credit = setupCreator.createCredit(knownProduct)

        when:
        service.insert(credit)

        then:
        0 * paymentAccountServiceMock.register(_)
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
        assert result.createdDateTime < 1.second.from.now
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
        def knownProduct = setupCreator.createProduct().with { creditInsertionTypes = [insertionType]; it }
        Credit creditA =  setupCreator.createCredit(knownProduct)
        Credit creditB =  setupCreator.createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.availableValue == creditB.value.setScale(2, BigDecimal.ROUND_HALF_UP)

        where:
        insertionType|_
        CreditInsertionType.BOLETO|_
        CreditInsertionType.CREDIT_CARD|_
        CreditInsertionType.PAMCARD_SYSTEM|_
    }

    void 'when insert credits with direct debit, available balance should be zero'(){
        given:
        def knownProduct = setupCreator.createProductWithCreditInsertionType([CreditInsertionType.DIRECT_DEBIT])
        Credit creditA =  setupCreator.createCredit(knownProduct)
        Credit creditB =  setupCreator.createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.availableValue == 0.0

    }

    void 'given more one credit when insert credits available balance should be updated'(){
        given:
        def knownProduct = setupCreator.createProductWithCreditInsertionType([CreditInsertionType.PAMCARD_SYSTEM])
        Credit creditA =  setupCreator.createCredit(knownProduct)
        Credit creditB = setupCreator.createCredit(knownProduct)
        Credit creditC =  setupCreator.createCredit(knownProduct)

        when:
        service.insert(creditA)
        service.insert(creditB)
        def inserted = service.insert(creditC)
        def result = service.findById(inserted.id)

        then:
        result.availableValue == creditC.value.setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    void 'when insert credits with direct debit, block balance should be updated'(){
        given:
        def knownProduct = setupCreator.createProduct().with { creditInsertionTypes = [CreditInsertionType.DIRECT_DEBIT]; it }
        Credit creditA = setupCreator.createCredit(knownProduct)
        Credit creditB =  setupCreator.createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.blockedValue == creditB.value.setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    @Unroll
    void 'when insert credits with #insertionType, block balance should be zero'(){
        given:
        def knownProduct = setupCreator.createProductWithCreditInsertionType([insertionType])
        Credit creditA =  setupCreator.createCredit(knownProduct)
        Credit creditB =  setupCreator.createCredit(knownProduct)

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.blockedValue == 0.0

        where:
        insertionType|_
        CreditInsertionType.BOLETO|_
        CreditInsertionType.CREDIT_CARD|_
        CreditInsertionType.PAMCARD_SYSTEM|_
    }

    void 'credit with product should be inserted with product credit insertion type'(){
        given:
        def knownProduct = setupCreator.createProduct()
        Credit credit =  setupCreator.createCredit(knownProduct)

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
        knownProduct.creditInsertionTypes.contains(result.creditInsertionType)
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
        Credit credit = Fixture.from(Credit.class).gimme("withProduct")
                .with { product = setupCreator.createProduct()
            it }

        when:
        service.insert(credit)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'HIRER_DOCUMENT_NOT_FOUND'
    }

    void 'given a credit without payment rule group and product should be inserted'(){
        given:
        setupCreator.createPaymentRuleGroupDefault()
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withoutProductAndPaymentRuleGroup")
                            .with { hirerDocument = hirer.getDocumentNumber(); it }

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
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withProduct")
                .with {
            product = setupCreator.createProduct()
            hirerDocument = hirer.getDocumentNumber()
            it
        }
        credit
    }


}
