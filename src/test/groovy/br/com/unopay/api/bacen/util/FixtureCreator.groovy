package br.com.unopay.api.bacen.util

import br.com.six2six.fixturefactory.Fixture
import org.springframework.security.access.event.AuthorizedEvent

import static br.com.six2six.fixturefactory.Fixture.from
import br.com.six2six.fixturefactory.Rule
import br.com.six2six.fixturefactory.function.impl.RegexFunction
import br.com.unopay.api.JpaProcessor
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.model.Institution
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.billing.creditcard.model.PaymentMethod
import br.com.unopay.api.credit.model.ContractorInstrumentCredit
import br.com.unopay.api.credit.model.Credit
import br.com.unopay.api.credit.model.CreditPaymentAccount
import br.com.unopay.api.credit.model.InstrumentBalance
import br.com.unopay.api.function.FixtureFunctions
import br.com.unopay.api.market.model.AuthorizedMember
import br.com.unopay.api.market.model.AuthorizedMemberCandidate
import br.com.unopay.api.market.model.BonusBilling
import br.com.unopay.api.market.model.ContractorBonus
import br.com.unopay.api.market.model.HirerNegotiation
import br.com.unopay.api.market.model.PaymentDayCalculator
import br.com.unopay.api.model.BatchClosing
import br.com.unopay.api.model.BatchClosingItem
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractEstablishment
import br.com.unopay.api.model.ContractInstallment
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.DocumentSituation
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.PaymentInstrumentType
import br.com.unopay.api.model.Person
import br.com.unopay.api.model.Product
import br.com.unopay.api.model.ServiceAuthorize
import br.com.unopay.api.model.ServiceAuthorizeEvent
import br.com.unopay.api.network.model.AccreditedNetwork
import br.com.unopay.api.network.model.Branch
import br.com.unopay.api.network.model.Establishment
import br.com.unopay.api.network.model.EstablishmentEvent
import br.com.unopay.api.network.model.Event
import br.com.unopay.api.network.model.Service
import br.com.unopay.api.network.model.ServiceType
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.order.model.OrderType
import br.com.unopay.api.order.model.PaymentStatus
import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.uaa.model.UserDetail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class FixtureCreator {

    @Autowired
    private PasswordEncoder passwordEncoder

    @Autowired
    private PaymentDayCalculator paymentDayCalculator

    @Autowired
    private JpaProcessor jpaProcessor

    UserDetail createEstablishmentUser(establishmentUnderTest = createEstablishment()) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        UserDetail user = from(UserDetail.class).uses(jpaProcessor).gimme("without-group", new Rule() {
            {
                add("establishment", establishmentUnderTest)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.with { password = generatePassword; it }
    }

    UserDetail createInstitutionUser(institution = createInstitution()) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        UserDetail user = from(UserDetail.class).uses(jpaProcessor).gimme("with-group", new Rule() {
            {
                add("institution", institution)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.with { password = generatePassword; it }
    }

    UserDetail createHirerUser(hirer = createHirer()) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        UserDetail user = from(UserDetail.class).uses(jpaProcessor).gimme("with-group", new Rule() {
            {
                add("hirer", hirer)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.with { password = generatePassword; it }
    }

    UserDetail createAccreditedNetworkUser(network = createNetwork()) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        UserDetail user = from(UserDetail.class).uses(jpaProcessor).gimme("with-group", new Rule() {
            {
                add("accreditedNetwork", network)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.with { password = generatePassword; it }
    }

    UserDetail createIssuerUser(issuerUnderTest = createIssuer()) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        UserDetail user = from(UserDetail.class).uses(jpaProcessor).gimme("without-group", new Rule() {
            {
                add("issuer", issuerUnderTest)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.with { password = generatePassword; it }
    }

    UserDetail createContractorUser(contractorUnderTest = createContractor("valid")) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        UserDetail user = Fixture.from(UserDetail.class).uses(jpaProcessor).gimme("without-group", new Rule() {
            {
                add("contractor", contractorUnderTest)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.with { password = generatePassword; it }
    }

    UserDetail createUser(String email = null) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        UserDetail user = from(UserDetail.class).uses(jpaProcessor).gimme("with-group", new Rule() {
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
        from(PaymentInstrument.class).gimme(label, new Rule() {
            {
                add("contractor", createContractor())
                add("product", createProduct())
            }
        })
    }
    PaymentInstrument createPersistedInstrument(contractor = createContractor(),
                                                product = createProduct(), type = PaymentInstrumentType.DIGITAL_WALLET){

        from(PaymentInstrument.class).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("contractor", contractor)
                add("product", product)
                add("type", type)
            }
        })
    }

    PaymentInstrument createInstrumentToProduct(Product product = createProduct(), contractor = createContractor()) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        PaymentInstrument inst = from(PaymentInstrument.class).uses(jpaProcessor).gimme("valid", new Rule() {
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
        from(Credit.class).gimme("allFields", new Rule() {
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
        from(Contract.class).gimme("valid", new Rule() {
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
        from(Contract.class).uses(jpaProcessor).gimme("valid", new Rule() {
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

    Contract createPersistedContractWithProductIssuerAsHirer(hirer = createHirer(), contractor = createContractor(),
                                                             situation = ContractSituation.ACTIVE,
                                                             BigDecimal membershipFee = (Math.random() * 100)) {
        Product product = createProductWithSameIssuerOfHirer(membershipFee, hirer)

        from(Contract.class).uses(jpaProcessor).gimme("valid", new Rule() {
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
        from(ContractEstablishment.class).uses(jpaProcessor).gimme(2, "valid", new Rule() {
            {
                add("establishment", establishment)
                add("contract", uniqueRandom(contractA, contractB))
            }
        })
        [contractB, contractA]
    }

    CreditPaymentAccount createCreditPaymentAccountFromContract(Contract contract = createContract()) {
        from(CreditPaymentAccount.class).uses(jpaProcessor).gimme("valid", new Rule() {
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
        from(CreditPaymentAccount.class).uses(jpaProcessor).gimme("valid", new Rule() {
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
        from(ContractorInstrumentCredit.class).gimme("toPersist", new Rule() {
            {
                add("paymentInstrument", createInstrumentToProduct(contract.product, contractor))
                add("creditPaymentAccount", creditPaymentAccountUnderTest)
                add("serviceType", contract.serviceTypes.find())
                add("value", creditPaymentAccountUnderTest.availableBalance)
                add("contract", contract)
            }
        })
    }

    ContractorInstrumentCredit createInstrumentCreditToContract(Contract contract) {
        PaymentInstrument paymentInstrument = createInstrumentToProduct(contract.product, contract.contractor)
        CreditPaymentAccount paymentAccount = from(CreditPaymentAccount.class)
                .uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("hirerDocument", contract.hirerDocumentNumber())
                add("product", contract.product)
            }
        })
        ContractorInstrumentCredit instrumentCredit = from(ContractorInstrumentCredit.class)
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
        from(ContractorInstrumentCredit.class).uses(jpaProcessor).gimme("toPersist", new Rule() {
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

        def establishmentEvent = createEstablishmentEvent(establishment)
        ServiceAuthorize authorize = from(ServiceAuthorize.class).gimme("valid", new Rule() {{
                createInstrumenBalance(credit.paymentInstrument, establishmentEvent.value)
                add("contract", credit.contract)
                add("contractor", credit.contract.contractor)
                add("user", createUser())
                add("authorizationDateTime", instant(dateAsText))
                add("paymentInstrument", credit.paymentInstrument)
                add("establishment", establishment)
                add("paymentInstrument.password", credit.paymentInstrument.password)
            }})

        def authorizeEvent = from(ServiceAuthorizeEvent.class).uses(jpaProcessor).gimme(1,"valid", new Rule() {{
            add("establishmentEvent", establishmentEvent)
            add("event", establishmentEvent.event)
            add("serviceType", ServiceType.DOCTORS_APPOINTMENTS)
            add("eventValue", 0.1)
        }})
        authorize.authorizeEvents = authorizeEvent
        authorize
    }

    ServiceAuthorize createServiceAuthorizeByScheduling(Contract contract = createPersistedContract(),
                                                        PaymentInstrument paymentInstrument,
                                                        UserDetail user,
                                                        Establishment establishment = createEstablishment(), String dateAsText = "1 day ago") {

        def establishmentEvent = createEstablishmentEvent(establishment)
        ServiceAuthorize authorize = from(ServiceAuthorize.class).gimme("valid", new Rule() {{
            createInstrumenBalance(paymentInstrument, establishmentEvent.value)
            add("contract", contract)
            add("contractor", contract.contractor)
            add("user", user)
            add("authorizationDateTime", instant(dateAsText))
            add("paymentInstrument", paymentInstrument)
            add("authorizedMember", createPersistedAuthorizedMember(contract.contractor))
            add("establishment", establishment)
            add("paymentInstrument.password", paymentInstrument.password)
        }})

        def authorizeEvent = from(ServiceAuthorizeEvent.class).gimme(1,"valid", new Rule() {{
            add("establishmentEvent", establishmentEvent)
            add("event", establishmentEvent.event)
            add("serviceType", ServiceType.DOCTORS_APPOINTMENTS)
            add("eventValue", 0.1)
        }})
        authorize.authorizeEvents = authorizeEvent

        authorize
    }

    ServiceAuthorize createServiceAuthorizePersisted(ContractorInstrumentCredit credit = createContractorInstrumentCreditPersisted(),
                                                     Establishment establishment = createEstablishment(), String dateAsText = "1 day ago") {

        ServiceAuthorize authorize = from(ServiceAuthorize.class).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("contract", credit.contract)
                add("contractor", credit.contract.contractor)
                add("user", createUser())
                add("authorizationDateTime", instant(dateAsText))
                add("paymentInstrument", credit.paymentInstrument)
                add("establishment", establishment)
                add("batchClosingDateTime", new Date())
                add("paymentInstrument.password", credit.paymentInstrument.password)
            }
        })
        def authorizeEvent = from(ServiceAuthorizeEvent.class).uses(jpaProcessor).gimme(1, "valid", new Rule() {{
            def establishmentEvent = createEstablishmentEvent(establishment)
            add("establishmentEvent", establishmentEvent)
            add("serviceAuthorize", authorize)
            add("event", createEvent(ServiceType.DOCTORS_APPOINTMENTS))
            add("serviceType", ServiceType.DOCTORS_APPOINTMENTS)
            add("eventValue", 0.1)
        }})
        authorize.authorizeEvents = authorizeEvent
        authorize
    }

    EstablishmentEvent createEstablishmentEvent(Establishment establishment = createEstablishment(),
                                                BigDecimal eventValue = null,
                                                Event event = createEvent(ServiceType.DOCTORS_APPOINTMENTS)) {
        return from(EstablishmentEvent.class).uses(jpaProcessor)
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
        from(BatchClosing.class).uses(jpaProcessor).gimme("valid")
    }

    BatchClosing createBatchToPersist() {
        from(BatchClosing.class).gimme("valid", new Rule() {
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
        from(Hirer.class).uses(jpaProcessor).gimme("valid",  new Rule())
    }

    Contractor createContractor(String label = "valid") {
        from(Contractor.class).uses(jpaProcessor).gimme(label)
    }

    InstrumentBalance createInstrumenBalance(PaymentInstrument instrument = createInstrumentToProduct(),
                                                                                            BigDecimal value = null) {
     return from(InstrumentBalance.class).uses(jpaProcessor).gimme("valid", new Rule() {{
                add("paymentInstrument", instrument)
                if(value){
                    add("value", value)
                }
            }})
    }

    Event createEvent(ServiceType serviceType = ServiceType.DOCTORS_APPOINTMENTS, Boolean requestQuantity = false) {
        Service serviceUnderTest = from(Service.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("type", serviceType)
        }})
        from(Event.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("service", serviceUnderTest)
            add("requestQuantity", requestQuantity)
        }})
    }
    Product createProduct(PaymentRuleGroup paymentRuleGroupUnderTest = createPaymentRuleGroup(),
                          BigDecimal membershipFee = (Math.random() * 100)) {
        from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("paymentRuleGroup", paymentRuleGroupUnderTest)
            add("membershipFee", membershipFee)
        }})
    }

    Product createProductWithIssuer(Issuer issuer = createIssuer()) {
        from(Product.class).uses(jpaProcessor).gimme("valid", new Rule() {{
                add("issuer", issuer)
       }})
    }

    Product createProductWithSameIssuerOfHirer(BigDecimal membershipFee = (Math.random() * 100),hirer = createHirer()){
        Person issuerPerson = from(Person.class).uses(jpaProcessor).gimme("physical", new Rule(){{
            add("document.number", hirer.documentNumber)
        }})
        Issuer issuer = from(Issuer.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", issuerPerson)
        }})
        Product product = from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issuer", issuer)
            add("membershipFee", membershipFee)
        }})
        createCreditPaymentAccount(hirer.documentNumber, product)
        product
    }


    Product createProductPFWithMethods(List<PaymentMethod> methods){
        def hirerDocument = createHirer().documentNumber
        Person issuerPerson = from(Person.class).uses(jpaProcessor).gimme("physical", new Rule(){{
            add("document.number", hirerDocument)
        }})
        Issuer issuer = from(Issuer.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", issuerPerson)
        }})
        Product product = from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issuer", issuer)
            add("paymentMethods", methods)
        }})
        createCreditPaymentAccount(hirerDocument, product)
        product
    }

    Hirer createHirerWithDocument(String document) {
        Person hirerPerson = from(Person.class).uses(jpaProcessor).gimme("physical", new Rule() {{
                if (document) {
                    add("document.number", document)
                }
        }})
        Hirer hirer = from(Hirer.class).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("person", hirerPerson)
            }
        })
        hirer
    }

    Product createProductWithCreditInsertionType(creditInsertionTypes) {
        from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("creditInsertionTypes",creditInsertionTypes)
        }})
    }

    List<BatchClosingItem> createBatchItems(batchClosing) {
        def serviceAuthorize = createServiceAuthorizePersisted()
        from(BatchClosingItem.class).uses(jpaProcessor).gimme(2, "valid", new Rule() {
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
        return from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", contract.product)
            add("contract", contract)
            add("type", OrderType.CREDIT)
            add("paymentInstrument", instrument)
            add("value", BigDecimal.ONE)
        }})
    }

    Order createPersistedOrderWithStatus(PaymentStatus status, OrderType type = OrderType.CREDIT,
                                         Contractor contractor = createContractor("physical")){
        return createPersistedPaidOrder(contractor, type, status)
    }

    Order createPersistedPaidOrder(Contractor contractor = createContractor("physical"),
                                   OrderType type = OrderType.CREDIT, PaymentStatus status = PaymentStatus.PAID){
        def product = createProduct()
        def user = createUser()
        def contract = createPersistedContract(contractor, product)
        from(ContractInstallment.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("contract", contract)
            add("paymentDateTime", null)
        }})
        def instrument = createInstrumentToProduct(product, contractor)
        return from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
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
        def product = createProductWithSameIssuerOfHirer()
        return from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", person)
            add("product", product)
            add("type", OrderType.ADHESION)
            add("value", BigDecimal.ONE)
            add("status", PaymentStatus.PAID)
        }})
    }


    HirerNegotiation createNegotiation(hirer = createHirer(), product = createProduct(),
                                       Date effectiveDate = FixtureFunctions.instant("one day ago")){
        return from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
            add("effectiveDate", effectiveDate)
            add("freeInstallmentQuantity", 0)
            add("billingWithCredits", Boolean.TRUE)
            add("active", Boolean.TRUE)
            add("paymentDay", paymentDayCalculator.nearDay)
        }})
    }

    CreditPaymentAccount createCreditPaymentAccount() {
        from(CreditPaymentAccount.class).uses(jpaProcessor).gimme("valid")
    }

    Product createProductWithOutDirectDebit() {
        from(Product.class).uses(jpaProcessor).gimme("creditWithoutDirectDebit")
    }

    Establishment createEstablishment(AccreditedNetwork network = createNetwork()) {
        from(Establishment.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("network",network)
        }})
    }

    Branch createBranch(Establishment headOffice = createEstablishment()) {
        from(Branch.class).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("headOffice", headOffice)
        }})
    }

    Branch createBranchForContract(Contract contract = createContract()) {
        def network = contract.getProduct().accreditedNetwork
        def establishment = createEstablishment(network)
        createBranch(establishment)
    }

    Establishment createHeadOffice() {
        from(Establishment.class).uses(jpaProcessor).gimme("valid")
    }

    AccreditedNetwork createNetwork() {
        from(AccreditedNetwork.class).uses(jpaProcessor).gimme("valid")
    }

    Service createService(Establishment establishment = createEstablishment()) {
        from(Service.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("establishments", Arrays.asList(establishment))
        }})
    }

    Issuer createIssuer() {
        from(Issuer.class).uses(jpaProcessor).gimme("valid")
    }

    Institution createInstitution() {
        from(Institution.class).uses(jpaProcessor).gimme("valid")
    }

    PaymentRuleGroup createPaymentRuleGroup(Institution institution = createInstitution()) {
        from(PaymentRuleGroup.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("institution", institution)
        }})
    }

    PaymentRuleGroup createPaymentRuleGroupDefault() {
        from(PaymentRuleGroup.class).uses(jpaProcessor).gimme("default")
    }

    AuthorizedMember createPersistedAuthorizedMember(contractor = createContractor("valid")) {
        def contract = createPersistedContract(contractor)
        from(AuthorizedMember.class).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("paymentInstrument", createInstrumentToProduct(createProduct(), contract.contractor))
            add("contract", contract)
        }})
    }

    AuthorizedMember createAuthorizedMemberForContract(Contract contract) {
         from(AuthorizedMember.class).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("paymentInstrument", createInstrumentToProduct(contract.product, contract.contractor))
            add("contract", contract)
        }})
    }

    AuthorizedMember createAuthorizedMemberToPersist(Contract contract = createPersistedContract()) {
        from(AuthorizedMember.class).gimme("valid", new Rule() {{
            add("paymentInstrument", createInstrumentToProduct(createProduct(), contract.contractor))
            add("contract", contract)
        }})
    }

    AuthorizedMemberCandidate createAuthorizedMemberCandidateToPersist() {
        def order = createPersistedAdhesionOrder(createContractor().person)
        from(AuthorizedMemberCandidate.class).gimme("valid", new Rule() {{
            add("order", order)
        }})
    }

    AuthorizedMemberCandidate createAuthorizedMemberCandidateForOrder(Order order = createPersistedAdhesionOrder(createContractor().person)) {
        from(AuthorizedMemberCandidate.class).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("order", order)
        }})
    }

    BonusBilling createBonusBillingToPersist() {
        def person = from(Person.class).uses(jpaProcessor).gimme("physical")
        from(BonusBilling).gimme("valid", new Rule() {{
            add("person", person)
        }})
    }

    BonusBilling createPersistedBonusBilling(person = from(Person.class).uses(jpaProcessor).gimme("physical"),
                                             issuer = createIssuer()) {
        from(BonusBilling).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("payer", person)
                add("issuer", issuer)
            }})
    }

    ContractorBonus createPersistedContractorBonusForContractor(Contractor contractor = createContractor(), person = from(Person.class).uses(jpaProcessor).gimme("physical"), product = createProduct()) {
        from(ContractorBonus.class).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("contractor", contractor)
            add("product", product)
            add("payer", person)
        }})
    }

    ContractorBonus createPersistedContractorBonusWithProduct(product = createProduct()) {
        def contractor = createContractor()
        from(ContractorBonus.class).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("contractor", contractor)
            add("product", product)
            add("payer", contractor.person)
        }})
    }

    ContractorBonus createPersistedContractorBonusForPerson(Person person) {
        from(ContractorBonus.class).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("contractor", createContractor())
            add("product", createProduct())
            add("payer", person)
        }})
    }

    Scheduling createSchedulingToPersist(Contract contract = createPersistedContract(),
                                  UserDetail userDetail = createUser()) {
        from(Scheduling.class).gimme("valid", ruleSchedulingValid(contract, userDetail))
    }

    Scheduling createSchedulingPersisted(Contract contract = createPersistedContract(),
                                         UserDetail userDetail  = createUser()) {
        from(Scheduling.class).uses(jpaProcessor).gimme("valid", ruleSchedulingValid(contract, userDetail))
    }

    private def ruleSchedulingValid(Contract contract, UserDetail userDetail) {
        def branch = createBranchForContract(contract)
        new Rule() {{
            add("branch", branch)
            add("contract", contract)
            add("contractor", contract.contractor)
            add("paymentInstrument", createInstrumentToProduct(contract.product, contract.contractor))
            add("user", userDetail)
            add("authorizedMember", createPersistedAuthorizedMember())
        }}
    }
}
