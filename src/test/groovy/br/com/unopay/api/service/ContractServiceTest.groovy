package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractEstablishment
import br.com.unopay.api.model.ContractOrigin
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.filter.ContractFilter
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.data.domain.Page

import static br.com.unopay.api.model.PaymentInstrumentType.DIGITAL_WALLET
import static br.com.unopay.api.model.PaymentInstrumentType.PREPAID_CARD
import br.com.unopay.api.model.Person
import br.com.unopay.api.model.Product
import br.com.unopay.api.network.model.Establishment
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.order.model.OrderType
import br.com.unopay.api.uaa.exception.Errors
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.springframework.beans.factory.annotation.Autowired
import static spock.util.matcher.HamcrestSupport.that

class ContractServiceTest extends SpockApplicationTests {

    @Autowired
    private ContractService service

    @Autowired
    private FixtureCreator fixtureCreator

    @Autowired
    private ContractInstallmentService installmentService

    @Autowired
    private PaymentInstrumentService paymentInstrumentService

    private Hirer hirerUnderTest
    private Contractor contractorUnderTest
    private Product productUnderTest
    private Establishment establishmentUnderTest


    void setup(){
        hirerUnderTest = fixtureCreator.createHirer()
        contractorUnderTest = fixtureCreator.createContractor()
        productUnderTest = fixtureCreator.createProduct()
        establishmentUnderTest = fixtureCreator.createHeadOffice()
    }

    void 'given known contract should find by code'(){
        given:
        def contract = fixtureCreator.createPersistedContract()

        when:
        def found = service.findByCode(contract.code)

        then:
        found
    }

    void 'given unknown code should return error'(){
        given:
        def code = 123L
        when:
        service.findByCode(code)
        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }

    void 'when create a new contract the contract installments should be created'(){
        given:
        Contract contract = createContract()

        when:
        service.create(contract)
        def installments = installmentService.findByContractId(contract.id)

        then:
        !installments.isEmpty()
    }

    void 'given contract without product should not be created'(){
        given:
        Contract contract = createContract()
        contract.getProduct().id = null

        when:
        service.create(contract)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'PRODUCT_REQUIRED'
    }

    void 'new contract should be created'(){
        given:
        Contract contract = createContract()

        when:
        def result  = service.create(contract)

        then:
        assert result.id != null
    }

