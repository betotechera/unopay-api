package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.ServiceType
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractorInstrumentCredit
import br.com.unopay.api.model.CreditPaymentAccount
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired

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
    SetupCreator setupCreator

    Contractor contractorUnderTest
    Contract contractUnderTest
    PaymentInstrument paymentInstrumentUnderTest
    CreditPaymentAccount creditPaymentAccountUnderTest

    void setup(){
        contractorUnderTest = setupCreator.createContractor()
        contractUnderTest = setupCreator.createPersistedContract(contractorUnderTest)
        paymentInstrumentUnderTest = setupCreator
                                    .createPaymentInstrumentWithProduct(contractUnderTest.product, contractorUnderTest)
        creditPaymentAccountUnderTest = setupCreator.createCreditPaymentAccountFromContract(contractUnderTest)
    }

    def 'instrument credit should be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        when:
        ContractorInstrumentCredit created = service.insert(contractorUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.id != null
    }

    def 'should create instrument credit with contractor contract'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with {
            paymentInstrument = paymentInstrumentUnderTest
        }

        when:
        ContractorInstrumentCredit created = service.insert(contractorUnderTest.id, instrumentCredit)
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
        ContractorInstrumentCredit created = service.insert(contractorUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.id != null
        result.paymentInstrument in paymentInstruments
    }

    def 'instrument credit with credit payment instrument of another hirer should not be inserted'(){
        given:
        def contractor = setupCreator.createContractor()
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with {
            paymentInstrument = setupCreator
                    .createPaymentInstrumentWithProduct(contractUnderTest.product, contractor,'newNumber')
        }
        when:
       service.insert(contractorUnderTest.id, instrumentCredit)

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
        ContractorInstrumentCredit created = service.insert(contractorUnderTest.id, instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.id != null
        result.creditPaymentAccount in creditPaymentAccounts
    }

    def 'instrument credit with credit payment account belongs to another hirer should not be inserted'(){
        given:
        def contractor = setupCreator.createContractor()
        def anotherContract = setupCreator.createContract(contractor, contractUnderTest.product)
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with {
            creditPaymentAccount = setupCreator.createCreditPaymentAccountFromContract(anotherContract)
        }
        when:
        service.insert(contractorUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_HIRER'
    }

    def 'instrument credit with credit payment account with product different of contract product should not be inserted'(){
        given:
        def myContract = contractService.findByContractorId(contractorUnderTest.id)
        List<CreditPaymentAccount> creditPaymentAccounts = creditPaymentAccountService
                                                                .findByHirerDocument(myContract.hirer.documentNumber)
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with {
            creditPaymentAccount = creditPaymentAccounts.find()?.with {
                    product = setupCreator.createProduct('DDBB', myContract.product.paymentRuleGroup); it
            }
        }
        when:
        service.insert(contractorUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_PRODUCT'
    }

    def 'instrument credit with credit payment account with service different of contract service should not be inserted'(){
        given:
        def myContract = contractService.findByContractorId(contractorUnderTest.id)
        List<CreditPaymentAccount> creditPaymentAccounts = creditPaymentAccountService
                .findByHirerDocument(myContract.hirer.documentNumber)
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with {
            creditPaymentAccount = creditPaymentAccounts.find()?.with {
                serviceType = ServiceType.values().find { !(it in myContract.serviceType) }
                it
            }
        }
        when:
        service.insert(contractorUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_SERVICE'
    }

    def 'instrument credit with service different of contract service should not be inserted'(){
        given:
        List<CreditPaymentAccount> creditPaymentAccounts = creditPaymentAccountService
                .findByHirerDocument(contractUnderTest.hirer.documentNumber)
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with {
            serviceType = ServiceType.values().find { !(it in creditPaymentAccounts.serviceType) }
        }
        when:
        service.insert(contractorUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'SERVICE_NOT_ACCEPTED'
    }

    def 'given an hirer product code different of payment instrument product code should not be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit = createInstrumentCredit()
        instrumentCredit.with {
            paymentInstrument.product.code = '5464664'
        }

        when:
        service.insert(contractorUnderTest.id, instrumentCredit)

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
        service.insert(contractorUnderTest.id, instrumentCredit)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'PRODUCT_ID_NOT_MET'
    }

    def 'instrument with unknown contract credit should not be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit =  createInstrumentCredit()

        when:
        service.insert('', instrumentCredit)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }

    private ContractorInstrumentCredit createInstrumentCredit() {
        ContractorInstrumentCredit instrumentCredit = Fixture.from(ContractorInstrumentCredit.class).gimme("toPersist")
        instrumentCredit.with {
            paymentInstrument = paymentInstrumentUnderTest
            creditPaymentAccount = creditPaymentAccountUnderTest
            serviceType = contractUnderTest.serviceType.find()
        }
        instrumentCredit
    }
}
