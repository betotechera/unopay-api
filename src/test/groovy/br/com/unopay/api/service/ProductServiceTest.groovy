package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.Product
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired

class ProductServiceTest extends SpockApplicationTests {

    @Autowired
    ProductService service

    @Autowired
    FixtureCreator fixtureCreator

    Issuer issuerUnderTest
    AccreditedNetwork networkUnderTest
    PaymentRuleGroup paymentRuleGroupUnderTest

    void setup(){
        issuerUnderTest = fixtureCreator.createIssuer()
        paymentRuleGroupUnderTest = fixtureCreator.createPaymentRuleGroup()
        networkUnderTest = fixtureCreator.createNetwork()
    }

    void 'new product should be created'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid", new Rule(){{
            add("accreditedNetwork", networkUnderTest)
            add("issuer", issuerUnderTest)
            add("paymentRuleGroup", paymentRuleGroupUnderTest)
        }})

        when:
        def result  = service.create(product)

        then:
        assert result.id != null
    }

    void 'given product with same name should not be created'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid", new Rule(){{
            add("accreditedNetwork", networkUnderTest)
            add("issuer", issuerUnderTest)
            add("paymentRuleGroup", paymentRuleGroupUnderTest)
        }})

        when:
        service.create(product)
        service.create(product.with { code = 'AAA'; id = null; it })

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'PRODUCT_ALREADY_EXISTS'
    }

    void 'given product with same code should not be created'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid", new Rule(){{
            add("accreditedNetwork", networkUnderTest)
            add("issuer", issuerUnderTest)
            add("paymentRuleGroup", paymentRuleGroupUnderTest)
        }})

        when:
        service.create(product)
        service.create(product.with { name = 'AAA'; id = null; it })

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'PRODUCT_ALREADY_EXISTS'
    }

    void 'given product with unknown network should not be created'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest.with { id = ''; it }
            issuer = issuerUnderTest
            paymentRuleGroup = paymentRuleGroupUnderTest
            it }

        when:
        service.create(product)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ACCREDITED_NETWORK_NOT_FOUND'
    }

    void 'given product with unknown issuer should not be created'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest
            issuer = issuerUnderTest.with { id = ''; it }
            paymentRuleGroup = paymentRuleGroupUnderTest
            it }

        when:
        service.create(product)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ISSUER_NOT_FOUND'
    }

    void 'given product with unknown payment rule group should not be created'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest
            issuer = issuerUnderTest
            paymentRuleGroup = paymentRuleGroupUnderTest.with { id = ''; it }
            it }

        when:
        service.create(product)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_RULE_GROUP_NOT_FOUND'
    }

    void 'given product without network id should not be created'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest.with { id = null; it }
            issuer = issuerUnderTest
            paymentRuleGroup = paymentRuleGroupUnderTest
            it }

        when:
        service.create(product)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ACCREDITED_NETWORK_ID_REQUIRED'
    }

    void 'given product without issuer id should not be created'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest
            issuer = issuerUnderTest.with { id = null; it }
            paymentRuleGroup = paymentRuleGroupUnderTest
            it }

        when:
        service.create(product)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ISSUER_ID_REQUIRED'
    }

    void 'given product without payment rule group id should not be created'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest
            issuer = issuerUnderTest
            paymentRuleGroup = paymentRuleGroupUnderTest.with { id = null; it }
            it }

        when:
        service.create(product)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'PAYMENT_RULE_GROUP_ID_REQUIRED'
    }

    void 'known product should be updated'(){
        given:
        Product product = createProduct('name')

        def created  = service.create(product)
        def newName = 'ProductNew'
        product.name = newName

        when:
        service.update(created.id, product)
        def result = service.findById(created.id)

        then:
        assert result.name == newName
    }

    void 'unknown product should not be updated'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")

        def newName = 'ProductNew'
        product.name = newName

        when:
        service.update('', product)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }

    void 'given product with same name should not be updated'(){
        given:
        def knownName = 'myName'
        Product product = createProduct(knownName)

        service.create(product)
        def created = service.create(product.with { code = 'AAA'; name='AAA'; id = null; it })

        when:
        service.update(created.id, product.with { code = 'BBB'; name = knownName; id = null; it })

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'PRODUCT_ALREADY_EXISTS'
    }

    void 'given product with same code should not be updated'(){
        given:
        def knownCode = 'MYCD'
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest
            issuer = issuerUnderTest
            paymentRuleGroup = paymentRuleGroupUnderTest
            code = knownCode
            it }

        service.create(product)
        def created = service.create(product.with { code = 'BBB'; name='AAA'; id = null; it })

        when:
        service.update(created.id, product.with { name = 'BBB'; code = knownCode; it })

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'PRODUCT_ALREADY_EXISTS'
    }

    void 'given product with unknown network should not be updated'(){
        given:
        def knownName = 'myName'
        Product product = createProduct(knownName)

        def created = service.create(product)

        when:
        service.update(created.id, product.with { name = knownName
                                                  accreditedNetwork = networkUnderTest.with { id = ''; it }
                                                it })
        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ACCREDITED_NETWORK_NOT_FOUND'
    }



    void 'given product with unknown issuer should not be updated'(){
        given:
        def knownName = 'myName'
        Product product = createProduct(knownName)

        def created = service.create(product)

        when:
        service.update(created.id, product.with { name = knownName; issuer = issuerUnderTest.with { id = ''; it }; it })

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ISSUER_NOT_FOUND'
    }

    void 'given product with unknown payment rule group should not be updated'(){
        given:
        def knownName = 'myName'
        Product product = createProduct(knownName)

        def created = service.create(product)

        when:
        service.update(created.id, product.with {
                                                    name = knownName
                                                    paymentRuleGroup = paymentRuleGroupUnderTest.with { id = ''; it }
                                                it })
        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_RULE_GROUP_NOT_FOUND'
    }

    void 'given product without network id should be updated'(){
        given:
        def knownName = 'myName'
        Product product = createProduct(knownName)

        def created = service.create(product)

        when:
        service.update(created.id, product.with { name = knownName
                                                  accreditedNetwork = networkUnderTest.with { id = null; it }
                                                it })
        def result = service.findById(created.id)
        then:
        result.name == knownName
    }

    void 'given product without issuer id should be updated'(){
        given:
        def knownName = 'myName'
        Product product = createProduct(knownName)

        def created = service.create(product)

        when:
        service.update(created.id, product.with { name = knownName
                                                  issuer = issuerUnderTest.with { id = null; it }
                                                it })
        def result = service.findById(created.id)

        then:
        result.name == knownName
    }

    void 'given product without payment rule group id should be updated'(){
        given:
        def knownName = 'myName'
        Product product = createProduct(knownName)

        def created = service.create(product)

        when:
        service.update(created.id, product.with {
            name = knownName
            paymentRuleGroup = paymentRuleGroupUnderTest.with { id = null; it }
            it })
        def result = service.findById(created.id)

        then:
        result.name == knownName
    }

    void 'known product should be found'(){
        given:
        Product product = createProduct('name')

        def created  = service.create(product)
        when:
        def result = service.findById(created.id)

        then:
        assert result != null
    }

    void 'unknown product should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }

    void 'known product should be deleted'(){
        given:
        Product product = createProduct('name')

        def created  = service.create(product)
        when:
        service.delete(created.id)
        service.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }

    void 'unknown product should not be deleted'(){
        when:
        service.delete('')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }

    void 'given product with monthsToExpireBonus and with bonusPercentage should be created'(){
        given:
        Product product = createProduct()
        product.monthsToExpireBonus = 12
        product.bonusPercentage = 0.1

        when:
        def created = service.create(product)

        then:
        created.id
    }

    void 'given product with monthsToExpireBonus and without bonusPercentage should not be created'(){
        given:
        Product product = createProduct()
        product.monthsToExpireBonus = 22

        when:
        service.create(product)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'BONUS_PERCENTAGE_REQUIRED'
    }

    void 'given product with bonusPercentage and without monthsToExpireBonus should not be created'(){
        given:
        Product product = createProduct()
        product.bonusPercentage = 0.1

        when:
        service.create(product)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'BONUS_EXPIRY_MONTH_REQUIRED'
    }

    private Object createProduct(newName='name') {
        Fixture.from(Product.class).gimme("valid")
                .with {
            accreditedNetwork = networkUnderTest
            issuer = issuerUnderTest
            paymentRuleGroup = paymentRuleGroupUnderTest
            name = newName
            it
        }
    }
}
