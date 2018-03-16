package br.com.unopay.api.order.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.billing.creditcard.model.CreditCard
import br.com.unopay.api.billing.creditcard.model.PaymentMethod
import br.com.unopay.api.billing.creditcard.model.PaymentRequest
import br.com.unopay.api.billing.creditcard.model.UserCreditCard
import br.com.unopay.api.billing.creditcard.service.UserCreditCardService
import br.com.unopay.api.config.Queues
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractInstallment
import br.com.unopay.api.model.Person
import br.com.unopay.api.notification.model.EventType
import br.com.unopay.api.notification.service.NotificationService
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.order.model.OrderType
import br.com.unopay.api.order.model.PaymentStatus
import br.com.unopay.api.service.ContractInstallmentService
import br.com.unopay.api.service.ContractService
import br.com.unopay.api.service.PersonService
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnauthorizedException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll
import static spock.util.matcher.HamcrestSupport.that

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
    UserCreditCardService userCreditCardService

    @Autowired
    FixtureCreator fixtureCreator

    Contract contractUnderTest
    ContractInstallment installmentUnderTest

    NotificationService notificationServiceMock = Mock(NotificationService)
    Notifier notifierMock = Mock(Notifier)

    def setup(){
        contractUnderTest = fixtureCreator.createPersistedContract(fixtureCreator.createContractor(),
                fixtureCreator.createProductWithSameIssuerOfHirer())
        installmentService.create(contractUnderTest)
        installmentUnderTest = installmentService.findByContractId(contractUnderTest.id).find()
        service.notifier = notifierMock
        service.notificationService = notificationServiceMock
    }

    def 'a valid order with known person should be created'(){
        given:
        def creditOrder = fixtureCreator.createOrder(contractUnderTest)

        when:
        Order created = service.save(creditOrder)
        Order result = service.findById(created.id)

        then:
        result != null
    }

    def 'given a credit order with paid status and credit type should call credit service'(){
        given:
        Contractor contractor = fixtureCreator.createContractor("physical")
        def paid = fixtureCreator.createPersistedOrderWithStatus(PaymentStatus.PAID, OrderType.CREDIT, contractor)

        when:
        service.process(paid)

        then:
        def result = instrumentCreditService.findByContractorId(contractor.id)
        result.availableBalance == paid.value
    }

    def 'given a adhesion order with paid status should create contract'(){
        given:
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def paid = fixtureCreator.createPersistedAdhesionOrder(person)

        when:
        service.process(paid)
        Optional<Contract> contract = contractService.findByContractorAndProduct(person.documentNumber(), paid.getProductId())

        then:
        contract.isPresent()
    }

    def 'given a installment payment order with paid status should mark installment as paid'(){
        given:
        Contractor contractor = fixtureCreator.createContractor("physical")
        def paid = fixtureCreator.createPersistedOrderWithStatus(PaymentStatus.PAID, OrderType.INSTALLMENT_PAYMENT, contractor)

        when:
        service.process(paid)

        then:
        def result = installmentService.findByContractId(paid.getContractId())
        result.sort{ it.installmentNumber }.find().paymentValue == paid.value
    }

    def 'given a known credit order with status waiting payment should update to paid status'(){
        given:
        Contractor contractor = fixtureCreator.createContractor("physical")
        def orderA = fixtureCreator.createPersistedOrderWithStatus(PaymentStatus.WAITING_PAYMENT,OrderType.CREDIT, contractor)

        Order orderB = Fixture.from(Order.class).gimme("valid", new Rule() {{
            add("status", PaymentStatus.PAID)
            add("contract", orderA.contract)
        }})

        when:
        service.update(orderA.id, orderB)
        def result = service.findById(orderA.id)

        then:
        result.status == PaymentStatus.PAID
    }

    def 'given a credit order with paid status should send payment approved email'(){
        given:
        def paid = fixtureCreator.createPersistedOrderWithStatus(PaymentStatus.PAID)

        when:
        service.process(paid)

        then:
        1 * notificationServiceMock.sendPaymentEmail(_, EventType.PAYMENT_APPROVED)
        0 * notificationServiceMock.sendPaymentEmail(_, EventType.PAYMENT_DENIED)
    }

    def 'given a credit order with payment denied status should send payment denied email'(){
        given:
        def paid = fixtureCreator.createPersistedOrderWithStatus(PaymentStatus.PAYMENT_DENIED)

        when:
        service.process(paid)

        then:
        1 * notificationServiceMock.sendPaymentEmail(_, EventType.PAYMENT_DENIED)
        0 * notificationServiceMock.sendPaymentEmail(_, EventType.PAYMENT_APPROVED)
    }

    def 'given a known credit order with status waiting payment when update status to paid should insert credit to payment instrument'() {
        given:
        Contractor contractor = fixtureCreator.createContractor("physical")

        def orderA = fixtureCreator.createPersistedOrderWithStatus(PaymentStatus.WAITING_PAYMENT, OrderType.CREDIT, contractor)

        Order orderB = Fixture.from(Order.class).gimme("valid", new Rule() {{
            add("status", PaymentStatus.PAID)
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
            add("status", PaymentStatus.CANCELED)
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
            add("status", PaymentStatus.WAITING_PAYMENT)
        }})

        Order order = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("status", PaymentStatus.PAID)
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
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
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

    def 'given a known contractor and installment payment order then the payment value should be contract installment value'(){
        given:
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
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
        result.value == creditOrder.contract.installmentValue()
    }


    def 'given a adhesion order for product without membership fee then the payment value should be product installment value'(){
        given:
        BigDecimal membershipFee = fee
        Person person =  Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def product = fixtureCreator.createProductWithSameIssuerOfHirer(membershipFee)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", person)
            add("product", product)
            add("type", OrderType.ADHESION)
            add("contract", contractUnderTest)
        }})
        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result.value == creditOrder.product.installmentValue

        where:
        _ | fee
        _ | null
        _ | 0
    }

    def 'given a adhesion order for product with membership fee then the payment value should be membership fee value'(){
        given:
        BigDecimal membershipFee = 25.0
        Person person =  Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def product = fixtureCreator.createProductWithSameIssuerOfHirer(membershipFee)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", person)
            add("product", product)
            add("type", OrderType.ADHESION)
            add("contract", contractUnderTest)
        }})
        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result.value == membershipFee
    }

    def 'given a unknown contractor and ADHESION order without payment instrument should be created'(){
        given:
        Person person =  Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
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
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
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
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
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

    def 'given a unknown contractor and order without payment instrument should be created'(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
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

    def 'given a adhesion order and issuer without hirer document should not be created'(){
        given:
        def product = fixtureCreator.createProduct()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("product", product)
            add("paymentInstrument", null)
            add("type", OrderType.ADHESION)
        }})

        when:
        service.create(creditOrder)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'HIRER_DOCUMENT_NOT_FOUND'
    }


    def 'given a non adhesion order and issuer without hirer document should be created'(){
        given:
        def contractor = contractUnderTest.contractor
        def product = fixtureCreator.createProduct()
        def instrument = fixtureCreator.createInstrumentToProduct(product, contractor)
        def orderType = type
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("type", orderType)
            add("contract", contractUnderTest)
            add("paymentInstrument", instrument)
            add("value", 20.0)
        }})

        when:
        service.create(creditOrder)

        then:
        notThrown(NotFoundException)

        where:
        _ | type
        _ | OrderType.INSTALLMENT_PAYMENT
        _ | OrderType.CREDIT

    }

    def 'given a adhesion order for an unknown contractor with known email should return error'(){
        given:
        def user = fixtureCreator.createUser()
        Person person = Fixture.from(Person.class).gimme("physical", new Rule(){{
            add("physicalPersonDetail.email", user.getEmail())
        }})
        def creditOrder = fixtureCreator.createOrder(contractUnderTest)
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
        def creditOrder = fixtureCreator.createOrder(contractUnderTest)
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
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
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
        def creditOrder = fixtureCreator.createOrder(contractUnderTest)

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
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
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
        def creditOrder = fixtureCreator.createOrder(contractUnderTest)

        when:
        Order created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result != null
        1 * notifierMock.notify(Queues.ORDER_CREATED, _)
    }

    def 'payment request order should be created with order id'(){
        given:
        def creditOrder = fixtureCreator.createOrder(contractUnderTest)

        when:
        Order created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result != null
        created.paymentRequest.orderId == created.id
    }

    def 'when create order should increment order number'(){
        given:
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
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

    def """when creating not-adhesion Order with paymentRequest.method equals card and paymentRequest.storeCard
                    equals true should create UserCreditCard for UserDetail and Order.creditCard"""(){
        given:
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard")
        UserDetail userDetail = crateOrderWithStoreCard(creditCard)

        when:
        UserCreditCard found = userCreditCardService.findByNumberForUser(creditCard.number, userDetail)

        then:
        found

    }

    def 'when create order with known card token should be created'(){
        given:
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard")
        creditCard.cardReference = 'DSDSFSDFDSFSD'
        def user = crateOrderWithStoreCard(creditCard, true)

        when:
        crateOrderWithStoreCard(creditCard, false, user)

        then:
        that userCreditCardService.findAll(), hasSize(1)
    }

    def 'when create order with unknown card token should not be created'(){
        given:
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard")
        creditCard.cardReference = null
        def user = crateOrderWithStoreCard(creditCard, false)
        creditCard.cardReference = 'DSDSFSDFDSFSD'

        when:
        crateOrderWithStoreCard(creditCard, false,user)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

    private UserDetail crateOrderWithStoreCard(creditCard, Boolean storeCard = true,
                                               UserDetail userDetail = fixtureCreator.createContractorUser()) {
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("creditCard", new Rule() {{
                add("method", PaymentMethod.CARD)
                add("storeCard", storeCard)
                add("creditCard", creditCard)
        }})
        Order order = fixtureCreator.createOrder(contractUnderTest)
        order.type = OrderType.INSTALLMENT_PAYMENT
        order.paymentRequest = paymentRequest
        service.create(userDetail.email, order)
        userDetail
    }

}
