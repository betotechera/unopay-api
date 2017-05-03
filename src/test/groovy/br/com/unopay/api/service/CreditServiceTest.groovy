package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.Credit
import br.com.unopay.api.model.CreditInsertionType
import br.com.unopay.api.model.CreditSituation
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Unroll

class CreditServiceTest extends SpockApplicationTests {

    @Autowired
    CreditService service

    @Autowired
    SetupCreator setupCreator

    void 'credit with product should be inserted with product payment rule group'(){
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
        assert result.id != null
        result.getPaymentRuleGroup() == knownProduct.getPaymentRuleGroup()
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
    "given a credit with #insertionTYpe insertion type should be inserted with processing situation"(){
        given:
        def knownProduct = setupCreator.createProduct()
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields")
                .with {
            hirerDocument = hirer.getDocumentNumber()
            product = knownProduct
            situation = CreditSituation.CONFIRMED
            creditInsertionType = insertionTYpe

            it }

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
        result.getSituation() == situation

        where:
        insertionTYpe|situation
        CreditInsertionType.BOLETO|CreditSituation.CONFIRMED
        CreditInsertionType.CREDIT_CARD|CreditSituation.CONFIRMED
        CreditInsertionType.PAMCARD_SYSTEM|CreditSituation.CONFIRMED
    }

    void 'when insert credits available balance should be updated'(){
        given:
        def knownProduct = setupCreator.createProduct()
        def hirer = setupCreator.createHirer()
        Credit creditA = Fixture.from(Credit.class).gimme("allFields")
                .with {
                    hirerDocument = hirer.getDocumentNumber()
                    product = knownProduct
                    it }

        Credit creditB = Fixture.from(Credit.class).gimme("allFields")
                .with {
                    hirerDocument = hirer.getDocumentNumber()
                    product = knownProduct
                it }

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.availableBalance == (creditA.value + creditB.value ).setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    void 'given more one credit when insert credits available balance should be updated'(){
        given:
        def knownProduct = setupCreator.createProduct()
        def hirer = setupCreator.createHirer()
        Credit creditA = Fixture.from(Credit.class).gimme("allFields")
                .with {
            hirerDocument = hirer.getDocumentNumber()
            product = knownProduct
            it }

        Credit creditB = Fixture.from(Credit.class).gimme("allFields")
                .with {
            hirerDocument = hirer.getDocumentNumber()
            product = knownProduct
            it }
        Credit creditC = Fixture.from(Credit.class).gimme("allFields")
                .with {
            hirerDocument = hirer.getDocumentNumber()
            product = knownProduct
            it }

        when:
        service.insert(creditA)
        service.insert(creditB)
        def inserted = service.insert(creditC)
        def result = service.findById(inserted.id)

        then:
        result.availableBalance == (creditA.value + creditB.value + creditC.value).setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    void 'when insert credits block balance should be updated'(){
        given:
        def knownProduct = setupCreator.createProduct()
        def hirer = setupCreator.createHirer()
        Credit creditA = Fixture.from(Credit.class).gimme("allFields")
                .with {
            hirerDocument = hirer.getDocumentNumber()
            product = knownProduct
            it }
        Credit creditB = Fixture.from(Credit.class).gimme("allFields")
                .with {
            hirerDocument = hirer.getDocumentNumber()
            product = knownProduct
            it }

        when:
        service.insert(creditA)
        def inserted = service.insert(creditB)
        def result = service.findById(inserted.id)

        then:
        result.blockedBalance == (creditA.value + creditB.value ).setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    void 'credit with product should be inserted with product credit insertion type'(){
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

    void 'credit without product and credit insert type should not be inserted'(){
        given:
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withoutProductAndCreditInsertionType")
                .with {
                        hirerDocument = hirer.getDocumentNumber()
                      it }
        when:
        service.insert(credit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CREDIT_INSERT_TYPE_REQUIRED'
    }

    void 'given a credit without product should be inserted with default payment rule group'(){
        given:
        def paymentRuleGroup = setupCreator.createPaymentRuleGroupDefault()
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withoutProduct")
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
        Credit credit = Fixture.from(Credit.class).gimme("withoutProduct")
                .with {
                    hirerDocument = hirer.getDocumentNumber()
                    it }
        service.setDefaultPaymentRuleGroup("")

        when:
        service.insert(credit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'DEFAULT_PAYMENT_RULE_GROUP_NOT_CONFIGURED'
    }

    void 'given a credit with known hirer document should be inserted'(){
        given:
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withoutProduct")
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
        Credit credit = Fixture.from(Credit.class).gimme("withoutProduct")
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

}
