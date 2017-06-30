package br.com.unopay.api.bacen.util

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.six2six.fixturefactory.function.impl.RegexFunction
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

    UserDetail createEstablishmentUser(establishmentUnderTest = createEstablishment()){
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        UserDetail user = Fixture.from(UserDetail.class).uses(jpaProcessor).gimme("with-group", new Rule(){{
            add("establishment", establishmentUnderTest)
            add("password", passwordEncoder.encode(generatePassword))
        }})
        user.with { password = generatePassword; it }
    }

    UserDetail createUser(){
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        UserDetail user = Fixture.from(UserDetail.class).uses(jpaProcessor).gimme("with-group", new Rule(){{
            add("password", passwordEncoder.encode(generatePassword))
        }})
        user.with { password = generatePassword; it }
    }

    PaymentInstrument createPaymentInstrument(String label = "valid") {
        Fixture.from(PaymentInstrument.class).gimme(label, new Rule(){{
            add("contractor", createContractor())
            add("product", createProduct())
        }})
    }

    PaymentInstrument createInstrumentToProduct(Product product = createProduct(), contractor = createContractor()) {
        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        PaymentInstrument inst = Fixture.from(PaymentInstrument.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("product", product)
            add("contractor", contractor)
            add("password", passwordEncoder.encode(generatePassword))
        }})
        inst.with { password = generatePassword; it }
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

    Contract createPersistedContract(contractor = createContractor(), Product product = createProduct(),
                                     hirer = createHirer(), situation = ContractSituation.ACTIVE){
        Fixture.from(Contract.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("contractor", contractor)
            add("product", product)
            add("serviceType",product.serviceTypes)
            add("situation", situation)
        }})
    }

    List addContractsToEstablishment(Establishment establishment, Product product) {
        def contractA = createPersistedContract(createContractor(), product)
        def contractB = createPersistedContract(createContractor(), product)
        Fixture.from(ContractEstablishment.class).uses(jpaProcessor).gimme(2, "valid", new Rule(){{
            add("establishment", establishment)
            add("contract", uniqueRandom(contractA, contractB))
        }})
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

    ContractorInstrumentCredit instrumentCredit(Contractor contractor = createContractor(), Contract contract = null){
        contract = contract ?: createPersistedContract(contractor)
        def creditPaymentAccountUnderTest = createCreditPaymentAccountFromContract(contract)
        Fixture.from(ContractorInstrumentCredit.class).gimme("toPersist", new Rule(){{
            add("paymentInstrument",createInstrumentToProduct(contract.product, contractor))
            add("creditPaymentAccount",creditPaymentAccountUnderTest)
            add("serviceType",contract.serviceType.find())
            add("value",creditPaymentAccountUnderTest.availableBalance)
            add("contract",contract)
        }})
    }

    ContractorInstrumentCredit createInstrumentToContract(Contract contract){
        PaymentInstrument paymentInstrument = createInstrumentToProduct(contract.product, contract.contractor)
        CreditPaymentAccount paymentAccount = Fixture.from(CreditPaymentAccount.class)
                .uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirerDocument", contract.hirerDocumentNumber)
            add("product", contract.product)
        }})
        ContractorInstrumentCredit instrumentCredit = Fixture.from(ContractorInstrumentCredit.class)
                .uses(jpaProcessor).gimme("allFields", new Rule(){{
            add("contract", contract)
            add("paymentInstrument", paymentInstrument)
            add("creditPaymentAccount", paymentAccount)
        }})
        instrumentCredit
    }


    ContractorInstrumentCredit createContractorInstrumentCreditPersisted(){
        def contractUnderTest = createPersistedContract()
        def creditPaymentAccountUnderTest = createCreditPaymentAccountFromContract(contractUnderTest)
        Fixture.from(ContractorInstrumentCredit.class).uses(jpaProcessor).gimme("toPersist", new Rule(){{
            add("paymentInstrument",createInstrumentToProduct(contractUnderTest.product, contractUnderTest.contractor))
            add("creditPaymentAccount",creditPaymentAccountUnderTest)
            add("serviceType",contractUnderTest.serviceType.find())
            add("value",creditPaymentAccountUnderTest.availableBalance)
            add("contract",contractUnderTest)
        }})
    }

    ServiceAuthorize createServiceAuthorize(ContractorInstrumentCredit credit = createContractorInstrumentCreditPersisted(),
                                            Establishment establishment = createEstablishment(), String dateAsText = "1 day ago"){
        Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule(){{
            add("contract",credit.contract)
            add("contractor",credit.contract.contractor)
            add("event",createEvent(ServiceType.FUEL_ALLOWANCE))
            add("serviceType",ServiceType.FUEL_ALLOWANCE)
            add("eventValue",0.1)
            add("user",createUser())
            add("authorizationDateTime", instant(dateAsText))
            add("contractorInstrumentCredit",credit)
            add("establishment",establishment)
            add("contractorInstrumentCredit.paymentInstrument.password",credit.paymentInstrument.password)
        }})
    }

    BatchClosing createBatchClosing() {
        Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid")
    }

    BatchClosing createBatchToPersist(){
       Fixture.from(BatchClosing.class).gimme("valid", new Rule(){{
            add("establishment",createEstablishment())
            add("issuer",createIssuer())
            add("accreditedNetwork",createNetwork())
            add("hirer",createHirer())
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

    CreditPaymentAccount createCreditPaymentAccount() {
        Fixture.from(CreditPaymentAccount.class).uses(jpaProcessor).gimme("valid")
    }

    Product createProductWithOutDirectDebit() {
        Fixture.from(Product.class).uses(jpaProcessor).gimme("creditWithoutDirectDebit")
    }

    Establishment createEstablishment() {
        Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
    }

    Establishment createHeadOffice() {
        Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
    }

    AccreditedNetwork createNetwork() {
        Fixture.from(AccreditedNetwork.class).uses(jpaProcessor).gimme("valid")
    }
    Issuer createIssuer() {
        Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid")
    }

    PaymentRuleGroup createPaymentRuleGroup() {
        Fixture.from(PaymentRuleGroup.class).uses(jpaProcessor).gimme("valid")
    }

    PaymentRuleGroup createPaymentRuleGroupDefault() {
        Fixture.from(PaymentRuleGroup.class).uses(jpaProcessor).gimme("default")
    }
}
