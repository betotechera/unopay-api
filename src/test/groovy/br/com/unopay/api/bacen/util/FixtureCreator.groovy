package br.com.unopay.api.bacen.util

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.JpaProcessor
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Event
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.model.Service
import br.com.unopay.api.bacen.model.ServiceType
import br.com.unopay.api.model.BatchClosing
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractEstablishment
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.ContractorInstrumentCredit
import br.com.unopay.api.model.Credit
import br.com.unopay.api.model.CreditPaymentAccount
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.Product
import br.com.unopay.api.model.ServiceAuthorize
import br.com.unopay.api.repository.PaymentInstrumentRepository
import br.com.unopay.api.service.ContractService
import br.com.unopay.api.service.ContractorInstrumentCreditService
import br.com.unopay.api.uaa.model.Group
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.uaa.service.UserDetailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class FixtureCreator {

    @Autowired
    private ContractService contractService

    @Autowired
    private PaymentInstrumentRepository paymentInstrumentRepository

    @Autowired
    private ContractorInstrumentCreditService contractorInstrumentCreditService

    @Autowired
    private UserDetailService userDetailService

    @Autowired
    private PasswordEncoder passwordEncoder

    @Autowired
    private JpaProcessor jpaProcessor

    BatchClosing createBatchClosing() {
        Fixture.from(BatchClosing.class).uses(jpaProcessor).uses(jpaProcessor).gimme("valid")
    }

    UserDetail createEstablishmentUser(establishmentUnderTest = createEstablishment()){
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group", new Rule(){{
            add("establishment", establishmentUnderTest)
        }})
        createUser(user)
    }

    UserDetail createUser(UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")){
        Group group = Fixture.from(Group.class).uses(jpaProcessor).uses(jpaProcessor).gimme("valid")
        user.addToMyGroups(group)
        userDetailService.create(user)
    }

    Establishment createHeadOffice() {
        return Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
    }

    AccreditedNetwork createNetwork() {
        Fixture.from(AccreditedNetwork.class).uses(jpaProcessor).gimme("valid")
    }
    Issuer createIssuer() {
        Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid")
    }

    PaymentRuleGroup createPaymentRuleGroup() {
        return Fixture.from(PaymentRuleGroup.class).uses(jpaProcessor).gimme("valid")
    }

    PaymentRuleGroup createPaymentRuleGroupDefault() {
        Fixture.from(PaymentRuleGroup.class).uses(jpaProcessor).gimme("default")
    }

    PaymentInstrument createPaymentInstrument(String label = "valid") {
        Fixture.from(PaymentInstrument.class).gimme(label, new Rule(){{
            add("contractor", createContractor())
            add("product", createProduct())
        }})
    }

    PaymentInstrument createPaymentInstrumentWithProduct(Product productUnderTest = createProduct(),
                                                         contractorUnderTest = createContractor()) {
        Fixture.from(PaymentInstrument.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("product", productUnderTest)
            add("contractor", contractorUnderTest)
        }})
    }

    Hirer createHirer() {
        Fixture.from(Hirer.class).uses(jpaProcessor).gimme("valid")
    }
    Contractor createContractor(String label = "valid") {
        Fixture.from(Contractor.class).uses(jpaProcessor).gimme(label)
    }

    Event createEvent(ServiceType serviceType) {
        Service serviceUnderTest = Fixture.from(Service.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("type", serviceType)
        }})
        Fixture.from(Event.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("service", serviceUnderTest)
        }})
    }
    Product createProduct(PaymentRuleGroup paymentRuleGroupUnderTest = createPaymentRuleGroup()) {
        Fixture.from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("paymentRuleGroup", paymentRuleGroupUnderTest)
        }})
    }

    Product createProductWithCreditInsertionType(creditTypes) {
        Fixture.from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("creditInsertionTypes",creditTypes)
        }})
    }

    Product createProductWithOutDirectDebit() {
        Fixture.from(Product.class).uses(jpaProcessor).gimme("creditWithoutDirectDebit")
    }
    Credit createCredit(Product product = createProduct()){
        def hirer = createHirer()
        Fixture.from(Credit.class).gimme("allFields", new Rule(){{
            add("hirerDocument", hirer.getDocumentNumber())
            add("product", product)
            if(product?.creditInsertionTypes) {
                add("creditInsertionType", product?.creditInsertionTypes?.find())
            }
        }})
    }

    Contract createContract(contractorUnderTest = createContractor(),
                            Product productUnderTest = createProduct(), hirerUnderTest = createHirer()) {
        Fixture.from(Contract.class).gimme("valid", new Rule(){{
            add("hirer", hirerUnderTest)
            add("contractor", contractorUnderTest)
            add("product", productUnderTest)
            add("serviceType",productUnderTest.serviceTypes)
        }})
    }

    Contract createPersistedContract(contractorUnderTest = createContractor(),
                                     Product productUnderTest = createProduct(),
                                     hirer = createHirer(), situationUnderTest = ContractSituation.ACTIVE){
        Fixture.from(Contract.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("contractor", contractorUnderTest)
            add("product", productUnderTest)
            add("serviceType",productUnderTest.serviceTypes)
            add("situation", situationUnderTest)
        }})
    }

    CreditPaymentAccount createCreditPaymentAccount() {
        Fixture.from(CreditPaymentAccount.class).uses(jpaProcessor).gimme("valid")
    }

    List addContractsToEstablishment(Establishment establishmentUnderTest, ContractorInstrumentCredit instrumentCreditUnderTest) {
        ContractEstablishment contractEstablishment = Fixture.from(ContractEstablishment.class).gimme("valid")
        contractEstablishment.with { establishment = establishmentUnderTest }
        def contractA = createPersistedContract(createContractor(), instrumentCreditUnderTest.contract.product)
        def contractB = createPersistedContract(createContractor(), instrumentCreditUnderTest.contract.product)
        contractService.addEstablishments(contractA.id, contractEstablishment)
        contractService.addEstablishments(contractB.id, contractEstablishment.with {id = null; it })
        [contractB, contractA]
    }

    CreditPaymentAccount createCreditPaymentAccountFromContract(Contract contract = createContract()) {
        Fixture.from(CreditPaymentAccount.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("product", contract.product)
            add("issuer", contract.product.issuer)
            add("hirerDocument", contract.hirer.documentNumber)
            add("paymentRuleGroup", contract.product.paymentRuleGroup)
            add("serviceType", contract.serviceType.find())
        }})
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

    ServiceAuthorize createServiceAuthorize(ContractorInstrumentCredit instrumentCreditUnderTest = createContractorInstrumentCredit(),
                                            Establishment establishmentUnderTest = createEstablishment() ){
        encodeInstrumentPassword(instrumentCreditUnderTest)
        def password = instrumentCreditUnderTest.paymentInstrument.password
        contractorInstrumentCreditService.insert(instrumentCreditUnderTest.paymentInstrumentId, instrumentCreditUnderTest)
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid")
        serviceAuthorize.with {
            contract = instrumentCreditUnderTest.contract
            contractor = instrumentCreditUnderTest.contract.contractor
            event = createEvent(ServiceType.FUEL_ALLOWANCE)
            serviceType = ServiceType.FUEL_ALLOWANCE
            eventValue = 0.1
            user = createUser()
            contractorInstrumentCredit = instrumentCreditUnderTest
            establishment = establishmentUnderTest
            contractorInstrumentCredit.paymentInstrument.password = password
            it
        }
    }

    void encodeInstrumentPassword(ContractorInstrumentCredit instrumentCredit) {
        def instrumentPasswordUnderTest = instrumentCredit.paymentInstrument.password
        def byId = paymentInstrumentRepository.findById(instrumentCredit.paymentInstrumentId).get()
        byId.with {
            password = passwordEncoder.encode(instrumentPasswordUnderTest)
        }
        paymentInstrumentRepository.save(byId)
        instrumentCredit.paymentInstrument.with {
            password = instrumentPasswordUnderTest
        }
    }

    Establishment createEstablishment() {
        Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
    }
}
