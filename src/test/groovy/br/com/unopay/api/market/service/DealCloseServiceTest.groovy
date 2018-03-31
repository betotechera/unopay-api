package br.com.unopay.api.market.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.service.AuthorizedMemberService
import br.com.unopay.api.bacen.service.ContractorService
import br.com.unopay.api.bacen.util.FixtureCreator
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.market.model.AuthorizedMemberCandidate
import br.com.unopay.api.market.model.HirerNegotiation
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.DealClose
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.Person
import br.com.unopay.api.model.Product
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.order.model.OrderType
import br.com.unopay.api.service.ContractInstallmentService
import br.com.unopay.api.service.ContractService
import br.com.unopay.api.service.PaymentInstrumentService
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.uaa.service.UserDetailService
import br.com.unopay.api.util.Time
import br.com.unopay.bootcommons.exception.BadRequestException
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import static spock.util.matcher.HamcrestSupport.that

class DealCloseServiceTest extends SpockApplicationTests{

    @Autowired
    private ContractorService contractorService
    @Autowired
    private PaymentInstrumentService instrumentService
    @Autowired
    private UserDetailService userDetailService
    @Autowired
    private AuthorizedMemberService authorizedMemberService
    @Autowired
    private FixtureCreator fixtureCreator
    @Autowired
    private DealCloseService service
    @Autowired
    private ContractService contractService
    @Autowired
    private ContractInstallmentService installmentService

    @Autowired
    private ResourceLoader resourceLoader

    void 'when deal close should create contract'(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(new DealClose(person, product.code))
        Contract result  = contractService.findById(contract.getId())

        then:
        result != null
    }

    void 'when deal close the contract period should be of one year'(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(new DealClose(person, product.code))
        Contract result  = contractService.findById(contract.getId())

        then:
        result.begin == Time.create()
        result.end == Time.createDateTime().plusYears(1).toDate()
    }

    void 'when deal close should create user'(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(new DealClose(person, product.code))
        UserDetail result  = userDetailService.getByEmail(contract.contractor.person.physicalPersonDetail.email)

        then:
        result != null
    }

    def 'when deal close should create user with contractor user type'(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(new DealClose(person, product.code))
        UserDetail result  = userDetailService.getByEmail(contract.contractor.person.physicalPersonDetail.email)

        then:
        result.type.name == 'CONTRATADO'
    }

    void 'when deal close should create contract with product'(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(new DealClose(person, product.code))
        Contract result  = contractService.findById(contract.getId())

        then:
        result.product.id == product.id
    }

    void 'when deal close should create contract contractor'(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(new DealClose(person, product.code))
        Contractor result  = contractorService.getById(contract.getContractor().getId())

        then:
        result != null
    }


    void 'given unknown hirer when deal close should create contract with product issuer how hirer'(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        service.dealCloseWithIssuerAsHirer(new DealClose(person, product.code))
        def result  = contractService.findByHirerDocument(product.getIssuer().documentNumber())

        then:
        that result, hasSize(1)
    }

    void 'given known hirer when deal close should create contract with him'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        fixtureCreator.createNegotiation(hirer, product)

        when:
        service.doDealClose(person, product.code, hirer.documentNumber)
        def result  = contractService.findByHirerDocument(hirer.documentNumber)

