package br.com.unopay.api.order.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.config.Queues
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractInstallment
import br.com.unopay.api.model.Person
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.order.model.OrderStatus
import br.com.unopay.api.order.model.OrderType
import br.com.unopay.api.service.ContractInstallmentService
import br.com.unopay.api.service.ContractService
import br.com.unopay.api.service.PersonService
import br.com.unopay.api.util.Rounder
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnauthorizedException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class OrderServiceTest extends SpockApplicationTests{

    @Autowired
    OrderService service

    @Autowired
    PersonService personService

    @Autowired
    ContractInstallmentService installmentService

    @Autowired
    ContractService contractService

    @Autowired
    ContractorInstrumentCreditService instrumentCreditService

    @Autowired
    FixtureCreator fixtureCreator

    Contract contractUnderTest
    ContractInstallment installmentUnderTest

    Notifier notifierMock = Mock(Notifier)

    def setup(){
        contractUnderTest = fixtureCreator.createPersistedContract()
        installmentService.create(contractUnderTest)
        installmentUnderTest = installmentService.findByContractId(contractUnderTest.id).find()
        service.notifier = notifierMock
    }

    def 'a valid order with known person should be created'(){
        given:
        def creditOrder = createOrder()

        when:
        Order created = service.save(creditOrder)
        Order result = service.findById(created.id)

        then:
        result != null
    }

    def 'given a credit order with paid status and credit type should call credit service'(){
        given:
        Contractor contractor = fixtureCreator.createContractor("physical")
        def paid = createPersistedOrder(contractor, OrderType.CREDIT, OrderStatus.PAID)

        when:
        service.process(paid)

        then:
        def result = instrumentCreditService.findByContractorId(contractor.id)
        result.availableBalance == paid.value
    }

    def 'given a adhesion order with paid status should create contract and mark installment as paid'(){
        given:
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def paid = createPersistedAdhesionOrder(person)

        when:
        service.process(paid)

        then:
        Optional<Contract> contract = contractService.findByContractorAndProduct(person.documentNumber(), paid.productId())
        contract.isPresent()
        def result = installmentService.findByContractId(contract.get().id)
        result.sort{ it.installmentNumber }.find().paymentValue == paid.value
    }

    def 'given a installment payment order with paid status should mark installment as paid'(){
        given:
        Contractor contractor = fixtureCreator.createContractor("physical")
        def paid = createPersistedOrder(contractor, OrderType.INSTALLMENT_PAYMENT, OrderStatus.PAID)

        when:
        service.process(paid)

        then:
        def result = installmentService.findByContractId(paid.contractId())
        result.sort{ it.installmentNumber }.find().paymentValue == paid.value
    }

    def 'given a known credit order with status waiting payment should update to paid status'(){
        given:
        Contractor contractor = fixtureCreator.createContractor("physical")
        def orderA = createPersistedOrder(contractor, OrderType.CREDIT, OrderStatus.WAITING_PAYMENT)

        Order orderB = Fixture.from(Order.class).gimme("valid", new Rule() {{
            add("status", OrderStatus.PAID)
            add("contract", orderA.contract)
        }})

        when:
        service.update(orderA.id, orderB)
        def result = service.findById(orderA.id)

        then:
        result.status == OrderStatus.PAID
    }

    def 'given a known credit order with status waiting payment when update status to paid should insert credit to payment instrument'() {
        given:
        Contractor contractor = fixtureCreator.createContractor("physical")

        def orderA = createPersistedOrder(contractor, OrderType.CREDIT, OrderStatus.WAITING_PAYMENT)

        Order orderB = Fixture.from(Order.class).gimme("valid", new Rule() {{
            add("status", OrderStatus.PAID)
            add("contract", orderA.contract)
        }})

        when:

        service.update(orderA.id, orderB)
        def credit = instrumentCreditService.findByContractorId(contractor.id)

        then:
        orderA.value == credit.value

    }

    def 'given a known order with status canceled when trying to update should return error'(){
        given:
        Order knownOrder = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("status", OrderStatus.CANCELED)
        }})

        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        service.update(knownOrder.id, order)

        then:
        def ex = thrown(UnauthorizedException)
        ex.errors.first().logref == 'UNABLE_TO_UPDATE_ORDER_STATUS'

    }

    def 'given a unknown order with status waiting payment should return error'(){
        given:
        Order unknownOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("status", OrderStatus.WAITING_PAYMENT)
        }})

        Order order = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("status", OrderStatus.PAID)
        }})

        when:
        service.update(unknownOrder.id, order)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ORDER_NOT_FOUND'
    }

    def 'given a known contractor and adhesion order should return error'(){
        given:
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProduct()
        fixtureCreator.createInstrumentToProduct(product, contractor)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("type", OrderType.ADHESION)
            add("paymentInstrument", null)
        }})

        when:
        service.create(creditOrder)

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'EXISTING_CONTRACTOR'
    }

    def 'given a known contractor and Credit order without payment instrument should return error'(){
        given:
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProduct()
        fixtureCreator.createInstrumentToProduct(product, contractor)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("type", OrderType.CREDIT)
            add("paymentInstrument", null)
        }})

        when:
        service.create(creditOrder)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'PAYMENT_INSTRUMENT_REQUIRED'
    }

    @Unroll
    'given a known contractor and #type order without contract should return error'(){
        given:
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProduct()
        def instrument = fixtureCreator.createInstrumentToProduct(product, contractor)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("type", OrderType.CREDIT)
            add("paymentInstrument", instrument)
            add("contract", null)
        }})

        when:
        service.create(creditOrder)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CONTRACT_REQUIRED'

        where:
        _ | type
        _ | OrderType.CREDIT
        _ | OrderType.INSTALLMENT_PAYMENT
    }

    def 'given a known contractor and Credit order without value should return error'(){
        given:
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProduct()
        def instrument = fixtureCreator.createInstrumentToProduct(product, contractor)
        fixtureCreator.createInstrumentToProduct(product, contractor)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("type", OrderType.CREDIT)
            add("paymentInstrument", instrument)
            add("value", null)
        }})

        when:
        service.create(creditOrder)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'VALUE_REQUIRED'
    }

    def 'given a known contractor and INSTALLMENT_PAYMENT order without payment instrument should be created'(){
        given:
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProduct()
        fixtureCreator.createInstrumentToProduct(product, contractor)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("type", OrderType.INSTALLMENT_PAYMENT)
            add("contract", contractUnderTest)
            add("paymentInstrument", null)
        }})
        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result.value != null
    }

    def 'given a unknown contractor and ADHESION order without payment instrument should be created'(){
        given:
        Person person =  Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def product = fixtureCreator.createProduct()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", person)
            add("product", product)
            add("type", OrderType.ADHESION)
            add("contract", contractUnderTest)
            add("paymentInstrument", null)
        }})
        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result.value != null
    }

    def 'given a unknown contractor and ADHESION order without value should be created'(){
        given:
        Person person =  Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def product = fixtureCreator.createProduct()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", person)
            add("product", product)
            add("type", OrderType.ADHESION)
            add("contract", contractUnderTest)
            add("value", null)
        }})
        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result != null
    }

    def 'given a known contractor and INSTALLMENT_PAYMENT order without value should be created'(){
        given:
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProduct()
        def instrument = fixtureCreator.createInstrumentToProduct(product, contractor)
        fixtureCreator.createInstrumentToProduct(product, contractor)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("type", OrderType.INSTALLMENT_PAYMENT)
            add("contract", contractUnderTest)
            add("paymentInstrument", instrument)
            add("value", null)
        }})
        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result != null
    }

    def 'given a known contractor and installment payment order the order value should be equals contract installment'(){
        given:
        def contractor = contractUnderTest.contractor
        def product = contractUnderTest.product
        def instrument = fixtureCreator.createInstrumentToProduct(product, contractor)
        fixtureCreator.createInstrumentToProduct(product, contractor)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("type", OrderType.INSTALLMENT_PAYMENT)
            add("contract", contractUnderTest)
            add("paymentInstrument", instrument)
            add("value", null)
        }})
        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result.value == installmentUnderTest.value
    }

    def 'given a unknown contractor and adhesion order the order value should be equals product installment'(){
        given:
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def product = contractUnderTest.product
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", person)
            add("product", product)
            add("type", OrderType.ADHESION)
            add("contract", contractUnderTest)
            add("paymentInstrument", null)
            add("value", null)
        }})
        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result.value == Rounder.round(product.annuity / product.paymentInstallments)
    }

    def 'given a unknown contractor and order without payment instrument should be created'(){
        given:
        def product = fixtureCreator.createProduct()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("product", product)
            add("paymentInstrument", null)
            add("type", OrderType.ADHESION)
        }})

        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result.createDateTime != null
    }

    def 'given a adhesion order for an unknown contractor with known email should return error'(){
        given:
        def user = fixtureCreator.createUser()
        Person person = Fixture.from(Person.class).gimme("physical", new Rule(){{
            add("physicalPersonDetail.email", user.getEmail())
        }})
        def creditOrder = createOrder()
        creditOrder.setPerson(person)
        creditOrder.type = OrderType.ADHESION


        when:
        service.create(creditOrder)

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'USER_ALREADY_EXISTS'
    }


    def 'given a known contractor and order with instrument of other contractor should return error'(){
        given:
        def creditOrder = createOrder()
        def instrument = fixtureCreator.createPaymentInstrument()
        creditOrder.setPaymentInstrument(instrument)
        when:
        service.create(creditOrder)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INSTRUMENT_NOT_BELONGS_TO_CONTRACTOR'
    }

    def 'given a known contractor and credit order with instrument of other product should return error'(){
        given:
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProduct()
        def instrument = fixtureCreator.createInstrumentToProduct(product, contractor)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", fixtureCreator.createProduct())
            add("paymentInstrument", instrument)
            add("type", OrderType.CREDIT)
        }})

        when:
        service.create(creditOrder)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INSTRUMENT_IS_NOT_FOR_PRODUCT'
    }

    def 'given a unknown document should create person when create order'(){
        given:
        def product = fixtureCreator.createProduct()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("product", product)
            add("type", OrderType.ADHESION)
        }})

        when:
        Order created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result != null
        result.person != null
        personService.findById(result.person.id)
    }

    def 'given a order with known person should not create a new person'(){
        given:
        def creditOrder = createOrder()

        when:
        Order created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result != null
        result.person != null
        result.person == creditOrder.person
    }

    def 'a order should not be created with unknown product'(){
        given:
        Order creditOrder = Fixture.from(Order.class).gimme("valid")

        when:
        service.create(creditOrder)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }

    def 'a order should not be created without product'(){
        given:
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("product", null)
        }})

        when:
        service.create(creditOrder)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'PRODUCT_REQUIRED'
    }

    def 'a order without payment request should not be created'(){
        given:
        def product = fixtureCreator.createProduct()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("product", product)
            add("paymentRequest", null)
        }})

        when:
        service.create(creditOrder)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'PAYMENT_REQUEST_REQUIRED'
    }

    def 'when create order should notify'(){
        given:
        def creditOrder = createOrder()

        when:
        Order created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result != null
        1 * notifierMock.notify(Queues.ORDER_CREATED, _)
    }

    def 'payment request order should be created with order id'(){
        given:
        def creditOrder = createOrder()

        when:
        Order created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result != null
        created.paymentRequest.orderId == created.id
    }

    def 'when create order should increment order number'(){
        given:
        def product = fixtureCreator.createProduct()
        List<Person> persons = Fixture.from(Person.class).gimme(2,"physical", new Rule(){{
            add("document.number", uniqueRandom("92505722803", "87023146300", "15173351160"))
        }})
        List<Order> orders = Fixture.from(Order.class).gimme(2,"valid", new Rule(){{
            add("product", product)
            add("person", uniqueRandom(persons.find(), persons.last()))
            add("type", OrderType.ADHESION)
        }})

        when:
        service.create(orders.find())
        service.create(orders.last())
        def result = service.findAll()

        then:
        result != null
        result.find().number != null
        result.last().number != null
        result.last().number != result.find().number
    }

    private Order createOrder(){
        def contractor = fixtureCreator.createContractor("physical")
        def product = fixtureCreator.createProduct()
        def instrument = fixtureCreator.createInstrumentToProduct(product, contractor)
        return Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("contract", contractUnderTest)
            add("type", OrderType.CREDIT)
            add("paymentInstrument", instrument)
            add("value", BigDecimal.ONE)
        }})
    }

    private Order createPersistedOrder(Contractor contractor = fixtureCreator.createContractor("physical"),
                                       OrderType type = OrderType.CREDIT, OrderStatus status = OrderStatus.PAID){
        def product = fixtureCreator.createProduct()
        def contract = fixtureCreator.createPersistedContract(contractor, product)
        installmentService.create(contract)
        def instrument = fixtureCreator.createInstrumentToProduct(product, contractor)
        return Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("contract", contract)
            add("type", type)
            add("paymentInstrument", instrument)
            add("value", BigDecimal.ONE)
            add("status", status)
        }})
    }

    private Order createPersistedAdhesionOrder(Person person){
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        return Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", person)
            add("product", product)
            add("type", OrderType.ADHESION)
            add("value", BigDecimal.ONE)
            add("status", OrderStatus.PAID)
        }})
    }
}
