package br.com.unopay.api.order.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.config.Queues
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractInstallment
import br.com.unopay.api.model.Person
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.order.model.OrderType
import br.com.unopay.api.service.ContractInstallmentService
import br.com.unopay.api.service.PersonService
import br.com.unopay.api.util.Rounder
import br.com.unopay.bootcommons.exception.NotFoundException
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

    @Unroll
    'given a known contractor and #type order without payment instrument should be created'(){
        given:
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProduct()
        def orderType = type
        fixtureCreator.createInstrumentToProduct(product, contractor)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("type", orderType)
            add("contract", contractUnderTest)
            add("paymentInstrument", null)
        }})
        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result.value != null

        where:
        _ | type
        _ | OrderType.ADHESION
        _ | OrderType.INSTALLMENT_PAYMENT

    }

    @Unroll
    'given a known contractor and #type order without value should be created'(){
        given:
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProduct()
        def instrument = fixtureCreator.createInstrumentToProduct(product, contractor)
        def orderType = type
        fixtureCreator.createInstrumentToProduct(product, contractor)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("type", orderType)
            add("contract", contractUnderTest)
            add("paymentInstrument", instrument)
            add("value", null)
        }})
        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result != null

        where:
        _ | type
        _ | OrderType.ADHESION
        _ | OrderType.INSTALLMENT_PAYMENT
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

    def 'given a known contractor and adhesion order the order value should be equals product installment'(){
        given:
        def contractor = contractUnderTest.contractor
        def product = contractUnderTest.product
        def instrument = fixtureCreator.createInstrumentToProduct(product, contractor)
        fixtureCreator.createInstrumentToProduct(product, contractor)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("type", OrderType.ADHESION)
            add("contract", contractUnderTest)
            add("paymentInstrument", instrument)
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
        1 * notifierMock.notify(Queues.UNOPAY_ORDER_CREATED, _)
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
        def contractor = fixtureCreator.createContractor()
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
}
