package br.com.unopay.api.credit.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.credit.model.ContractorInstrumentCredit
import br.com.unopay.api.credit.model.ContractorInstrumentCreditType
import br.com.unopay.api.credit.model.CreditPaymentAccount
import br.com.unopay.api.credit.model.CreditSituation
import br.com.unopay.api.credit.model.InstrumentCreditSource
import br.com.unopay.api.market.model.filter.BonusBillingFilter
import br.com.unopay.api.market.service.BonusBillingService
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.PaymentInstrumentType
import br.com.unopay.api.model.Person
import br.com.unopay.api.model.Product
import br.com.unopay.api.network.model.ServiceType
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.order.model.PaymentStatus
import br.com.unopay.api.service.ContractService
import br.com.unopay.api.service.PaymentInstrumentService
import br.com.unopay.api.util.Rounder
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import groovy.time.TimeCategory
import static org.hamcrest.Matchers.hasSize
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import static spock.util.matcher.HamcrestSupport.that

class ContractorInstrumentCreditServiceTest extends SpockApplicationTests {

    @Autowired
    ContractorInstrumentCreditService service

    @Autowired
    ContractService contractService

    @Autowired
    CreditPaymentAccountService creditPaymentAccountService

    @Autowired
    PaymentInstrumentService paymentInstrumentService

    @Autowired
    InstrumentBalanceService instrumentBalanceService

    @Autowired
    BonusBillingService bonusBillingService

    @Autowired
    FixtureCreator fixtureCreator

    Contractor contractorUnderTest
    Contract contractUnderTest
    PaymentInstrument paymentInstrumentUnderTest
    CreditPaymentAccount creditPaymentAccountUnderTest
    Hirer hirerUnderTest

    void setup(){
        contractorUnderTest = fixtureCreator.createContractor()
        hirerUnderTest = fixtureCreator.createHirer()
        contractUnderTest = fixtureCreator
                            .createPersistedContract(contractorUnderTest, fixtureCreator.createProduct(), hirerUnderTest)
        paymentInstrumentUnderTest = fixtureCreator
                                    .createInstrumentToProduct(contractUnderTest.product, contractorUnderTest)
        creditPaymentAccountUnderTest = fixtureCreator.createCreditPaymentAccountFromContract(contractUnderTest)
        Integer.mixin(TimeCategory)
    }

