package br.com.unopay.api.util

import java.util.Date

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Fixture.from
import br.com.six2six.fixturefactory.Rule
import br.com.six2six.fixturefactory.function.impl.{ChronicFunction, RegexFunction}
import br.com.unopay.api.bacen.model.{Establishment, PaymentRuleGroup, Service, _}
import br.com.unopay.api.credit.model._
import br.com.unopay.api.market.model.{AuthorizedMember, AuthorizedMemberCandidate, _}
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
import br.com.unopay.api.model.ServiceAuthorizeEvent
import br.com.unopay.api.order.model.{Order, OrderType, PaymentStatus}
import br.com.unopay.api.uaa.model.UserDetail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._

@Component
@Autowired
class FixtureCreatorScala(passwordEncoder: PasswordEncoder,
                          paymentDayCalculator: PaymentDayCalculator,
                          jpaProcessor: JpaProcessorScala) {


    def createEstablishmentUser(establishmentUnderTest: Establishment = createEstablishment()): UserDetail = {
        val generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        val user: UserDetail = from(classOf[UserDetail]).uses(jpaProcessor).gimme("without-group", new Rule() {
            {
                add("establishment", establishmentUnderTest)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.setPassword(generatePassword)
        user
    }

    def createInstitutionUser(institution: Institution = createInstitution()): UserDetail = {
        val generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        val user: UserDetail = from(classOf[UserDetail]).uses(jpaProcessor).gimme("with-group", new Rule() {
            {
                add("institution", institution)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.setPassword(generatePassword)
        user
    }

    def createHirerUser(hirer: Hirer = createHirer()): UserDetail = {
        val generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        val user: UserDetail = from(classOf[UserDetail]).uses(jpaProcessor).gimme("with-group", new Rule() {
            {
                add("hirer", hirer)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.setPassword(generatePassword)
        user
    }

    def createAccreditedNetworkUser(network: AccreditedNetwork = createNetwork()): UserDetail = {
        val generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        val user: UserDetail = from(classOf[UserDetail]).uses(jpaProcessor).gimme("with-group", new Rule() {
            {
                add("accreditedNetwork", network)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.setPassword(generatePassword)
        user
    }

    def createIssuerUser(issuerUnderTest: Issuer = createIssuer()): UserDetail = {
        val generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        val user: UserDetail = from(classOf[UserDetail]).uses(jpaProcessor).gimme("without-group", new Rule() {
            {
                add("issuer", issuerUnderTest)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.setPassword(generatePassword)
        user
    }

    def createContractorUser(contractorUnderTest: Contractor = createContractor("valid")): UserDetail = {
        val generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        val user: UserDetail = Fixture.from(classOf[UserDetail]).uses(jpaProcessor).gimme("without-group", new Rule() {
            {
                add("contractor", contractorUnderTest)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        user.setPassword(generatePassword)
        user
    }

    def createUser(email: String = null): UserDetail = {
        val generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        val user: UserDetail = from(classOf[UserDetail]).uses(jpaProcessor).gimme("with-group", new Rule() {
            {
                add("password", passwordEncoder.encode(generatePassword))
                if (email == null) {
                    add("email", "${name}@gmail.com")
                } else {
                    add("email", email)
                }
            }
        })
        user.setPassword(generatePassword)
        user
    }

    def createPaymentInstrument(label: String = "valid"): PaymentInstrument = {
        from(classOf[PaymentInstrument]).gimme(label, new Rule() {
            {
                add("contractor", createContractor())
                add("product", createProduct())
            }
        })
    }

    def createInstrumentToProduct(product: Product = createProduct(), contractor: Contractor = createContractor()): PaymentInstrument = {
        val generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        val inst: PaymentInstrument = from(classOf[PaymentInstrument]).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("product", product)
                add("contractor", contractor)
                add("password", passwordEncoder.encode(generatePassword))
            }
        })
        inst.setPassword(generatePassword)
        inst
    }

    def createCredit(product: Product = createProduct()): Credit = {
        val hirer = createHirer()
        val issuer = createIssuer()
        from(classOf[Credit]).gimme("allFields", new Rule() {
            {
                add("hirer", hirer)
                add("product", product)
                add("issuer", if(product != null) product.getIssuer() else issuer)
                if (product != null && !product.getCreditInsertionTypes.isEmpty) {
                    add("creditInsertionType", product.getCreditInsertionTypes.asScala.head)
                }
            }
        })
    }

    def createContract(contractorUnderTest: Contractor = createContractor(),
                             productUnderTest: Product = createProduct(), hirerUnderTest: Hirer = createHirer()): Contract = {
        from(classOf[Contract]).gimme("valid", new Rule() {
            {
                add("hirer", hirerUnderTest)
                add("contractor", contractorUnderTest)
                add("product", productUnderTest)
                add("serviceTypes", productUnderTest.getServiceTypes)
            }
        })
    }

    def createPersistedContractWithMembershipFee(membershipFee: java.math.BigDecimal){
        createPersistedContract(createContractor(), createProduct(),
                createHirer(), ContractSituation.ACTIVE, membershipFee)
    }

    def createPersistedContract(contractor: Contractor = createContractor(), product: Product = createProduct(),
                                     hirer: Hirer = createHirer(), situation: ContractSituation = ContractSituation.ACTIVE,
                                     membershipFee: java.math.BigDecimal = randomBigDecimal()) : Contract = {
        from(classOf[Contract]).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("hirer", hirer)
                add("contractor", contractor)
                add("product", product)
                add("serviceTypes", product.getServiceTypes)
                add("situation", situation)
                add("membershipFee", membershipFee)
            }
        }).asInstanceOf[Contract]
    }

    def addContractsToEstablishment(establishment: Establishment, product: Product): List[Contract] = {
        val contractA = createPersistedContract(createContractor(), product)
        val contractB = createPersistedContract(createContractor(), product)
        from(classOf[ContractEstablishment]).uses(jpaProcessor).gimme(2, "valid", new Rule() {
            {
                add("establishment", establishment)
                add("contract", uniqueRandom(contractA, contractB))
            }
        })
        List(contractB, contractA)
    }

    def createCreditPaymentAccountFromContract(contract: Contract = createContract()): CreditPaymentAccount = {
        from(classOf[CreditPaymentAccount]).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("product", contract.getProduct)
                add("issuer", contract.getProduct.getIssuer)
                add("hirerDocument", contract.getHirer.getDocumentNumber)
                add("paymentRuleGroup", contract.getProduct.getPaymentRuleGroup)
                add("serviceType", contract.getServiceTypes.asScala.head)
            }
        })
    }

    def createCreditPaymentAccount(hirerDocument: String, product: Product = createProduct()): CreditPaymentAccount = {
        from(classOf[CreditPaymentAccount]).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("product", product)
                add("issuer", product.getIssuer)
                add("hirerDocument", hirerDocument)
                add("paymentRuleGroup", product.getPaymentRuleGroup)
                add("serviceType", product.getServiceTypes.asScala.head)
            }
        })
    }

    def instrumentCredit(contractor: Contractor = createContractor(), contract: Contract = null): ContractorInstrumentCredit = {
        val newContract: Contract = if(contract != null) contract else createPersistedContract(contractor)
        val creditPaymentAccountUnderTest = createCreditPaymentAccountFromContract(contract)
        from(classOf[ContractorInstrumentCredit]).gimme("toPersist", new Rule() {
            {
                add("paymentInstrument", createInstrumentToProduct(newContract.getProduct, contractor))
                add("creditPaymentAccount", creditPaymentAccountUnderTest)
                add("serviceType", newContract.getServiceTypes.asScala.head)
                add("value", creditPaymentAccountUnderTest.getAvailableBalance)
                add("contract", newContract)
            }
        })
    }

    def createInstrumentToContract(contract: Contract): ContractorInstrumentCredit = {
        val paymentInstrument = createInstrumentToProduct(contract.getProduct, contract.getContractor)
        val paymentAccount: CreditPaymentAccount = from(classOf[CreditPaymentAccount])
                .uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("hirerDocument", contract.hirerDocumentNumber())
                add("product", contract.getProduct)
            }
        })
        from(classOf[ContractorInstrumentCredit])
                .uses(jpaProcessor).gimme("allFields", new Rule() {
            {
                add("contract", contract)
                add("paymentInstrument", paymentInstrument)
                add("creditPaymentAccount", paymentAccount)
            }
        })
    }


    def createContractorInstrumentCreditPersisted(contractUnderTest: Contract = createPersistedContract()): ContractorInstrumentCredit = {
        val creditPaymentAccountUnderTest = createCreditPaymentAccountFromContract(contractUnderTest)
        from(classOf[ContractorInstrumentCredit]).uses(jpaProcessor).gimme("toPersist", new Rule() {
            {
                add("paymentInstrument", createInstrumentToProduct(contractUnderTest.getProduct, contractUnderTest.getContractor))
                add("creditPaymentAccount", creditPaymentAccountUnderTest)
                add("serviceType", contractUnderTest.getServiceTypes.asScala.head)
                add("value", creditPaymentAccountUnderTest.getAvailableBalance)
                add("contract", contractUnderTest)
            }
        })
    }

    def createServiceAuthorize(credit: ContractorInstrumentCredit = createContractorInstrumentCreditPersisted(),
                                            establishment: Establishment = createEstablishment(), dateAsText: String = "1 day ago"): ServiceAuthorize =  {

        val establishmentEvent: EstablishmentEvent = createEstablishmentEvent(establishment)
        val authorize: ServiceAuthorize = from(classOf[ServiceAuthorize]).gimme("valid", new Rule() {{
                createInstrumenBalance(credit.getPaymentInstrument, establishmentEvent.getValue)
                add("contract", credit.getContract)
                add("contractor", credit.getContract.getContractor)
                add("user", createUser())
                add("authorizationDateTime", instant(dateAsText))
                add("paymentInstrument", credit.getPaymentInstrument)
                add("establishment", establishment)
                add("paymentInstrument.password", credit.getPaymentInstrument.getPassword)
            }})

        val authorizeEvent = from(classOf[ServiceAuthorizeEvent]).uses(jpaProcessor).gimme(1,"valid", new Rule() {{
            add("establishmentEvent", establishmentEvent)
            add("event", establishmentEvent.getEvent)
            add("serviceType", ServiceType.DOCTORS_APPOINTMENTS)
            add("eventValue", 0.1)
        }}).asInstanceOf[java.util.List[ServiceAuthorizeEvent]]
        authorize.setAuthorizeEvents(authorizeEvent)
        authorize
    }

    def createServiceAuthorizePersisted(credit: ContractorInstrumentCredit = createContractorInstrumentCreditPersisted(),
                                                     establishment: Establishment = createEstablishment(),
                                        dateAsText: String = "1 day ago"): ServiceAuthorize = {

        val authorize: ServiceAuthorize = from(classOf[ServiceAuthorize]).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("contract", credit.getContract)
                add("contractor", credit.getContract.getContractor)
                add("user", createUser())
                add("authorizationDateTime", instant(dateAsText))
                add("paymentInstrument", credit.getPaymentInstrument)
                add("establishment", establishment)
                add("batchClosingDateTime", new Date())
                add("paymentInstrument.password", credit.getPaymentInstrument.getPassword)
            }
        })
        val authorizeEvent = from(classOf[ServiceAuthorizeEvent]).uses(jpaProcessor).gimme(1, "valid", new Rule() {{
            val establishmentEvent = createEstablishmentEvent(establishment)
            add("establishmentEvent", establishmentEvent)
            add("serviceAuthorize", authorize)
            add("event", createEvent(ServiceType.DOCTORS_APPOINTMENTS))
            add("serviceType", ServiceType.DOCTORS_APPOINTMENTS)
            add("eventValue", 0.1)
        }}).asInstanceOf[java.util.List[ServiceAuthorizeEvent]]
        authorize.setAuthorizeEvents(authorizeEvent)
        authorize
    }

    def createEstablishmentEvent(establishment: Establishment = createEstablishment(),
                                 eventValue: BigDecimal = null,
                                                event: Event = createEvent(ServiceType.DOCTORS_APPOINTMENTS)): EstablishmentEvent = {
        from(classOf[EstablishmentEvent]).uses(jpaProcessor)
                .gimme("withoutReferences", new Rule() {
            {
                add("event", event)
                if (eventValue != null)
                    add("value", eventValue)
                add("establishment", establishment)
            }
        })
    }

    def createBatchClosing(): BatchClosing = {
        from(classOf[BatchClosing]).uses(jpaProcessor).gimme("valid")
    }

    def createBatchToPersist(): BatchClosing = {
        from(classOf[BatchClosing]).gimme("valid", new Rule() {
            {
                add("establishment", createEstablishment())
                add("issuer", createIssuer())
                add("accreditedNetwork", createNetwork())
                add("hirer", createHirer())
                add("payment", null)
            }
        })
    }

    def createHirer(): Hirer = {
        from(classOf[Hirer]).uses(jpaProcessor).gimme("valid",  new Rule())
    }

    def createContractor(label: String = "valid"): Contractor = {
        from(classOf[Contractor]).uses(jpaProcessor).gimme(label)
    }

    def createInstrumenBalance(instrument: PaymentInstrument = createInstrumentToProduct(),
                                                                                            value: BigDecimal = null): InstrumentBalance = {
     from(classOf[InstrumentBalance]).uses(jpaProcessor).gimme("valid", new Rule() {{
                add("paymentInstrument", instrument)
                if(value != null){
                    add("value", value)
                }
            }})
    }

    def createEvent(serviceType: ServiceType = ServiceType.DOCTORS_APPOINTMENTS, requestQuantity: Boolean = false): Event = {
        val serviceUnderTest: Service = from(classOf[Service]).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("type", serviceType)
        }})
        from(classOf[Event]).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("service", serviceUnderTest)
            add("requestQuantity", requestQuantity)
        }})
    }
    def createProduct(paymentRuleGroupUnderTest: PaymentRuleGroup = createPaymentRuleGroup(),
                          membershipFee: java.math.BigDecimal = randomBigDecimal()): Product = {
        from(classOf[Product]).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("paymentRuleGroup", paymentRuleGroupUnderTest)
            add("membershipFee", membershipFee)
        }})
    }

    def createProductWithIssuer(issuer: Issuer = createIssuer()): Product = {
        from(classOf[Product]).uses(jpaProcessor).gimme("valid", new Rule() {{
                add("issuer", issuer)
       }})
    }

    def createProductWithSameIssuerOfHirer(membershipFee: java.math.BigDecimal = randomBigDecimal()): Product = {
        val hirer = createHirer()
        val issuerPerson: Person = from(classOf[Person]).uses(jpaProcessor).gimme("physical", new Rule(){{
            add("document.number", hirer.getDocumentNumber)
        }})
        val issuer: Issuer = from(classOf[Issuer]).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", issuerPerson)
        }})
        val product: Product = from(classOf[Product]).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issuer", issuer)
            add("membershipFee", membershipFee)
        }})
        createCreditPaymentAccount(hirer.getDocumentNumber, product)
        product
    }

    def createHirerWithDocument(document: String): Hirer = {
        val hirerPerson: Person = from(classOf[Person]).uses(jpaProcessor).gimme("physical", new Rule() {{
                if (document != null) {
                    add("document.number", document)
                }
        }})
        val hirer: Hirer = from(classOf[Hirer]).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("person", hirerPerson)
            }
        })
        hirer
    }

    def createProductWithCreditInsertionType(creditInsertionTypes: java.util.List[CreditInsertionType]): Product = {
        from(classOf[Product]).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("creditInsertionTypes",creditInsertionTypes)
        }})
    }

    def createBatchItems(batchClosing: BatchClosing): java.util.List[BatchClosingItem] = {
        val serviceAuthorize = createServiceAuthorizePersisted()
        from(classOf[BatchClosingItem]).uses(jpaProcessor).gimme(2, "valid", new Rule() {
            {
                add("batchClosing", batchClosing)
                add("serviceAuthorize", serviceAuthorize)
                add("invoiceDocumentSituation", DocumentSituation.PENDING)
            }
        })
    }

    def createOrder(contract: Contract = createPersistedContract()): Order ={
        val contractor = createContractor("physical")
        val instrument = createInstrumentToProduct(contract.getProduct, contractor)
        return from(classOf[Order]).gimme("valid", new Rule(){{
            add("person", contractor.getPerson)
            add("product", contract.getProduct)
            add("contract", contract)
            add("type", OrderType.CREDIT)
            add("paymentInstrument", instrument)
            add("value", java.math.BigDecimal.ONE)
        }})
    }

    def createPersistedOrderWithStatus(status: PaymentStatus, typ: OrderType = OrderType.CREDIT,
                                         contractor: Contractor = createContractor("physical")): Order = {
        createPersistedPaidOrder(contractor, typ, status)
    }

    def createPersistedPaidOrder(contractor: Contractor = createContractor("physical"),
                                 typ: OrderType = OrderType.CREDIT, status: PaymentStatus = PaymentStatus.PAID): Order = {
        val product = createProduct()
        val user = createUser()
        val contract = createPersistedContract(contractor, product)
        from(classOf[ContractInstallment]).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("contract", contract)
            add("paymentDateTime", null)
        }})
        val instrument: PaymentInstrument = createInstrumentToProduct(product, contractor)
        return from(classOf[Order]).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", contractor.getPerson)
            add("person.physicalPersonDetail.email", user.getEmail)
            add("product", product)
            add("contract", contract)
            add("type", typ)
            add("paymentInstrument", instrument)
            add("value", java.math.BigDecimal.ONE)
            add("status", status)
        }})
    }

    def createPersistedAdhesionOrder(person: Person): Order = {
        val product: Product = createProductWithSameIssuerOfHirer()
        return from(classOf[Order]).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", person)
            add("product", product)
            add("type", OrderType.ADHESION)
            add("value", java.math.BigDecimal.ONE)
            add("status", PaymentStatus.PAID)
        }})
    }


    def createNegotiation(hirer: Hirer = createHirer(), product: Product = createProduct(),
                                        effectiveDate: Date = instant("one day ago")): HirerNegotiation = {
        from(classOf[HirerNegotiation]).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
            add("effectiveDate", effectiveDate)
            add("freeInstallmentQuantity", 0)
            add("billingWithCredits", java.lang.Boolean.TRUE)
            add("active", java.lang.Boolean.TRUE)
            add("paymentDay", paymentDayCalculator.getNearDay)
        }})
    }

    def createCreditPaymentAccount(): CreditPaymentAccount =  {
        from(classOf[CreditPaymentAccount]).uses(jpaProcessor).gimme("valid")
    }

    def createProductWithOutDirectDebit(): Product = {
        from(classOf[Product]).uses(jpaProcessor).gimme("creditWithoutDirectDebit")
    }

    def createEstablishment(network: AccreditedNetwork = createNetwork()): Establishment = {
        from(classOf[Establishment]).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("network",network)
        }})
    }

    def createHeadOffice(): Establishment =  {
        from(classOf[Establishment]).uses(jpaProcessor).gimme("valid")
    }

    def createNetwork(): AccreditedNetwork = {
        from(classOf[AccreditedNetwork]).uses(jpaProcessor).gimme("valid")
    }

    def createService(establishment: Establishment = createEstablishment()): Service = {
        from(classOf[Service]).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("establishments", java.util.Arrays.asList(establishment))
        }})
    }

    def createIssuer(): Issuer = {
        from(classOf[Issuer]).uses(jpaProcessor).gimme("valid")
    }

    def createInstitution(): Institution = {
        from(classOf[Institution]).uses(jpaProcessor).gimme("valid")
    }

    def createPaymentRuleGroup(institution: Institution = createInstitution()): PaymentRuleGroup =  {
        from(classOf[PaymentRuleGroup]).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("institution", institution)
        }})
    }

    def createPaymentRuleGroupDefault(): PaymentRuleGroup =  {
        from(classOf[PaymentRuleGroup]).uses(jpaProcessor).gimme("default")
    }

    def createPersistedAuthorizedMember(contractor: Contractor = createContractor("valid")): AuthorizedMember =  {
        val contract: Contract = createPersistedContract(contractor)
        from(classOf[AuthorizedMember]).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("paymentInstrument", createInstrumentToProduct(createProduct(), contract.getContractor))
            add("contract", contract)
        }})
    }

    def createAuthorizedMemberForContract(contract: Contract): AuthorizedMember =  {
         from(classOf[AuthorizedMember]).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("paymentInstrument", createInstrumentToProduct(contract.getProduct, contract.getContractor))
            add("contract", contract)
        }})
    }

    def createAuthorizedMemberToPersist(): AuthorizedMember = {
        val contract: Contract = createPersistedContract()
        from(classOf[AuthorizedMember]).gimme("valid", new Rule() {{
            add("paymentInstrument", createInstrumentToProduct(createProduct(), contract.getContractor))
            add("contract", contract)
        }})
    }

    def createAuthorizedMemberCandidateToPersist(): AuthorizedMemberCandidate =  {
        val order: Order = createPersistedAdhesionOrder(createContractor().getPerson)
        from(classOf[AuthorizedMemberCandidate]).gimme("valid", new Rule() {{
            add("order", order)
        }})
    }

    def createAuthorizedMemberCandidateForOrder(order: Order = createPersistedAdhesionOrder(createContractor().getPerson)): AuthorizedMemberCandidate =  {
        from(classOf[AuthorizedMemberCandidate]).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("order", order)
        }})
    }

    def createBonusBillingToPersist(): BonusBilling = {
        val person: Person = from(classOf[Person]).uses(jpaProcessor).gimme("physical")
        val issuer: Issuer = createIssuer()
        return from(classOf[BonusBilling]).gimme("valid", new Rule() {{
            add("payer", person)
            add("issuer", issuer)
        }})
    }

    def createPersistedBonusBilling(person: Person = from(classOf[Person]).uses(jpaProcessor).gimme("physical")): BonusBilling = {
        return from(classOf[BonusBilling]).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("payer", person)
        }})
    }

    def createPersistedContractorBonusForContractor(contractor : Contractor = createContractor()): ContractorBonus = {
        from(classOf[ContractorBonus]).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("contractor", contractor)
            add("product", createProduct())
            add("payer", contractor.getPerson)
        }})
    }

    def createPersistedContractorBonusWithProduct(product: Product = createProduct()): ContractorBonus = {
        val contractor = createContractor()
        from(classOf[ContractorBonus]).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("contractor", contractor)
            add("product", product)
            add("payer", contractor.getPerson)
        }})
    }

    private def instant(pattern: String): java.util.Date ={
        new ChronicFunction(pattern).generateValue()
    }

    private def randomBigDecimal(): java.math.BigDecimal ={
        val random : scala.math.BigDecimal = Math.random() * 100
        return random.bigDecimal
    }
}