    def 'given a installment payment order with unknown contract should return error'(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()

        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        Order order = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("person", person)
            add("product", product)
            add("type", OrderType.INSTALLMENT_PAYMENT)
        }})

        when:
        service.markInstallmentAsPaidFrom(order)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }

    void 'when create a contract the annuity should be the same of product'(){
        given:
        Contract contract = createContract()

        when:
        def result  = service.create(contract)

        then:
        assert result.annuity == contract.product.annuity
    }

    void 'when create a contract the member annuity should be the same of product'(){
        given:
        Contract contract = createContract()

        when:
        def result  = service.create(contract)

        then:
        assert result.memberAnnuity == contract.product.memberAnnuity
    }

    void 'when create a contract the paymentInstallments should be the same of product'(){
        given:
        Contract contract = createContract()

        when:
        def result  = service.create(contract)

        then:
        assert result.paymentInstallments == contract.product.paymentInstallments
    }

    void 'when create a contract the membershipFee should be the same of product'(){
        given:
        Contract contract = createContract()

        when:
        def result  = service.create(contract)

        then:
        assert result.membershipFee == contract.product.membershipFee
    }

    void 'given contract with null and default values should create with default values'(){
        given:
        Contract contract = createContract()
        contract = contract.with {
            documentNumberInvoice = null
            origin = null
            situation = null
            it }

        when:
        def result  = service.create(contract)

        then:
        assert result.id != null
        assert result.documentNumberInvoice == hirerUnderTest.documentNumber
        assert result.situation == ContractSituation.ACTIVE
        assert result.origin == ContractOrigin.APPLICATION
    }

    void 'given contract with same code should not be created'(){
        given:
        Contract contract = createContract()

        when:
        service.create(contract)
        service.create(contract.with { id = null; it })

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'CONTRACT_ALREADY_EXISTS'
    }

    void 'given contract with unknown hirer should not be created'(){
        given:
        Contract contract = createContract()
        contract = contract.with {
            hirer = hirerUnderTest.with {id = '112222233' ; it}
            it }

        when:
        service.create(contract)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'HIRER_NOT_FOUND'
    }

    void 'given contract with unknown contractor should not be created'(){
        given:
        Contract contract = createContract()
        contract = contract.with {
            contractor = contractorUnderTest.with {id = '112222233' ; it}
            it }
        when:
        service.create(contract)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACTOR_NOT_FOUND'
    }

    void 'given contract with unknown product should not be created'(){
        given:
        Contract contract = createContract()
        contract = contract.with {
            product = productUnderTest.with {id = '112222233' ; it}
            it }

        when:
        service.create(contract)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }




    void 'known contract should be updated'(){
        given:
        Contract contract = createContract()

        def created  = service.create(contract)
        def newName = 'ContractNew'
        contract.name = newName

        when:
        service.update(created.id, contract)
        def result = service.findById(created.id)

        then:
        assert result.name == newName
    }


    void 'should not update contract situation to canceled'(){
        given:
        Contract contract = createContract()

        def created  = service.create(contract)
        contract.situation = ContractSituation.CANCELLED

        when:
        service.update(created.id, contract)
        def result = service.findById(created.id)

        then:
        assert result.situation != ContractSituation.CANCELLED
    }

    void 'unknown contract should not be updated'(){
        given:
        Contract contract = createContract()

        def newName = 'ContractNew'
        contract.name = newName

        when:
        service.update('', contract)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }


    void 'given contract with unknown hirer should not be updated'(){
        given:
        def knownName = 'myName'
        Contract contract = createContract()

        def created = service.create(contract)

        when:
        service.update(created.id, contract.with { name = knownName
                                                  hirer = hirerUnderTest.with { id = '223455'; it }
                                                it })
        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'HIRER_NOT_FOUND'
    }

    void 'given contract with unknown contractor should not be updated'(){
        given:
        def knownName = 'myName'
        Contract contract = createContract()

        def created = service.create(contract)

        when:
        service.update(created.id, contract.with { name = knownName; contractor = contractorUnderTest.with { id = '1122345'; it }; it })

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACTOR_NOT_FOUND'
    }

    void 'given contract with unknown product should not be updated'(){
        given:
        def knownName = 'myName'
        Contract contract =createContract()

        def created = service.create(contract)

        when:
        service.update(created.id, contract.with {
                                                    name = knownName
                                                    product = productUnderTest.with { id = '11223345'; it }
                                                it })
        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }

    void 'known contract should be found'(){
        given:
        Contract contract = createContract()

        def created  = service.create(contract)
        when:
        def result = service.findById(created.id)

        then:
        assert result != null
    }

    void 'unknown contract should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }

    void 'known contract should be canceled'(){
        given:
        Contract contract = createContract()
        def created  = service.create(contract)
        when:
        service.cancel(created.id)
        def current = service.findById(created.id)

        then:
        current.situation == ContractSituation.CANCELLED
    }

    void 'when cancel a known contract should cancel the payment instruments with same contract product'(){
        given:
        Contract contract = createContract()
        fixtureCreator.createPersistedInstrument(contract.contractor, contract.product, PREPAID_CARD)
        fixtureCreator.createPersistedInstrument(contract.contractor, contract.product, DIGITAL_WALLET)
        def created  = service.create(contract)

        when:
        service.cancel(created.id)
        def instruments = paymentInstrumentService.findByContractorId(contract.contractor.id)

        then:
        that instruments, hasSize(2)
        instruments.findAll {
            it.hasProduct(contract.product) && it.isCanceled()
        }.size() == 2
    }

    void 'when cancel a known contract should not cancel the payment instruments with different contract product'(){
        given:
        Contract contract = createContract()
        def anotherProduct = fixtureCreator.createProduct()
        fixtureCreator.createPersistedInstrument(contract.contractor, anotherProduct, PREPAID_CARD)
        fixtureCreator.createPersistedInstrument(contract.contractor, contract.product, DIGITAL_WALLET)
        def created  = service.create(contract)

        when:
        service.cancel(created.id)
        def instruments = paymentInstrumentService.findByContractorId(contract.contractor.id)

        then:
        that instruments, hasSize(2)
        instruments.findAll {
            it.hasProduct(contract.product) && it.isCanceled()
        }.size() == 1
    }

    void 'unknown contract should not be canceled'(){
        when:
        service.cancel('')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }

    void 'should add establishment in contract'(){
        given:
        Contract contract = createContract()
        ContractEstablishment contractEstablishment = Fixture.from(ContractEstablishment.class).gimme("valid")
        contract = service.create(contract)
        when:
        contractEstablishment = contractEstablishment.with {establishment = establishmentUnderTest; it}
        service.addEstablishments(contract.id,contractEstablishment)
        contract = service.findById(contract.id)
        then:
        that contract.contractEstablishments, hasSize(1)
    }


    void 'should not add establishment in contract if is already in contract'(){
        given:
        Contract contract = createContract()
        ContractEstablishment contractEstablishment = Fixture.from(ContractEstablishment.class).gimme("valid")
        contract = service.create(contract)
        when:
        contractEstablishment = contractEstablishment.with {establishment = establishmentUnderTest; it}
        service.addEstablishments(contract.id,contractEstablishment)
        service.addEstablishments(contract.id,contractEstablishment.with {id =null;it})
        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == Errors.ESTABLISHMENT_ALREADY_IN_CONTRACT.logref
    }

    void 'should remove establishment in contract'(){
        given:
        Contract contract = createContract()
        ContractEstablishment contractEstablishment = Fixture.from(ContractEstablishment.class).gimme("valid")
        contract = service.create(contract)
        contractEstablishment = contractEstablishment.with {establishment = establishmentUnderTest; it}
        contractEstablishment = service.addEstablishments(contract.id,contractEstablishment)
        when:
        service.removeEstablishment(contract.id,contractEstablishment.id)
        contract =service.findById(contract.id)
        then:
        assert contract.contractEstablishments.size() == 0
    }

    void 'should not remove if ContractEstablishment is not found'(){
        given:
        Contract contract = createContract()
        contract = service.create(contract)
        when:
        service.removeEstablishment(contract.id,'1234')
        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_ESTABLISHMENT_NOT_FOUND'
    }


    void 'given contractEstablishment with null and default values should create with default values'(){
        given:
        Contract contract = createContract()
        ContractEstablishment contractEstablishment = Fixture.from(ContractEstablishment.class).gimme("valid")
        contract = service.create(contract)
        when:
        contractEstablishment = contractEstablishment.with {establishment = establishmentUnderTest;origin=null; it}
        service.addEstablishments(contract.id,contractEstablishment)
        contract = service.findById(contract.id)
        then:
        def result = contract.contractEstablishments.first()
        assert result.id != null
        assert result.contract.id == contract.id
        assert result.origin == ContractOrigin.APPLICATION
    }

    void 'should return contracts by establishment'(){
        given:
        Contract contract = createContract()
        ContractEstablishment contractEstablishment = Fixture.from(ContractEstablishment.class).gimme("valid")
        contract = service.create(contract)
        when:
        contractEstablishment = contractEstablishment.with {establishment = establishmentUnderTest; it}
        service.addEstablishments(contract.id,contractEstablishment)
        List<Contract> contracts = service.findByEstablishmentId(contractEstablishment.establishment.id)

        then:
        that contracts, hasSize(1)
    }

    void 'should return valid contract contracts'(){
        given:
        Contract contract = createContract()
        ContractEstablishment contractEstablishment = Fixture.from(ContractEstablishment.class).gimme("valid")
        contract.situation = ContractSituation.ACTIVE
        contract = service.create(contract)
        when:
        contractEstablishment = contractEstablishment.with {establishment = establishmentUnderTest; it}
        service.addEstablishments(contract.id,contractEstablishment)
        def contractorId=contract.contractor.id
        def productCode = contract.product.code
        List<Contract> contracts = service.getContractorValidContracts(contractorId, productCode)

        then:
        that contracts, hasSize(1)
    }

    void 'establishment without contracts should not be founded'(){
        when:
        service.findByEstablishmentId(establishmentUnderTest.id)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_ESTABLISHMENT_NOT_FOUND'
    }

    void 'given a known product code and contractor id as filter should return contract for a logged network'(){
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        ContractFilter filter = new ContractFilter()
        filter.product = contract.productCode()
        filter.accreditedNetwork = contract.productNetworkId()
        filter.contractor = contract.contractorId()

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<Contract> contracts = service.findByFilter(filter, page)

        then:
        assert contracts.content.size() == 1
    }

    void 'given a unknown product code as filter should not return contract for a logged network'(){
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        ContractFilter filter = new ContractFilter()
        filter.product = "00000"
        filter.accreditedNetwork = contract.productNetworkId()
        filter.contractor = contract.contractorId()

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<Contract> contracts = service.findByFilter(filter, page)

        then:
        assert contracts.content.size() == 0
    }

    void 'given a unknown contractor id as filter should not return contract for a logged network'(){
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        ContractFilter filter = new ContractFilter()
        filter.product = contract.productCode()
        filter.accreditedNetwork = contract.productNetworkId()
        filter.contractor = "00000"

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<Contract> contracts = service.findByFilter(filter, page)

        then:
        assert contracts.content.size() == 0
    }

    void 'given a known product code and contractor id as filter should not return contract for a unlogged network'(){
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        ContractFilter filter = new ContractFilter()
        filter.product = contract.productCode()
        filter.accreditedNetwork = fixtureCreator.createNetwork()
        filter.contractor = contract.contractorId()

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<Contract> contracts = service.findByFilter(filter, page)

        then:
        assert contracts.content.size() == 0
    }

    private Contract createContract() {
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest
            serviceTypes = productUnderTest.serviceTypes
            it
        }
        contract
    }

}