        then:
        that result, hasSize(1)
    }

    void 'given order with authorized member candidates when deal close should be created authorized members'(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def candidates = Fixture.from(AuthorizedMemberCandidate).gimme(2, "valid") as Set

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(new DealClose(person, product.code, candidates))
        def result  = authorizedMemberService.countByContract(contract.id)

        then:
        result == 2
    }

    void 'given order with authorized member candidates when deal close should create contract with authorized members total'(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def candidates = Fixture.from(AuthorizedMemberCandidate).gimme(2, "valid") as Set

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(new DealClose(person, product.code, candidates))

        then:
        contract.memberTotal == 2
    }

    void 'given product with member ship fee when deal close should not mark installment as paid'(){
        given:
        BigDecimal memberShipFee = 20.0
        def product = fixtureCreator.createProductWithSameIssuerOfHirer(memberShipFee)
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(new DealClose(person, product.code))
        def result  = installmentService.findByContractId(contract.getId())
        then:
        result.every { it.paymentDateTime == null && it.paymentValue == null}
    }

    void 'given product without member ship fee when deal close should mark first installment as paid'(){
        given:
        def memberShipFee = null
        def product = fixtureCreator.createProductWithSameIssuerOfHirer(memberShipFee)
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(new DealClose(person, product.code))
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
        def result = contractService.findByHirerDocument(hirerDocument)

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
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        Contract contract =  service.dealCloseWithIssuerAsHirer(new DealClose(person, product.code))
        List<PaymentInstrument> result  = instrumentService.findByContractorId(contract.getContractor().getId())

        then:
        !result.isEmpty()
    }

    void 'given valid person and product should deal close'(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        def result  = service.dealCloseWithIssuerAsHirer(new DealClose(person, product.code))

        then:
        assert result.id != null
    }

    def 'when deal close for known contractor should return error'(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        def contractor = fixtureCreator.createContractor()

        when:
        service.dealCloseWithIssuerAsHirer(new DealClose(contractor.person, product.code))

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'EXISTING_CONTRACTOR'
    }


    def 'given a installment payment order should mark next contract installment as paid'(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()

        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        Order order = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("person", person)
            add("product", product)
            add("type", OrderType.INSTALLMENT_PAYMENT)
        }})
        service.dealCloseWithIssuerAsHirer(new DealClose(person, product.code))

        when:
        contractService.markInstallmentAsPaidFrom(order)
        def result = contractService.findByContractorAndProduct(person.documentNumber(), product.id)

        then:
        def installment = result.get().contractInstallments.find {
            it.installmentNumber == 2
        }
        timeComparator.compare(installment.paymentDateTime, new Date()) == 0
    }

    void """given known negotiation for contract product and hirer with past effective date
            when dealClose should be created without past installments"""(){
        given:
        def monthsAgo = 5
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        def hirer = fixtureCreator.createHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        installmentService.setCurrentDate(new Date())
        def negotiation = fixtureCreator.createNegotiation(hirer, product, instant("5 months ago"))

        when:
        def created  = service.doDealClose(person, product.code, hirer.documentNumber)
        def result = installmentService.findByContractId(created.id)

        then:
        result.size() == negotiation.installments - monthsAgo
    }

    void """given known negotiation for contract product and hirer with free installments
            when dealClose should be created firsts free contract installments
            with negotiation installment number"""(){
        given:
        def freeInstallmentQuantity = 3
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        def hirer = fixtureCreator.createHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
            add("freeInstallmentQuantity", freeInstallmentQuantity)
        }})

        when:
        def created  = service.doDealClose(person, product.code, hirer.documentNumber)
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
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        def hirer = fixtureCreator.createHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        HirerNegotiation negotiation =  Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
            add("freeInstallmentQuantity", freeInstallmentQuantity - 1)
        }})

        when:
        def created  = service.doDealClose(person, product.code, hirer.documentNumber)
        def result = installmentService.findByContractId(created.id)

        then:
        def installments = freeInstallmentQuantity..negotiation.installments
        installments.every { number ->
            result.find { it.installmentNumber == number }.value == negotiation.installmentValue
        }
    }

    void """given known negotiation for contract product and hirer
            when create deal close with hirer should be created"""(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        def hirer = fixtureCreator.createHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        fixtureCreator.createNegotiation(hirer, product)

        when:
        def created  = service.doDealClose(person, product.code, hirer.documentNumber)
        def result = contractService.findById(created.id)

        then:
        result
    }

    void """given unknown negotiation for contract product and hirer
            when deal close with hirer should not be created"""(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        def hirer = fixtureCreator.createHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        when:
        service.doDealClose(person, product.code, hirer.documentNumber)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'HIRER_NEGOTIATION_NOT_FOUND'
    }


    void """given known negotiation for contract product and hirer
            when dealClose should be created with negotiation installment value"""(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        def hirer = fixtureCreator.createHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def negotiation = fixtureCreator.createNegotiation(hirer, product)

        when:
        def created  = service.doDealClose(person, product.code, hirer.documentNumber)
        def result = contractService.findById(created.id)

        then:
        result.installmentValue() == negotiation.installmentValue
    }

    void """given known negotiation for contract product and hirer
            when dealClose should be created with negotiation installments"""(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        def hirer = fixtureCreator.createHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def negotiation = fixtureCreator.createNegotiation(hirer, product)

        when:
        def created  = service.doDealClose(person, product.code, hirer.documentNumber)
        def result = installmentService.findByContractId(created.id)

        then:
        result.size() == negotiation.installments
    }
}
