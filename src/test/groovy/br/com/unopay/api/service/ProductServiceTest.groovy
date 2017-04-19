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


    void 'should create product'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = networkUnderTest;
                        issuer = issuerUnderTest;
                        paymentRuleGroup = paymentRuleGroupUnderTest; it }

        when:
        def result  = service.save(product)

        then:
        assert result.id != null
    }
}
