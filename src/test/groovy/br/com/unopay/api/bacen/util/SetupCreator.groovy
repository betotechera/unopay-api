package br.com.unopay.api.bacen.util

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.*
import br.com.unopay.api.bacen.service.*
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.Product
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
    private HirerService hirerService

    @Autowired
    private PaymentBankAccountService paymentBankAccountService

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

    PaymentRuleGroup createPaymentRuleGroupDefault() {
        PaymentRuleGroup paymentRuleGroup = Fixture.from(PaymentRuleGroup.class).gimme("default")
        paymentRuleGroupService.create(paymentRuleGroup)
    }

    PaymentBankAccount createPaymentBankAccount() {
        PaymentBankAccount paymentRuleGroup = Fixture.from(PaymentBankAccount.class).gimme("valid")
        paymentBankAccountService.create(paymentRuleGroup)
    }


    PaymentInstrument createPaymentInstrument(String label) {
        return Fixture.from(PaymentInstrument.class).gimme(label)
                .with {
            product = createProduct()
            contractor = createContractor()
            it
        }
    }

    Hirer createHirer() {
        Hirer hirer = Fixture.from(Hirer.class).gimme("valid")
        hirerService.create(hirer)
    }
    Contractor createContractor() {
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")
        contractorService.create(contractor)
    }
    Product createProduct() {
        Product product = Fixture.from(Product.class).gimme("valid")
        product = product.with {
            issuer = createIssuer()
            accreditedNetwork = createNetwork()
            paymentRuleGroup = createPaymentRuleGroup()
            it
        }
        productService.save(product)
    }
    Product createSimpleProduct(){
        createProduct().with {
            accreditedNetwork = null
            issuer = null
            paymentRuleGroup = null
            it
        }
    }

    Establishment createEstablishment() {
        null
    }
}
