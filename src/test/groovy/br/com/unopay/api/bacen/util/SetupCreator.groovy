package br.com.unopay.api.bacen.util

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Event
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.model.Service
import br.com.unopay.api.bacen.model.ServiceType
import br.com.unopay.api.bacen.service.AccreditedNetworkService
import br.com.unopay.api.bacen.service.ContractorService
import br.com.unopay.api.bacen.service.EstablishmentService
import br.com.unopay.api.bacen.service.EventService
import br.com.unopay.api.bacen.service.HirerService
import br.com.unopay.api.bacen.service.IssuerService
import br.com.unopay.api.bacen.service.PaymentRuleGroupService
import br.com.unopay.api.bacen.service.ServiceService
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractEstablishment
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.ContractorInstrumentCredit
import br.com.unopay.api.model.Credit
import br.com.unopay.api.model.CreditInsertionType
import br.com.unopay.api.model.CreditPaymentAccount
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.Product
import br.com.unopay.api.model.ServiceAuthorize
import br.com.unopay.api.service.ContractService
import br.com.unopay.api.service.ContractorInstrumentCreditService
import br.com.unopay.api.service.CreditPaymentAccountService
import br.com.unopay.api.service.PaymentInstrumentService
import br.com.unopay.api.service.ProductService
import br.com.unopay.api.uaa.model.Group
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.uaa.service.GroupService
import br.com.unopay.api.uaa.service.UserDetailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
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
    private EventService eventService

    @Autowired
    private ContractService contractService

    @Autowired
    private PaymentInstrumentService paymentInstrumentService

    @Autowired
    private CreditPaymentAccountService creditPaymentAccountService

    @Autowired
    private ServiceService serviceService

    @Autowired
    private ContractorInstrumentCreditService contractorInstrumentCreditService

    @Autowired
    private UserDetailService userDetailService

    @Autowired
    private GroupService groupService

    @Autowired
    private PasswordEncoder passwordEncoder


    UserDetail createEstablishmentUser(establishmentUnderTest = createEstablishment()){
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        user.with {
            establishment = establishmentUnderTest
        }
        createUser(user)
    }

    UserDetail createUser(user = Fixture.from(UserDetail.class).gimme("without-group")){
        Group group = Fixture.from(Group.class).gimme("valid")
        groupService.create(group)
        user.addToMyGroups(group)
        userDetailService.create(user)
    }


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
        PaymentInstrument paymentInstrument =  Fixture.from(PaymentInstrument.class).gimme(label)
        paymentInstrument.with {
            product = createProduct()
            contractor = createContractor()
            it
        }
    }

    PaymentInstrument createPaymentInstrumentWithProduct(
            Product productUnderTest = createProduct(),
            contractorUnderTest = createContractor(), String number = UUID.randomUUID()) {
        PaymentInstrument paymentInstrument =  Fixture.from(PaymentInstrument.class).gimme("valid")
        paymentInstrument.with {
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
    Contractor createContractor(String label = "valid") {
        Contractor contractor = Fixture.from(Contractor.class).gimme(label)
        contractorService.create(contractor)
    }

    Event createEvent(ServiceType serviceType) {
        Event event = Fixture.from(Event.class).gimme("valid")
        Service serviceUnderTest = Fixture.from(Service.class).gimme("valid").with { type = serviceType; it }
        serviceService.create(serviceUnderTest)
        event.with {
            service = serviceUnderTest
            ncmCode = UUID.randomUUID()
        }
        eventService.create(event)
    }
    Product createProduct(code = null, PaymentRuleGroup paymentRuleGroupUnderTest = createPaymentRuleGroup(),
                          Set<CreditInsertionType> insertionTypes = Arrays.asList(CreditInsertionType.values())) {
        Product product = Fixture.from(Product.class).gimme("valid")
        product = product.with {
            issuer = createIssuer()
            accreditedNetwork = createNetwork()
            setPaymentRuleGroup (paymentRuleGroupUnderTest)
            serviceTypes =  [ServiceType.FUEL_ALLOWANCE, ServiceType.FREIGHT_RECEIPT]
            creditInsertionTypes = insertionTypes
            it
        }
        if(code){
            product.code = code
        }
        productService.save(product)
    }

    Product createProductWithCreditInsertionType(creditTypes ) {
        Product product = Fixture.from(Product.class).gimme("valid")
        product = product.with {
            issuer = createIssuer()
            accreditedNetwork = createNetwork()
            paymentRuleGroup =  createPaymentRuleGroup()
            serviceTypes = [ServiceType.FUEL_ALLOWANCE, ServiceType.FREIGHT_RECEIPT]
            creditInsertionTypes = creditTypes
            it
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
        if(knownProduct?.creditInsertionTypes)
            credit.creditInsertionType = knownProduct?.creditInsertionTypes?.first()
        credit
    }

    Credit createCredit(){
        def product = createProduct()
        createCredit(product)
    }

    Contract createContract(contractorUnderTest = createContractor(),
                            Product productUnderTest = createProduct(), hirerUnderTest = createHirer()) {
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceTypes
            it }
    }

    Contract createPersistedContract(contractorUnderTest = createContractor(),
                                     Product productUnderTest = createProduct(),
                                     hirer = createHirer(), situationUnderTest = ContractSituation.ACTIVE){
        Contract contract = createContract(contractorUnderTest, productUnderTest, hirer)
        contract.with {
            situation = situationUnderTest
        }
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

    public List addContractsToEstablishment(Establishment establishmentUnderTest, ContractorInstrumentCredit instrumentCreditUnderTest) {
        ContractEstablishment contractEstablishment = Fixture.from(ContractEstablishment.class).gimme("valid")
        contractEstablishment.with { establishment = establishmentUnderTest }
        def contractA = createPersistedContract(createContractor(), instrumentCreditUnderTest.contract.product)
        def contractB = createPersistedContract(createContractor(), instrumentCreditUnderTest.contract.product)
        contractService.addEstablishments(contractA.id, contractEstablishment)
        contractService.addEstablishments(contractB.id, contractEstablishment.with {id = null; it })
        [contractB, contractA]
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

    ContractorInstrumentCredit createContractorInstrumentCredit(Contractor contractorUnderTest = createContractor(),
                                                                Contract contractUnderTest = null){
        if(!contractUnderTest) {
            contractUnderTest = createPersistedContract(contractorUnderTest)
        }
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

    ServiceAuthorize createServiceAuthorize(){
        def instrumentCreditUnderTest = createContractorInstrumentCredit()
        encodeInstrumentPassword(instrumentCreditUnderTest)
        def password = instrumentCreditUnderTest.paymentInstrument.password
        contractorInstrumentCreditService.insert(instrumentCreditUnderTest.paymentInstrumentId, instrumentCreditUnderTest)
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid")
        serviceAuthorize.with {
            contract = instrumentCreditUnderTest.contract
            contractor = instrumentCreditUnderTest.contract.contractor
            event = createEvent(ServiceType.FUEL_ALLOWANCE)
            serviceType = ServiceType.FUEL_ALLOWANCE
            user = createUser()
            contractorInstrumentCredit = instrumentCreditUnderTest
            establishment = createEstablishment()
            contractorInstrumentCredit.paymentInstrument.password = password
            it
        }
    }

    void encodeInstrumentPassword(ContractorInstrumentCredit instrumentCredit) {
        def instrumentPasswordUnderTest = instrumentCredit.paymentInstrument.password
        instrumentCredit.paymentInstrument.with {
            password = passwordEncoder.encode(instrumentPasswordUnderTest)
        }
        paymentInstrumentService.update(instrumentCredit.paymentInstrumentId,
                instrumentCredit.paymentInstrument)
        instrumentCredit.paymentInstrument.with {
            password = instrumentPasswordUnderTest
        }
    }

    Establishment createEstablishment() {
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        accreditedNetworkService.create(establishment.network)
        establishmentService.create(establishment)
    }
}
