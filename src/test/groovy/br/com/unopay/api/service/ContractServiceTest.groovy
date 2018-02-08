package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.model.HirerNegotiation
import br.com.unopay.api.bacen.service.ContractorService
import br.com.unopay.api.bacen.util.FixtureCreator
import static br.com.unopay.api.function.FixtureFunctions.*
import br.com.unopay.api.function.FixtureFunctions
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractEstablishment
import br.com.unopay.api.model.ContractOrigin
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.Person
import br.com.unopay.api.model.Product
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.order.model.OrderType
import br.com.unopay.api.uaa.exception.Errors
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.uaa.service.UserDetailService
import br.com.unopay.api.util.Time
import br.com.unopay.bootcommons.exception.BadRequestException
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
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

    @Autowired
    ContractInstallmentService installmentService

    @Autowired
    ResourceLoader resourceLoader

    Hirer hirerUnderTest
    Contractor contractorUnderTest
    Product productUnderTest
    Establishment establishmentUnderTest


    void setup(){
        hirerUnderTest = fixtureCreator.createHirer()
        contractorUnderTest = fixtureCreator.createContractor()
        productUnderTest = fixtureCreator.createProduct()
        establishmentUnderTest = fixtureCreator.createHeadOffice()
    }

    void """given known negotiation for contract product and hirer
            when create deal close with hirer should be created"""(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        def hirer = fixtureCreator.createHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        fixtureCreator.createNegotiation(hirer, product)

        when:
        def created  = service.dealClose(person, product.code, hirer.documentNumber)
        def result = service.findById(created.id)

        then:
        result
    }

    void """given unknown negotiation for contract product and hirer
            when deal close with hirer should not be created"""(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        def hirer = fixtureCreator.createHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        service.dealClose(person, product.code, hirer.documentNumber)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'HIRER_NEGOTIATION_NOT_FOUND'
    }


    void """given known negotiation for contract product and hirer
            when dealClose should be created with negotiation installment value"""(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        def hirer = fixtureCreator.createHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def negotiation = fixtureCreator.createNegotiation(hirer, product)

        when:
        def created  = service.dealClose(person, product.code, hirer.documentNumber)
        def result = service.findById(created.id)

        then:
        result.installmentValue() == negotiation.installmentValue
    }

    void """given known negotiation for contract product and hirer
            when dealClose should be created with negotiation installments"""(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        def hirer = fixtureCreator.createHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def negotiation = fixtureCreator.createNegotiation(hirer, product)

        when:
        def created  = service.dealClose(person, product.code, hirer.documentNumber)
        def result = installmentService.findByContractId(created.id)

        then:
        result.size() == negotiation.installments
    }

    void """given known negotiation for contract product and hirer with past effective date
            when dealClose should be created without past installments"""(){
        given:
        def monthsAgo = 5
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        def hirer = fixtureCreator.createHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def negotiation = fixtureCreator.createNegotiation(hirer, product, instant("5 months ago"))

        when:
        def created  = service.dealClose(person, product.code, hirer.documentNumber)
        def result = installmentService.findByContractId(created.id)

        then:
        result.size() == negotiation.installments - monthsAgo
    }

    void """given known negotiation for contract product and hirer with free installments
            when dealClose should be created firsts free contract installments
            with negotiation installment number"""(){
        given:
        def freeInstallmentQuantity = 3
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        def hirer = fixtureCreator.createHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
            add("freeInstallmentQuantity", freeInstallmentQuantity)
        }})

        when:
        def created  = service.dealClose(person, product.code, hirer.documentNumber)
        def result = installmentService.findByContractId(created.id)

        then:
        def installments = 1..freeInstallmentQuantity
        installments.every { number ->
            result.find { it.installmentNumber == number }?.value == 0.0
        }
    }

    void """given known negotiation for contract product and hirer with free installments
            when dealClose should be created lasts contract installments
            without discount"""(){
        given:
        def freeInstallmentQuantity = 4
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        def hirer = fixtureCreator.createHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        HirerNegotiation negotiation =  Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
            add("freeInstallmentQuantity", freeInstallmentQuantity - 1)
        }})

        when:
        def created  = service.dealClose(person, product.code, hirer.documentNumber)
        def result = installmentService.findByContractId(created.id)

        then:
        def installments = freeInstallmentQuantity..negotiation.installments
        installments.every { number ->
            result.find { it.installmentNumber == number }.value == negotiation.installmentValue
        }
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

    void 'given valid person and product should deal close'(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        def result  = service.dealCloseWithIssuerAsHirer(person, product.code)

        then:
        assert result.id != null
    }

    def 'when deal close for known contractor should return error'(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        def contractor = fixtureCreator.createContractor()

        when:
        service.dealCloseWithIssuerAsHirer(contractor.person, product.code)

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'EXISTING_CONTRACTOR'
    }


    def 'given a installment payment order should mark next contract installment as paid'(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()

        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        Order order = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("person", person)
            add("product", product)
            add("type", OrderType.INSTALLMENT_PAYMENT)
        }})
        service.dealCloseWithIssuerAsHirer(person, product.code)

        when:
        service.markInstallmentAsPaidFrom(order)
        def result = service.findByContractorAndProduct(person.documentNumber(), product.id)

        then:
        def installment = result.get().contractInstallments.find {
            it.installmentNumber == 2
        }
        timeComparator.compare(installment.paymentDateTime, new Date()) == 0
    }

    def 'given a installment payment order with unknown contract should return error'(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()

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

    void 'when deal close should create contract'(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(person, product.code)
        Contract result  = service.findById(contract.getId())

        then:
        result != null
    }

    void 'when deal close the contract period should be of one year'(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(person, product.code)
        Contract result  = service.findById(contract.getId())

        then:
        result.begin == Time.create()
        result.end == Time.createDateTime().plusYears(1).toDate()
    }

    void 'when deal close should create user'(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(person, product.code)
        UserDetail result  = userDetailService.getByEmail(contract.contractor.person.physicalPersonDetail.email)

        then:
        result != null
    }

    def 'when deal close should create user with contractor user type'(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(person, product.code)
        UserDetail result  = userDetailService.getByEmail(contract.contractor.person.physicalPersonDetail.email)

        then:
        result.type.name == 'CONTRATADO'
    }

    void 'when deal close should create contract with product'(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(person, product.code)
        Contract result  = service.findById(contract.getId())

        then:
        result.product.id == product.id
    }

    void 'when deal close should create contract contractor'(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(person, product.code)
        Contractor result  = contractorService.getById(contract.getContractor().getId())

        then:
        result != null
    }


    void 'given unknown hirer when deal close should create contract with product issuer how hirer'(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        service.dealCloseWithIssuerAsHirer(person, product.code)
        def result  = service.findByHirerDocument(product.getIssuer().documentNumber())

        then:
        that result, hasSize(1)
    }

    void 'given known hirer when deal close should create contract with him'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        fixtureCreator.createNegotiation(hirer, product)

        when:
        service.dealClose(person, product.code, hirer.documentNumber)
        def result  = service.findByHirerDocument(hirer.documentNumber)

        then:
        that result, hasSize(1)
    }

    void 'given product with member ship fee when deal close should not mark installment as paid'(){
        given:
        BigDecimal memberShipFee = 20.0
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer(memberShipFee)
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(person, product.code)
        def result  = installmentService.findByContractId(contract.getId())
        then:
        result.every { it.paymentDateTime == null && it.paymentValue == null}
    }

    void 'given product without member ship fee when deal close should mark first installment as paid'(){
        given:
        def memberShipFee = null
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer(memberShipFee)
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(person, product.code)
        def result  = installmentService.findByContractId(contract.getId())
        then:
        def installment = result.sort { it.installmentNumber }.find()
        timeComparator.compare(installment.paymentDateTime, new Date()) == 0
        installment.paymentValue == product.installmentValue
    }

    def'given known hirer should deal close from csv with persons in file'(){
        given:
        def hirerDocument = "75136542000195"
        List<Product> products = Fixture.from(Product.class).uses(jpaProcessor).gimme(2, "valid", new Rule(){{
            add("code", uniqueRandom("5102", "5105"))
        }})

        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical", new Rule(){{
            add("document.number", hirerDocument)
        }})

        Hirer hirer = Fixture.from(Hirer.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", person)
        }})

        Resource csv  = resourceLoader.getResource("classpath:/clients.csv")
        MultipartFile file = new MockMultipartFile('file', csv.getInputStream())
        fixtureCreator.createNegotiation(hirer, products.find())
        fixtureCreator.createNegotiation(hirer, products.last())

        when:
        service.dealCloseFromCsv(hirerDocument, file)
        def result = service.findByHirerDocument(hirerDocument)

        then:
        that result, hasSize(2)
    }

    def'given known hirer and invalid person information should not be deal close from csv with persons in file'(){
        given:
        def hirerDocument = "75136542000195"
        Fixture.from(Product.class).uses(jpaProcessor).gimme(2, "valid", new Rule(){{
            add("code", uniqueRandom("5102", "5105"))
        }})

        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical", new Rule(){{
            add("document.number", hirerDocument)
        }})

        Fixture.from(Hirer.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", person)
        }})

        Resource csv  = resourceLoader.getResource("classpath:/invalidClients.csv")
        MultipartFile file = new MockMultipartFile('file', csv.getInputStream())

        when:
        service.dealCloseFromCsv(hirerDocument, file)

        then:
        def ex = thrown(BadRequestException)
        assert ex.errors.any { it.logref == 'gender' }
        assert ex.errors.any { it.logref == 'document' }
    }

    void 'when deal close should create contractor payment instrument'(){
        given:
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(person, product.code)
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
