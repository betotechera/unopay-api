package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.Credit
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired

class CreditServiceTest extends SpockApplicationTests {

    @Autowired
    CreditService service

    @Autowired
    SetupCreator setupCreator

    void 'credit with product should be created with product payment rule group'(){
        given:
        def knownProduct = setupCreator.createProduct()
        Credit credit = Fixture.from(Credit.class).gimme("allFields")
                .with {
                        product = knownProduct
                    it }

        when:
        def created  = service.save(credit)
        def result = service.findById(created.id)

        then:
        assert result.id != null
        result.getPaymentRuleGroup() == knownProduct.getPaymentRuleGroup()
    }

    void 'given a credit without product should be created'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("withoutProduct")
                .with { paymentRuleGroup = setupCreator.createPaymentRuleGroup()
                    it }

        when:
        def created  = service.save(credit)
        def result = service.findById(created.id)

        then:
        assert result.id != null
    }

    void 'given a credit without payment rule group and product should not be created'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("withoutProductAndPaymentRuleGroup")

        when:
        service.save(credit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'PAYMENT_RULE_GROUP_OR_PRODUCT_REQUIRED'
    }




}
