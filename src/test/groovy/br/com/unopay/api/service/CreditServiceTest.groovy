package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.Credit
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired

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

    void 'given a credit without product should be inserted'(){
        given:
        def hirer = setupCreator.createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("withoutProduct")
                .with { paymentRuleGroup = setupCreator.createPaymentRuleGroup()
                        hirerDocument = hirer.getDocumentNumber()
                    it }

        when:
        def inserted  = service.insert(credit)
        def result = service.findById(inserted.id)

        then:
        assert result.id != null
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
