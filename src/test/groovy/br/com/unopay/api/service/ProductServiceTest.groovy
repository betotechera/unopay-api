package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.service.AccreditedNetworkService
import br.com.unopay.api.bacen.service.IssuerService
import br.com.unopay.api.bacen.service.PaymentRuleGroupService
import br.com.unopay.api.model.Product
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class ProductServiceTest extends SpockApplicationTests {

    @Autowired
    ProductService service

    @Autowired
    AccreditedNetworkService networkService

    @Autowired
    IssuerService issuerService

    @Autowired
    PaymentRuleGroupService paymentRuleGroupService

    Issuer issuerUnderTest
    AccreditedNetwork networkUnderTest
    PaymentRuleGroup paymentRuleGroupUnderTest

    void setup(){
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        AccreditedNetwork network = Fixture.from(AccreditedNetwork.class).gimme("valid")
        PaymentRuleGroup paymentRuleGroup = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        issuerUnderTest = issuerService.create(issuer)
        paymentRuleGroupUnderTest = paymentRuleGroupService.create(paymentRuleGroup)
        networkUnderTest = networkService.create(network)
    }

    void 'new product should be created'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest
                        issuer = issuerUnderTest
                        paymentRuleGroup = paymentRuleGroupUnderTest
                    it }

        when:
        def result  = service.save(product)

        then:
        assert result.id != null
    }

    void 'given product with same name should not be created'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest
            issuer = issuerUnderTest
            paymentRuleGroup = paymentRuleGroupUnderTest
            it }

        when:
        service.save(product)
        service.save(product.with { code = 'AAA'; id = null; it })

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'PRODUCT_ALREADY_EXISTS'
    }

    void 'given product with same code should not be created'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest
            issuer = issuerUnderTest
            paymentRuleGroup = paymentRuleGroupUnderTest
            it }

        when:
        service.save(product)
        service.save(product.with { name = 'AAA'; id = null; it })

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'PRODUCT_ALREADY_EXISTS'
    }

    void 'known product should be updated'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest
            issuer = issuerUnderTest
            paymentRuleGroup = paymentRuleGroupUnderTest
            it }

        def created  = service.save(product)
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
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest
            issuer = issuerUnderTest
            paymentRuleGroup = paymentRuleGroupUnderTest
            name = knownName
            it }

        service.save(product)
        def created = service.save(product.with { code = 'AAA'; name='AAA'; id = null; it })

        when:
        service.update(created.id, product.with { code = 'AAA'; name = knownName; id = null; it })

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'PRODUCT_ALREADY_EXISTS'
    }

    void 'given product with same code should not be updated'(){
        given:
        def knownCode = 'myCode'
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest
            issuer = issuerUnderTest
            paymentRuleGroup = paymentRuleGroupUnderTest
            code = knownCode
            it }

        service.save(product)
        def created = service.save(product.with { code = 'AAA'; name='AAA'; id = null; it })

        when:
        service.update(created.id, product.with { name = 'BBB'; code = knownCode; id = null; it })

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'PRODUCT_ALREADY_EXISTS'
    }

    void 'known product should be found'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest
            issuer = issuerUnderTest
            paymentRuleGroup = paymentRuleGroupUnderTest
            it }

        def created  = service.save(product)
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
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest
            issuer = issuerUnderTest
            paymentRuleGroup = paymentRuleGroupUnderTest
            it }

        def created  = service.save(product)
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
}
