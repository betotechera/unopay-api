package br.com.unopay.api.bacen.util

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.six2six.fixturefactory.function.impl.RegexFunction
import br.com.unopay.api.JpaProcessor
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.EstablishmentEvent
import br.com.unopay.api.bacen.model.Event
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.model.HirerNegotiation
import br.com.unopay.api.bacen.model.Institution
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.model.Service
import br.com.unopay.api.bacen.model.ServiceType
import br.com.unopay.api.credit.model.ContractorInstrumentCredit
import br.com.unopay.api.credit.model.Credit
import br.com.unopay.api.credit.model.CreditInsertionType
import br.com.unopay.api.credit.model.CreditPaymentAccount
import br.com.unopay.api.credit.model.InstrumentBalance
import br.com.unopay.api.model.BatchClosing
import br.com.unopay.api.model.BatchClosingItem
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractEstablishment
import br.com.unopay.api.model.ContractInstallment
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.DocumentSituation
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.Person
import br.com.unopay.api.model.Product
import br.com.unopay.api.model.ServiceAuthorize
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.order.model.OrderStatus
import br.com.unopay.api.order.model.OrderType
import br.com.unopay.api.uaa.model.UserDetail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class FixtureCreator {

    @Autowired
    private PasswordEncoder passwordEncoder

    @Autowired
    private JpaProcessor jpaProcessor

    UserDetail createEstablishmentUser(establishmentUnderTest = createEstablishment()) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        UserDetail user = Fixture.from(UserDetail.class).uses(jpaProcessor).gimme("without-group", new Rule() {
            {
                add("establishment", establishmentUnderTest)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.with { password = generatePassword; it }
    }

    UserDetail createInstitutionUser(institution = createInstitution()) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        UserDetail user = Fixture.from(UserDetail.class).uses(jpaProcessor).gimme("with-group", new Rule() {
            {
                add("institution", institution)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.with { password = generatePassword; it }
    }

    UserDetail createHirerUser(hirer = createHirer()) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        UserDetail user = Fixture.from(UserDetail.class).uses(jpaProcessor).gimme("with-group", new Rule() {
            {
                add("hirer", hirer)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.with { password = generatePassword; it }
    }

    UserDetail createAccreditedNetworkUser(network = createNetwork()) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        UserDetail user = Fixture.from(UserDetail.class).uses(jpaProcessor).gimme("with-group", new Rule() {
            {
                add("accreditedNetwork", network)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.with { password = generatePassword; it }
    }

    UserDetail createIssuerUser(issuerUnderTest = createIssuer()) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        UserDetail user = Fixture.from(UserDetail.class).uses(jpaProcessor).gimme("without-group", new Rule() {
            {
                add("issuer", issuerUnderTest)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.with { password = generatePassword; it }
    }

    UserDetail createUser(String email = null) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        UserDetail user = Fixture.from(UserDetail.class).uses(jpaProcessor).gimme("with-group", new Rule() {
            {
                add("password", passwordEncoder.encode(generatePassword))
                if (!email) {
                    add("email", '${name}@gmail.com')
                } else {
                    add("email", email)
                }
            }
        })
        user.with { password = generatePassword; it }
    }

    PaymentInstrument createPaymentInstrument(String label = "valid") {
        Fixture.from(PaymentInstrument.class).gimme(label, new Rule() {
            {
                add("contractor", createContractor())
                add("product", createProduct())
            }
        })
    }

    PaymentInstrument createInstrumentToProduct(Product product = createProduct(), contractor = createContractor()) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        PaymentInstrument inst = Fixture.from(PaymentInstrument.class).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("product", product)
                add("contractor", contractor)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        inst.with { password = generatePassword; it }
    }

    Credit createCredit(Product product = createProduct()) {
        def hirer = createHirer()
        def issuer = createIssuer()
        Fixture.from(Credit.class).gimme("allFields", new Rule() {
            {
                add("hirer", hirer)
                add("product", product)
                add("issuer", product ? product.getIssuer() : issuer)
                if (product?.creditInsertionTypes) {
                    add("creditInsertionType", product?.creditInsertionTypes?.find())
                }
            }
        })
    }

    Contract createContract(contractorUnderTest = createContractor(),
                            Product productUnderTest = createProduct(), hirerUnderTest = createHirer()) {
        Fixture.from(Contract.class).gimme("valid", new Rule() {
            {
                add("hirer", hirerUnderTest)
                add("contractor", contractorUnderTest)
                add("product", productUnderTest)
                add("serviceTypes", productUnderTest.serviceTypes)
            }
        })
    }

    Contract createPersistedContractWithMembershipFee(BigDecimal membershipFee){
        createPersistedContract(createContractor(), createProduct(),
                createHirer(), ContractSituation.ACTIVE, membershipFee)
    }

    Contract createPersistedContract(contractor = createContractor(), Product product = createProduct(),
                                     hirer = createHirer(), situation = ContractSituation.ACTIVE,
                                     BigDecimal membershipFee = (Math.random() * 100)) {
        Fixture.from(Contract.class).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("hirer", hirer)
                add("contractor", contractor)
                add("product", product)
                add("serviceTypes", product.serviceTypes)
                add("situation", situation)
                add("membershipFee", membershipFee)
            }
        })
    }

    List addContractsToEstablishment(Establishment establishment, Product product) {
        def contractA = createPersistedContract(createContractor(), product)
        def contractB = createPersistedContract(createContractor(), product)
        Fixture.from(ContractEstablishment.class).uses(jpaProcessor).gimme(2, "valid", new Rule() {
            {
                add("establishment", establishment)
                add("contract", uniqueRandom(contractA, contractB))
            }
        })
        [contractB, contractA]
    }

    CreditPaymentAccount createCreditPaymentAccountFromContract(Contract contract = createContract()) {
        Fixture.from(CreditPaymentAccount.class).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("product", contract.product)
                add("issuer", contract.product.issuer)
                add("hirerDocument", contract.hirer.documentNumber)
                add("paymentRuleGroup", contract.product.paymentRuleGroup)
                add("serviceType", contract.serviceTypes.find())
            }
        })
    }

    CreditPaymentAccount createCreditPaymentAccount(String hirerDocument, Product product = createProduct()) {
        Fixture.from(CreditPaymentAccount.class).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("product", product)
                add("issuer", product.issuer)
                add("hirerDocument", hirerDocument)
                add("paymentRuleGroup", product.paymentRuleGroup)
                add("serviceType", product.serviceTypes.find())
            }
        })
    }

    ContractorInstrumentCredit instrumentCredit(Contractor contractor = createContractor(), Contract contract = null) {
        contract = contract ?: createPersistedContract(contractor)
        def creditPaymentAccountUnderTest = createCreditPaymentAccountFromContract(contract)
        Fixture.from(ContractorInstrumentCredit.class).gimme("toPersist", new Rule() {
            {
                add("paymentInstrument", createInstrumentToProduct(contract.product, contractor))
                add("creditPaymentAccount", creditPaymentAccountUnderTest)
                add("serviceType", contract.serviceTypes.find())
                add("value", creditPaymentAccountUnderTest.availableBalance)
                add("contract", contract)
            }
        })
    }

    ContractorInstrumentCredit createInstrumentToContract(Contract contract) {
        PaymentInstrument paymentInstrument = createInstrumentToProduct(contract.product, contract.contractor)
        CreditPaymentAccount paymentAccount = Fixture.from(CreditPaymentAccount.class)
                .uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("hirerDocument", contract.hirerDocumentNumber())
                add("product", contract.product)
            }
        })
        ContractorInstrumentCredit instrumentCredit = Fixture.from(ContractorInstrumentCredit.class)
                .uses(jpaProcessor).gimme("allFields", new Rule() {
            {
                add("contract", contract)
                add("paymentInstrument", paymentInstrument)
                add("creditPaymentAccount", paymentAccount)
            }
        })
        instrumentCredit
    }


    ContractorInstrumentCredit createContractorInstrumentCreditPersisted(Contract contractUnderTest = createPersistedContract()) {
        def creditPaymentAccountUnderTest = createCreditPaymentAccountFromContract(contractUnderTest)
        Fixture.from(ContractorInstrumentCredit.class).uses(jpaProcessor).gimme("toPersist", new Rule() {
            {
                add("paymentInstrument", createInstrumentToProduct(contractUnderTest.product, contractUnderTest.contractor))
                add("creditPaymentAccount", creditPaymentAccountUnderTest)
                add("serviceType", contractUnderTest.serviceTypes.find())
                add("value", creditPaymentAccountUnderTest.availableBalance)
                add("contract", contractUnderTest)
            }
        })
    }

    ServiceAuthorize createServiceAuthorize(ContractorInstrumentCredit credit = createContractorInstrumentCreditPersisted(),
                                            Establishment establishment = createEstablishment(), String dateAsText = "1 day ago") {
        Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule() {
            {
                def establishmentEvent = createEstablishmentEvent(establishment)
                createInstrumenBalance(credit.paymentInstrument, establishmentEvent.value)
                add("contract", credit.contract)
                add("contractor", credit.contract.contractor)
                add("establishmentEvent", establishmentEvent)
                add("event", establishmentEvent.event)
                add("serviceType", ServiceType.FUEL_ALLOWANCE)
                add("eventValue", 0.1)
                add("user", createUser())
                add("authorizationDateTime", instant(dateAsText))
                add("paymentInstrument", credit.paymentInstrument)
                add("establishment", establishment)
                add("paymentInstrument.password", credit.paymentInstrument.password)
            }
        })
    }

    ServiceAuthorize createServiceAuthorizePersisted(ContractorInstrumentCredit credit = createContractorInstrumentCreditPersisted(),
                                                     Establishment establishment = createEstablishment(), String dateAsText = "1 day ago") {
        Fixture.from(ServiceAuthorize.class).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("contract", credit.contract)
                add("contractor", credit.contract.contractor)
                add("event", createEvent(ServiceType.FUEL_ALLOWANCE))
                add("serviceType", ServiceType.FUEL_ALLOWANCE)
                add("eventValue", 0.1)
                add("user", createUser())
                add("authorizationDateTime", instant(dateAsText))
                add("paymentInstrument", credit.paymentInstrument)
                add("establishment", establishment)
                add("batchClosingDateTime", new Date())
                add("paymentInstrument.password", credit.paymentInstrument.password)
            }
        })
    }

    EstablishmentEvent createEstablishmentEvent(Establishment establishment = createEstablishment(),
                                                BigDecimal eventValue = null,
                                                Event event = createEvent(ServiceType.FUEL_ALLOWANCE)) {
        return Fixture.from(EstablishmentEvent.class).uses(jpaProcessor)
                .gimme("withoutReferences", new Rule() {
            {
                add("event", event)
                if (eventValue)
                    add("value", eventValue)
                add("establishment", establishment)
            }
        })
    }

    BatchClosing createBatchClosing() {
        Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid")
    }

    BatchClosing createBatchToPersist() {
        Fixture.from(BatchClosing.class).gimme("valid", new Rule() {
            {
                add("establishment", createEstablishment())
                add("issuer", createIssuer())
                add("accreditedNetwork", createNetwork())
                add("hirer", createHirer())
                add("payment", null)
            }
        })
    }

    Hirer createHirer() {
        Fixture.from(Hirer.class).uses(jpaProcessor).gimme("valid")
    }

    Contractor createContractor(String label = "valid") {
        Fixture.from(Contractor.class).uses(jpaProcessor).gimme(label)
    }

    InstrumentBalance createInstrumenBalance(PaymentInstrument instrument = createInstrumentToProduct(),
                                                                                            BigDecimal value = null) {
     return Fixture.from(InstrumentBalance.class).uses(jpaProcessor).gimme("valid", new Rule() {{
                add("paymentInstrument", instrument)
                if(value){
                    add("value", value)
                }
            }})
    }

    Event createEvent(ServiceType serviceType = ServiceType.FUEL_ALLOWANCE) {
        Service serviceUnderTest = Fixture.from(Service.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("type", serviceType)
        }})
        Fixture.from(Event.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("service", serviceUnderTest)
        }})
    }
    Product createProduct(PaymentRuleGroup paymentRuleGroupUnderTest = createPaymentRuleGroup(),
                          BigDecimal membershipFee = (Math.random() * 100)) {
        Fixture.from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("paymentRuleGroup", paymentRuleGroupUnderTest)
            add("membershipFee", membershipFee)
        }})
    }

    Product createProductWithIssuer(Issuer issuer = createIssuer()) {
        Fixture.from(Product.class).uses(jpaProcessor).gimme("valid", new Rule() {{
                add("issuer", issuer)
       }})
    }

    Product crateProductWithSameIssuerOfHirer(BigDecimal membershipFee = (Math.random() * 100)){
        def hirer = createHirer()
        Person issuerPerson = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical", new Rule(){{
            add("document.number", hirer.documentNumber)
        }})
        Issuer issuer = Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", issuerPerson)
        }})
        Product product = Fixture.from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issuer", issuer)
            add("membershipFee", membershipFee)
        }})
        createCreditPaymentAccount(hirer.documentNumber, product)
        product
    }

    Product createProductWithCreditInsertionType(creditInsertionTypes) {
        Fixture.from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("creditInsertionTypes",creditInsertionTypes)
        }})
    }

    List<BatchClosingItem> createBatchItems(batchClosing) {
        def serviceAuthorize = createServiceAuthorizePersisted()
        Fixture.from(BatchClosingItem.class).uses(jpaProcessor).gimme(2, "valid", new Rule() {
            {
                add("batchClosing", batchClosing)
                add("serviceAuthorize", serviceAuthorize)
                add("invoiceDocumentSituation", DocumentSituation.PENDING)
            }
        })
    }

    Order createOrder(Contract contract = createPersistedContract()){
        def contractor = createContractor("physical")
        def instrument = createInstrumentToProduct(contract.product, contractor)
        return Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", contract.product)
            add("contract", contract)
            add("type", OrderType.CREDIT)
            add("paymentInstrument", instrument)
            add("value", BigDecimal.ONE)
        }})
    }

    Order createPersistedOrderWithStatus(OrderStatus status, OrderType type = OrderType.CREDIT,
                                                 Contractor contractor = createContractor("physical")){
        return createPersistedPaidOrder(contractor, type, status)
    }

    Order createPersistedPaidOrder(Contractor contractor = createContractor("physical"),
                                           OrderType type = OrderType.CREDIT, OrderStatus status = OrderStatus.PAID){
        def product = createProduct()
        def user = createUser()
        def contract = createPersistedContract(contractor, product)
        Fixture.from(ContractInstallment.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("contract", contract)
            add("paymentDateTime", null)
        }})
        def instrument = createInstrumentToProduct(product, contractor)
        return Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("person.physicalPersonDetail.email", user.email)
            add("product", product)
            add("contract", contract)
            add("type", type)
            add("paymentInstrument", instrument)
            add("value", BigDecimal.ONE)
            add("status", status)
        }})
    }

    Order createPersistedAdhesionOrder(Person person){
        def product = crateProductWithSameIssuerOfHirer()
        return Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", person)
            add("product", product)
            add("type", OrderType.ADHESION)
            add("value", BigDecimal.ONE)
            add("status", OrderStatus.PAID)
        }})
    }


    HirerNegotiation createNegotiation(hirer = createHirer(), product = createProduct()){
        return Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
        }})
    }

    CreditPaymentAccount createCreditPaymentAccount() {
        Fixture.from(CreditPaymentAccount.class).uses(jpaProcessor).gimme("valid")
    }

    Product createProductWithOutDirectDebit() {
        Fixture.from(Product.class).uses(jpaProcessor).gimme("creditWithoutDirectDebit")
    }

    Establishment createEstablishment(AccreditedNetwork network = createNetwork()) {
        Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("network",network)
        }})
    }

    Establishment createHeadOffice() {
        Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
    }

    AccreditedNetwork createNetwork() {
        Fixture.from(AccreditedNetwork.class).uses(jpaProcessor).gimme("valid")
    }

    Service createService(Establishment establishment = createEstablishment()) {
        Fixture.from(Service.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("establishments", Arrays.asList(establishment))
        }})
    }

    Issuer createIssuer() {
        Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid")
    }

    Institution createInstitution() {
        Fixture.from(Institution.class).uses(jpaProcessor).gimme("valid")
    }

    PaymentRuleGroup createPaymentRuleGroup(Institution institution = createInstitution()) {
        Fixture.from(PaymentRuleGroup.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("institution", institution)
        }})
    }

    PaymentRuleGroup createPaymentRuleGroupDefault() {
        Fixture.from(PaymentRuleGroup.class).uses(jpaProcessor).gimme("default")
    }
}
