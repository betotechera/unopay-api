package br.com.unopay.api.bacen.util

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.*
import br.com.unopay.api.bacen.service.*
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractorInstrumentCredit
import br.com.unopay.api.model.Credit
import br.com.unopay.api.model.CreditInsertionType
import br.com.unopay.api.model.CreditPaymentAccount
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.Product
import br.com.unopay.api.service.ContractService
import br.com.unopay.api.service.CreditPaymentAccountService
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
    private HirerService hirerService

    @Autowired
    private PaymentBankAccountService paymentBankAccountService

    @Autowired
    private ContractService contractService

    @Autowired
    private PaymentInstrumentService paymentInstrumentService

    @Autowired
    private CreditPaymentAccountService creditPaymentAccountService


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

    PaymentRuleGroup createPaymentRuleGroup(String code = null) {
        PaymentRuleGroup paymentRuleGroup = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        if(code){
            paymentRuleGroup.code = code
        }
        paymentRuleGroupService.create(paymentRuleGroup)
    }

    PaymentRuleGroup createPaymentRuleGroupDefault() {
        PaymentRuleGroup paymentRuleGroup = Fixture.from(PaymentRuleGroup.class).gimme("default")
        paymentRuleGroupService.create(paymentRuleGroup)
    }

    PaymentInstrument createPaymentInstrument(String label) {
        return Fixture.from(PaymentInstrument.class).gimme(label)
                .with {
            product = createProduct()
            contractor = createContractor()
            it
        }
    }

    PaymentInstrument createPaymentInstrumentWithProduct(
            Product productUnderTest = createProduct(),
            contractorUnderTest = createContractor(), String number = null) {
        PaymentInstrument paymentInstrument =  Fixture.from(PaymentInstrument.class).gimme("valid")
                .with {
            product = productUnderTest
            contractor = contractorUnderTest
            it
        }
        if(number){
            paymentInstrument.number = number
            paymentInstrument.externalNumberId = number
        }
        paymentInstrument
        paymentInstrumentService.save(paymentInstrument)
    }

    Hirer createHirer() {
        Hirer hirer = Fixture.from(Hirer.class).gimme("valid")
        hirerService.create(hirer)
    }
    Contractor createContractor() {
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")
        contractorService.create(contractor)
    }
    Product createProduct(code = null, paymentRuleGroupUnderTest = createPaymentRuleGroup()) {
        Product product = Fixture.from(Product.class).gimme("valid")
        product = product.with {
            issuer = createIssuer()
            accreditedNetwork = createNetwork()
            paymentRuleGroup = paymentRuleGroupUnderTest
            serviceType = [ServiceType.FREIGHT, ServiceType.ELECTRONIC_TOLL]
            it
        }
        if(code){
            product.code = code
        }
        productService.save(product)
    }

    Product createProductWithOutDirectDebit() {
        Product product = Fixture.from(Product.class).gimme("creditWithoutDirectDebit")
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

    Credit createCredit(Product knownProduct){
        def hirer = createHirer()
        Credit credit = Fixture.from(Credit.class).gimme("allFields")
                .with {
            hirerDocument = hirer.getDocumentNumber()
            product = knownProduct

            it
        }
        credit
    }

    Credit createCredit(){
        def product = createProduct()
        createCredit(product)
    }

    Contract createContract(contractorUnderTest = createContractor(),
                            productUnderTest = createProduct(), hirerUnderTest = createHirer()) {
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceType
            it }
    }

    Contract createPersistedContract(contractorUnderTest = createContractor(),
                                     productUnderTest = createProduct(), hirer = createHirer()){
        Contract contract = createContract(contractorUnderTest, productUnderTest, hirer)
        contractService.save(contract)
    }

    CreditPaymentAccount createCreditPaymentAccount() {
        return Fixture.from(CreditPaymentAccount.class).gimme("valid")
                .with {
            product = createProduct()
            issuer = createIssuer()
            paymentRuleGroup = createPaymentRuleGroup()
            it
        }
    }

    CreditPaymentAccount createCreditPaymentAccountFromContract(Contract contract = createContract()) {
        CreditPaymentAccount creditPaymentAccount =  Fixture.from(CreditPaymentAccount.class).gimme("valid")
                .with {
            product = contract.product
            issuer = contract.product.issuer
            hirerDocument = contract.hirer.documentNumber
            paymentRuleGroup = contract.product.paymentRuleGroup
            serviceType = contract.serviceType.find()
            it
        }
        creditPaymentAccountService.save(creditPaymentAccount)
    }

    ContractorInstrumentCredit createContractorInstrumentCredit(){
        def contractorUnderTest = createContractor()
        def contractUnderTest = createPersistedContract(contractorUnderTest)
        def paymentInstrumentUnderTest = createPaymentInstrumentWithProduct(contractUnderTest.product, contractorUnderTest)
        def creditPaymentAccountUnderTest = createCreditPaymentAccountFromContract(contractUnderTest)
        ContractorInstrumentCredit instrumentCredit = Fixture.from(ContractorInstrumentCredit.class).gimme("toPersist")
        instrumentCredit.with {
            paymentInstrument = paymentInstrumentUnderTest
            creditPaymentAccount = creditPaymentAccountUnderTest
            serviceType = contractUnderTest.serviceType.find()
            value = creditPaymentAccountUnderTest.availableBalance
            contract = contractUnderTest
        }
        instrumentCredit
    }

    Establishment createEstablishment() {
        null
    }
}
