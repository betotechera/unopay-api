package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.service.ContractorService
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractEstablishment
import br.com.unopay.api.model.ContractOrigin
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.Person
import br.com.unopay.api.model.Product
import br.com.unopay.api.uaa.exception.Errors
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.uaa.model.UserType
import br.com.unopay.api.uaa.service.UserDetailService
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import static org.hamcrest.Matchers.hasSize
import org.springframework.beans.factory.annotation.Autowired
import static spock.util.matcher.HamcrestSupport.that

class ContractServiceTest extends SpockApplicationTests {

    @Autowired
    ContractService service

    @Autowired
    ContractorService contractorService

    @Autowired
    PaymentInstrumentService instrumentService

    @Autowired
    UserDetailService userDetailService

    @Autowired
    FixtureCreator fixtureCreator

    Hirer hirerUnderTest
    Contractor contractorUnderTest
    Product productUnderTest
    Establishment establishmentUnderTest
    ContractInstallmentService installmentServiceMock = Mock(ContractInstallmentService)


    void setup(){
        hirerUnderTest = fixtureCreator.createHirer()
        contractorUnderTest = fixtureCreator.createContractor()
        productUnderTest = fixtureCreator.createProduct()
        establishmentUnderTest = fixtureCreator.createHeadOffice()
        service.installmentService = installmentServiceMock
    }

    void 'when create a new contract the contract installments should be created'(){
        given:
        Contract contract = createContract()

        when:
        service.create(contract)

        then:
        1 * installmentServiceMock.create(_)
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

    void 'should create from person and product'(){
        given:
        def hirer = fixtureCreator.createHirer()
        Person issuerPerson = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical", new Rule(){{
            add("document.number", hirer.documentNumber)
        }})
        Issuer issuer = Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", issuerPerson)
        }})
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        Product product = Fixture.from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issuer", issuer)
        }})

        when:
        def result  = service.dealClose(person, product.code)

        then:
        assert result.id != null
    }


    void 'when deal close should create contract'(){
        given:
        def hirer = fixtureCreator.createHirer()
        Person issuerPerson = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical", new Rule(){{
            add("document.number", hirer.documentNumber)
        }})
        Issuer issuer = Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", issuerPerson)
        }})
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        Product product = Fixture.from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issuer", issuer)
        }})

        when:
        Contract contract =  service.dealClose(person, product.code)
        Contract result  = service.findById(contract.getId())

        then:
        result != null
    }

    void 'when deal close should create user'(){
        given:
        def hirer = fixtureCreator.createHirer()
        Person issuerPerson = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical", new Rule(){{
            add("document.number", hirer.documentNumber)
        }})
        Issuer issuer = Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", issuerPerson)
        }})

        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        Product product = Fixture.from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issuer", issuer)
        }})

        when:
        Contract contract =  service.dealClose(person, product.code)
        UserDetail result  = userDetailService.getByEmail(contract.contractor.person.physicalPersonDetail.email)

        then:
        result != null
    }

    void 'when deal close should create contract with product'(){
        given:
        def hirer = fixtureCreator.createHirer()
        Person issuerPerson = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical", new Rule(){{
            add("document.number", hirer.documentNumber)
        }})
        Issuer issuer = Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", issuerPerson)
        }})
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        Product product = Fixture.from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issuer", issuer)
        }})

        when:
        Contract contract =  service.dealClose(person, product.code)
        Contract result  = service.findById(contract.getId())

        then:
        result.product.id == product.id
    }

    void 'when deal close should create contract contractor'(){
        given:
        def hirer = fixtureCreator.createHirer()
        Person issuerPerson = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical", new Rule(){{
            add("document.number", hirer.documentNumber)
        }})
        Issuer issuer = Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", issuerPerson)
        }})
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        Product product = Fixture.from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issuer", issuer)
        }})

        when:
        Contract contract =  service.dealClose(person, product.code)
        Contractor result  = contractorService.getById(contract.getContractor().getId())

        then:
        result != null
    }

    void 'when deal close should create contractor payment instrument'(){
        given:
        def hirer = fixtureCreator.createHirer()
        Person issuerPerson = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical", new Rule(){{
            add("document.number", hirer.documentNumber)
        }})
        Issuer issuer = Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", issuerPerson)
        }})
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        Product product = Fixture.from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issuer", issuer)
        }})

        when:
        Contract contract =  service.dealClose(person, product.code)
        List<PaymentInstrument> result  = instrumentService.findByContractorId(contract.getContractor().getId())

        then:
        !result.isEmpty()
    }

    void 'when create a contract the annuity should be the same of product'(){
        given:
        Contract contract = createContract()

        when:
        def result  = service.create(contract)

        then:
        assert result.annuity == contract.product.annuity
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

    void 'known contract should be deleted'(){
        given:
        Contract contract = createContract()
        def created  = service.create(contract)
        when:
        service.delete(created.id)
        service.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }

    void 'unknown contract should not be deleted'(){
        when:
        service.delete('')

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
        def establishmentId = contractEstablishment.establishment.id
        def services = [contract.serviceType.find()] as Set
        List<Contract> contracts = service.getContractorValidContracts(contractorId, establishmentId, services)

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

    private Contract createContract() {
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceTypes
            it
        }
        contract
    }
}