    def 'given a paid order for known client with contract and payment instrument should insert credit'(){
        given:
        Order creditOrder = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", contractUnderTest.contractor.person)
            add("product", contractUnderTest.product)
            add("status", PaymentStatus.PAID)
            add("paymentInstrument", paymentInstrumentUnderTest)
        }})

        when:
        ContractorInstrumentCredit created = service.processOrder(creditOrder)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.availableBalance == creditOrder.getValue()
        result.contract.id == contractUnderTest.id
        result.situation == CreditSituation.AVAILABLE
    }

    def 'given a paid order for known client with contract and more one instrument should insert credit on order instrument'(){
        given:
        fixtureCreator.createInstrumentToProduct(contractUnderTest.product, contractorUnderTest)
        Order creditOrder = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", contractUnderTest.contractor.person)
            add("product", contractUnderTest.product)
            add("status", PaymentStatus.PAID)
            add("paymentInstrument", paymentInstrumentUnderTest)
        }})

        when:
        ContractorInstrumentCredit created = service.processOrder(creditOrder)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.paymentInstrument == paymentInstrumentUnderTest
    }

    def 'given an order with more one payment account should insert credit in payment account with same product order'(){
        given:
        fixtureCreator.createInstrumentToProduct(contractUnderTest.product, contractorUnderTest)
        Order creditOrder = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", contractUnderTest.contractor.person)
            add("product", contractUnderTest.product)
            add("status", PaymentStatus.PAID)
            add("paymentInstrument", paymentInstrumentUnderTest)
        }})
        fixtureCreator.createCreditPaymentAccount(hirerUnderTest.documentNumber)

        when:
        ContractorInstrumentCredit created = service.processOrder(creditOrder)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.creditPaymentAccount.id == creditPaymentAccountUnderTest.id
    }

    def 'given an order with hirer without payment account should create and insert credit in payment account with same product order'(){
        given:
        Hirer hirer = fixtureCreator.createHirer()
        def contractor = fixtureCreator.createContractor()
        def contract = fixtureCreator
                .createPersistedContract(contractor, fixtureCreator.createProduct(), hirer)
        def instrumentToProduct = fixtureCreator.createInstrumentToProduct(contract.product, contractor)
        Order creditOrder = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", contract.product)
            add("status", PaymentStatus.PAID)
            add("contract", contract)
            add("paymentInstrument", instrumentToProduct)
        }})

        when:
        ContractorInstrumentCredit created = service.processOrder(creditOrder)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        contract.isProductCodeEquals(result.creditPaymentAccount.productCode)
    }

    def 'given a paid order for unknown client should not insert credit'(){
        given:
        def hirer = fixtureCreator.createHirer()
        Person issuerPerson = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical", new Rule(){{
            add("document.number", hirer.documentNumber)
        }})
        Issuer issuer = Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", issuerPerson)
        }})
        Product product = Fixture.from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issuer", issuer)
        }})
        fixtureCreator.createCreditPaymentAccount(hirer.documentNumber, product)

        def person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        Order creditOrder = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", person)
            add("product", product)
            add("status", PaymentStatus.PAID)
            add("paymentInstrument", null)
        }})

        when:
        service.processOrder(creditOrder)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }

    def 'given a valid instrument credit should be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        when:
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.id != null
    }

    def 'given a instrument credit without type should be inserted with type normal'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.type = null
        when:
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.id != null
        result.type == ContractorInstrumentCreditType.NORMAL
    }

    def "given a instrument credit with type should be inserted with it's type"(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.type = ContractorInstrumentCreditType.BONUS
        when:
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.id != null
        result.type == ContractorInstrumentCreditType.BONUS
    }

    def "given valid bonus billing when processing it should create instrument credit with product expiration"(){
        given:
        def contractorBonus1 = fixtureCreator.createPersistedContractorBonusForContractor()
        def product = contractorBonus1.getProduct()
        def contractor1 = contractorBonus1.getContractor()
        fixtureCreator.createPersistedContract(contractor1, product)
        fixtureCreator.createPersistedInstrument(contractor1, product, PaymentInstrumentType.DIGITAL_WALLET)

        def contractorBonus2 = fixtureCreator.createPersistedContractorBonusForContractor(
                fixtureCreator.createContractor(), contractorBonus1.getPayer(), product)
        def contractor2 = contractorBonus2.getContractor()
        fixtureCreator.createPersistedContract(contractor2, product)
        fixtureCreator.createPersistedInstrument(contractor2, product, PaymentInstrumentType.DIGITAL_WALLET)

        def filter = new BonusBillingFilter()
        filter.document = contractorBonus1.payer.documentNumber()
        bonusBillingService.process()
        def billing = bonusBillingService.findByFilter(filter, new UnovationPageRequest()).first()

        when:
        service.processBonusBilling(billing)

        def found1 = service.findByContractorId(contractor1.id)
        def found2 = service.findByContractorId(contractor2.id)

        then:
        def timeToExpire = new DateTime().plusMonths(product.monthsToExpireBonus)
        timeComparator.compare(found1.expirationDateTime, timeToExpire) == 0
        timeComparator.compare(found2.expirationDateTime, timeToExpire) == 0
    }

    def "given valid bonus billing when processing it should create instrument credit"(){
        given:
        def contractorBonus1 = fixtureCreator.createPersistedContractorBonusForContractor()
        def product = contractorBonus1.getProduct()
        def contractor1 = contractorBonus1.getContractor()
        fixtureCreator.createPersistedContract(contractor1, product)
        fixtureCreator.createPersistedInstrument(contractor1, product, PaymentInstrumentType.DIGITAL_WALLET)

        def contractorBonus2 = fixtureCreator.createPersistedContractorBonusForContractor(
                                                fixtureCreator.createContractor(), contractorBonus1.getPayer(), product)
        def contractor2 = contractorBonus2.getContractor()
        fixtureCreator.createPersistedContract(contractor2, product)
        fixtureCreator.createPersistedInstrument(contractor2, product, PaymentInstrumentType.DIGITAL_WALLET)

        def filter = new BonusBillingFilter()
        filter.document = contractorBonus1.payer.documentNumber()
        bonusBillingService.process()
        def billing = bonusBillingService.findByFilter(filter, new UnovationPageRequest()).first()

        when:
        service.processBonusBilling(billing)

        def found1 = service.findByContractorId(contractor1.id)
        def found2 = service.findByContractorId(contractor2.id)

        then:
        found1.availableBalance == contractorBonus1.earnedBonus
        found2.availableBalance == contractorBonus2.earnedBonus
    }

    def "given bonus billing without bonus associated to id when processing it should return error"(){
        given:
        def billing = fixtureCreator.createPersistedBonusBilling()

        when:
        service.processBonusBilling(billing)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACTOR_BONUS_NOT_FOUND'
    }

    def "given bonus billing whose contractor has no contract associated to id when processing it should return error"(){
        given:
        def contractorBonus = fixtureCreator.createPersistedContractorBonusForContractor()

        def filter = new BonusBillingFilter()
        filter.document = contractorBonus.payer.documentNumber()
        bonusBillingService.process()
        def billing = bonusBillingService.findByFilter(filter, new UnovationPageRequest()).first()
        when:
        service.processBonusBilling(billing)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }

    def 'given a valid instrument credit when create should be subtract payment account balance'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        def paymentAccount = creditPaymentAccountService.findById(instrumentCredit.creditPaymentAccount.id)
        def expectedPaymentAccountBalance = paymentAccount.availableBalance - instrumentCredit.value
        when:
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        then:
        creditPaymentAccountService
                .findById(instrumentCredit.creditPaymentAccount.id)
                .availableBalance == Rounder.round(expectedPaymentAccountBalance)
    }


    def 'given a valid instrument credit when create should be increment instrument balance'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        def expectedPaymentAccountBalance = instrumentCredit.value
        when:
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        then:
        instrumentBalanceService.findByInstrumentId(instrumentCredit.paymentInstrumentId)
                .value == Rounder.round(expectedPaymentAccountBalance)
    }

    def 'when insert instrument credit then balance should be equals value'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        when:
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.id != null
        result.value == result.availableBalance
    }

    def 'given a credit without credit source should define hirer credit source'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.creditSource = null

        when:
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.id != null
        result.creditSource == InstrumentCreditSource.HIRER
    }

    def 'when insert instrument credit then block balance should be zero'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        when:
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.id != null
        result.blockedBalance == BigDecimal.ZERO
    }

    def 'when insert instrument credit then situation should be available'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        when:
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.id != null
        result.situation == CreditSituation.AVAILABLE
    }

    def 'when insert instrument credit then emission fee should be equals product credit insertion fee'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        when:
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.id != null
        result.issuerFee == result.contract.product.creditInsertionFee
    }

    def 'given a credit payment account balance with balance less than instrument credit value should not be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with { value = creditPaymentAccountUnderTest.availableBalance + 0.01 }
        when:
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'VALUE_GREATER_THAN_BALANCE'
    }

    def 'given a instrument credit with expiration date less than now should not be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with { expirationDateTime = 1.second.ago }

        when:
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EXPIRATION_DATA_GREATER_THAN_NOW_REQUIRED'
    }

    def 'given a instrument credit with expiration date equals now should not be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with { expirationDateTime = new Date() }

        when:
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EXPIRATION_DATA_GREATER_THAN_NOW_REQUIRED'

    }


    def 'given a instrument credit with value less than or equals zero should not be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit().with { value = valueUnderTest; it }
        when:
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'VALUE_GREATER_THAN_ZERO_REQUIRED'

        where:
        _ | valueUnderTest
        _ | 0
        _ | -1
    }

    def 'should create instrument credit with contractor contract'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with {
            paymentInstrument = paymentInstrumentUnderTest
        }

        when:
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.id != null
        result.contract.id == contractUnderTest.id
    }

    def 'should create instrument credit with credit payment instrument of hirer'(){
        given:
        List<PaymentInstrument> paymentInstruments = paymentInstrumentService.findByContractorId(contractorUnderTest.id)
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()

        when:
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.id != null
        result.paymentInstrument in paymentInstruments
    }

    def 'instrument credit with credit payment instrument of another hirer should not be inserted'(){
        given:
        def contractor = fixtureCreator.createContractor()
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with {
            paymentInstrument = fixtureCreator
                    .createInstrumentToProduct(contractUnderTest.product, contractor)
        }
        when:
       service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'PAYMENT_INSTRUMENT_NOT_VALID'
    }

    def 'should insert instrument credit with credit payment account of my hirer'(){
        given:
        List<CreditPaymentAccount> creditPaymentAccounts = creditPaymentAccountService
                                                            .findByHirerDocument(contractUnderTest.hirer.documentNumber)
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        when:
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.id != null
         creditPaymentAccounts.any { it.id == result.creditPaymentAccount.id }
    }

    def 'payment instrument credit with credit payment account belongs to another hirer should not be inserted'(){
        given:
        def contractor = fixtureCreator.createContractor()
        def anotherContract = fixtureCreator.createContract(contractor, contractUnderTest.product)
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with {
            creditPaymentAccount = fixtureCreator.createCreditPaymentAccountFromContract(anotherContract)
        }
        when:
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_HIRER'
    }

    def 'given a contractor with two or more contracts when insert credit should be inserted with contract informed'(){
        given:
        def anotherContract = fixtureCreator
                            .createPersistedContract(contractorUnderTest, contractUnderTest.product, hirerUnderTest)
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with {
            contract = anotherContract
        }
        when:
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.contract.id != contractUnderTest.id
        result.contract.id == anotherContract.id
    }

    def 'payment instrument credit with credit payment account with product different of contract product should not be inserted'(){
        given:
        def myContract = contractService.getByIdAndContractorId(contractUnderTest.id, contractorUnderTest)
        List<CreditPaymentAccount> creditPaymentAccounts = creditPaymentAccountService
                                                                .findByHirerDocument(myContract.hirer.documentNumber)
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()

        def paymentAccount = creditPaymentAccounts.find()?.with {
            product = fixtureCreator.createProduct(myContract.product.paymentRuleGroup)
            it
        }
        creditPaymentAccountService.save(paymentAccount)
        instrumentCredit.with {
            creditPaymentAccount = paymentAccount
        }
        when:
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_PRODUCT'
    }

    def 'payment instrument credit with credit payment account with service different of contract service should not be inserted'(){
        given:
        def myContract = contractService.getByIdAndContractorId(contractUnderTest.id, contractorUnderTest)
        List<CreditPaymentAccount> creditPaymentAccounts = creditPaymentAccountService
                .findByHirerDocument(myContract.hirer.documentNumber)
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        def paymentAccount = creditPaymentAccounts.find()?.with {
            serviceType = ServiceType.values().find { !(it in myContract.serviceTypes) }
            it
        }
        creditPaymentAccountService.save(paymentAccount)
        instrumentCredit.with {
            creditPaymentAccount = paymentAccount
        }
        when:
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_SERVICE'
    }

    def 'given a payment instrument credit with service different of contract service should not be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with {
            serviceType = ServiceType.values().find { !(it in contractUnderTest.serviceTypes) }
        }
        when:
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'SERVICE_NOT_ACCEPTED'
    }

    def 'given a payment instrument credit without service type should be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with {
            serviceType = null
        }
        when:
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result != null
        !result.serviceType
    }

    def 'given an hirer product code different of payment instrument product code should not be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with {
            paymentInstrument.product.code = '5464664'
        }

        when:
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'PRODUCT_CODE_NOT_MET'
    }

    def 'given an hirer product id different of payment instrument product id should not be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with {
            paymentInstrument.product.id = '5464664'
        }

        when:
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'PRODUCT_ID_NOT_MET'
    }

    def 'given a payment instrument with unknown payment instrument then credit should not be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit =  createInstrumentCredit()

        when:
        service.insert('', instrumentCredit)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_INSTRUMENT_NOT_FOUND'
    }


    def 'given a known instrument credit should be canceled'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        when:
        service.cancel(paymentInstrumentUnderTest.id, created.id)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.situation == CreditSituation.CANCELED
    }

    def 'when cancel credit should given back credit to payment account'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        def paymentAccount = creditPaymentAccountService.findById(instrumentCredit.creditPaymentAccount.id)
        def expectedPaymentAccountBalance = paymentAccount.availableBalance

        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        when:
        service.cancel(paymentInstrumentUnderTest.id, created.id)

        then:
        creditPaymentAccountService
                           .findById(instrumentCredit.creditPaymentAccount.id)
                            .availableBalance == Rounder.round(expectedPaymentAccountBalance)
    }

    def 'when cancel credit should subtract credit of instrument balance'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        when:
        service.cancel(paymentInstrumentUnderTest.id, created.id)

        then:
        instrumentBalanceService.findByInstrumentId(instrumentCredit.paymentInstrumentId)
                .value == 0.0
    }

    def 'when cancel instrument credit already canceled should return error'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        ContractorInstrumentCredit created = service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        service.cancel(paymentInstrumentUnderTest.id, created.id)

        when:
        service.cancel(paymentInstrumentUnderTest.id, created.id)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CREDIT_ALREADY_CANCELED'
    }

    def 'given a unknown instrument credit should not be canceled'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        when:
        service.cancel(paymentInstrumentUnderTest.id, '')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACTOR_INSTRUMENT_CREDIT_NOT_FOUND'
    }

    def 'when cancel credit by contract should given back all credit to payment account'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        def paymentAccount = creditPaymentAccountService.findById(instrumentCredit.creditPaymentAccount.id)
        def expectedPaymentAccountBalance = paymentAccount.availableBalance
        instrumentCredit = instrumentCredit.with { value = (expectedPaymentAccountBalance / 2); it }
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit.with { id = null; it })

        when:
        service.cancel(instrumentCredit.contract.id)

        then:
        creditPaymentAccountService
                .findById(instrumentCredit.creditPaymentAccount.id)
                .availableBalance == Rounder.round(expectedPaymentAccountBalance)
    }

    def 'when cancel credit by contract should subtract all credit of instrument balances'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        def paymentAccount = creditPaymentAccountService.findById(instrumentCredit.creditPaymentAccount.id)
        def expectedPaymentAccountBalance = paymentAccount.availableBalance
        instrumentCredit = instrumentCredit.with { value = (expectedPaymentAccountBalance / 2); it }
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit.with { id = null; it })

        when:
        service.cancel(instrumentCredit.contract.id)

        then:
        instrumentBalanceService.findByInstrumentId(instrumentCredit.paymentInstrumentId)
                .value == 0.0
    }

    def 'given a unknown contract should not be canceled'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)

        when:
        service.cancel('')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }

    def 'given a contract without instrument credits should not be canceled'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()

        when:
        service.cancel(instrumentCredit.contract.id)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CONTRACT_WITHOUT_CREDITS'
    }

    def 'should return all contractor credits'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        service.insert(instrumentCredit.paymentInstrumentId, instrumentCredit)

        when:
        Page<ContractorInstrumentCredit> credits =  service
                .findContractorCredits(contractUnderTest.id, contractorUnderTest.documentNumber, new UnovationPageRequest())

        then:
        that credits.getContent(), hasSize(1)
    }

    def 'given contractor without credits when find should not be returned'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        service.insert(instrumentCredit.paymentInstrumentId, instrumentCredit)

        when:
        Page<ContractorInstrumentCredit> credits =  service
                .findContractorCredits('', contractorUnderTest.documentNumber, new UnovationPageRequest())

        then:
        that credits.getContent(), hasSize(0)
    }

    private ContractorInstrumentCredit createInstrumentCredit(ServiceType svt = contractUnderTest.serviceTypes.find(),
                                                              Contract contractTest = contractUnderTest) {
        ContractorInstrumentCredit instrumentCredit = Fixture.from(ContractorInstrumentCredit.class).gimme("toPersist")
        instrumentCredit.with {
            paymentInstrument = paymentInstrumentUnderTest
            creditPaymentAccount = creditPaymentAccountUnderTest
            serviceType = svt
            value = creditPaymentAccountUnderTest.availableBalance - (Math.random() * 1)
            contract = contractTest
        }
        instrumentCredit
    }

    private void insertCreditAndRollback(ContractorInstrumentCredit instrumentCredit) {
        service.insert(paymentInstrumentUnderTest.id, instrumentCredit)
        creditPaymentAccountService.giveBack(instrumentCredit.creditPaymentAccountId, instrumentCredit.value)
    }

}
