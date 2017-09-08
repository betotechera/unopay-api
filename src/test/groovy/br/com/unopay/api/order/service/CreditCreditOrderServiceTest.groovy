package br.com.unopay.api.order.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.config.Queues
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.model.Person
import br.com.unopay.api.order.model.CreditOrder
import br.com.unopay.api.service.PersonService
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired

class CreditCreditOrderServiceTest extends SpockApplicationTests{

    @Autowired
    CreditOrderService service

    @Autowired
    PersonService personService

    @Autowired
    FixtureCreator fixtureCreator

    Notifier notifierMock = Mock(Notifier)

    def setup(){
        service.notifier = notifierMock
    }

    def 'a with known person order should be created'(){
        given:
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProduct()
        CreditOrder creditOrder = Fixture.from(CreditOrder.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
        }})

        when:
        CreditOrder created = service.save(creditOrder)
        CreditOrder result = service.findById(created.id)

        then:
        result != null
    }

    def 'given a unknown document should create person when create order'(){
        given:
        def product = fixtureCreator.createProduct()
        CreditOrder creditOrder = Fixture.from(CreditOrder.class).gimme("valid", new Rule(){{
            add("product", product)
        }})

        when:
        CreditOrder created = service.create(creditOrder)
        CreditOrder result = service.findById(created.id)

        then:
        result != null
        result.person != null
        personService.findById(result.person.id)
    }

    def 'given a order with known person should not create a new person'(){
        given:
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProduct()
        CreditOrder creditOrder = Fixture.from(CreditOrder.class).gimme("valid", new Rule(){{
            add("product", product)
            add("person", contractor.person)
            add("value", BigDecimal.ONE)
        }})

        when:
        CreditOrder created = service.create(creditOrder)
        CreditOrder result = service.findById(created.id)

        then:
        result != null
        result.person != null
        result.person == contractor.person
    }

    def 'a order should not be created with unknown product'(){
        given:
        CreditOrder creditOrder = Fixture.from(CreditOrder.class).gimme("valid")

        when:
        service.create(creditOrder)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }

    def 'a order should not be created without product'(){
        given:
        CreditOrder creditOrder = Fixture.from(CreditOrder.class).gimme("valid", new Rule(){{
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
        CreditOrder creditOrder = Fixture.from(CreditOrder.class).gimme("valid", new Rule(){{
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
        def product = fixtureCreator.createProduct()
        CreditOrder creditOrder = Fixture.from(CreditOrder.class).gimme("valid", new Rule(){{
            add("product", product)
        }})

        when:
        CreditOrder created = service.create(creditOrder)
        CreditOrder result = service.findById(created.id)

        then:
        result != null
        1 * notifierMock.notify(Queues.UNOPAY_ORDER_CREATED, _)
    }

    def 'payment request order should be created order'(){
        given:
        def product = fixtureCreator.createProduct()
        CreditOrder creditOrder = Fixture.from(CreditOrder.class).gimme("valid", new Rule(){{
            add("product", product)
        }})

        when:
        CreditOrder created = service.create(creditOrder)
        CreditOrder result = service.findById(created.id)

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
        List<CreditOrder> orders = Fixture.from(CreditOrder.class).gimme(2,"valid", new Rule(){{
            add("product", product)
            add("person", uniqueRandom(persons.find(), persons.last()))
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
}
