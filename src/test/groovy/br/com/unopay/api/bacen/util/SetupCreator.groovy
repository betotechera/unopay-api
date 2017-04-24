package br.com.unopay.api.bacen.util

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.service.AccreditedNetworkService
import br.com.unopay.api.bacen.service.ContractorService
import br.com.unopay.api.bacen.service.EstablishmentService
import br.com.unopay.api.bacen.service.IssuerService
import br.com.unopay.api.bacen.service.PaymentRuleGroupService
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.Product
import br.com.unopay.api.service.PaymentInstrumentService
import br.com.unopay.api.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SetupCreator {

    @Autowired
    private AccreditedNetworkService accreditedNetworkService

    @Autowired
    private EstablishmentService establishmentService

    @Autowired
    private IssuerService issuerService

    @Autowired
    private PaymentRuleGroupService paymentRuleGroupService

    @Autowired
    private ProductService productService

    @Autowired
    private ContractorService contractorService

    @Autowired
    private PaymentInstrumentService paymentInstrumentService

    Establishment createHeadOffice() {
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        accreditedNetworkService.create(establishment.network)
        establishmentService.create(establishment)
    }

    AccreditedNetwork createNetwork() {
        AccreditedNetwork accreditedNetwork = Fixture.from(AccreditedNetwork.class).gimme("valid")
        accreditedNetworkService.create(accreditedNetwork)
    }
    Issuer createIssuer() {
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        issuerService.create(issuer)
    }

    PaymentRuleGroup createPaymentRuleGroup() {
        PaymentRuleGroup paymentRuleGroup = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        paymentRuleGroupService.create(paymentRuleGroup)
    }

    Contractor createContractor(){
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")
        contractorService.create(contractor)
    }

    Product createProduct(){
        Product product = Fixture.from(Product.class).gimme("valid")
                .with { accreditedNetwork = createNetwork()
                        issuer = createIssuer()
                        paymentRuleGroup = createPaymentRuleGroup()
                  it }
        productService.save(product)
    }

    PaymentInstrument createPaymentInstrument(String label){
        PaymentInstrument instrument = Fixture.from(PaymentInstrument.class).gimme(label)
                .with { product = createProduct()
                        contractor = createContractor()
                    it }
        paymentInstrumentService.save(instrument)
    }
}
