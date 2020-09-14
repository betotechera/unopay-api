package br.com.unopay.api.order.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.billing.creditcard.model.CreditCard
import br.com.unopay.api.billing.creditcard.model.PaymentMethod
import br.com.unopay.api.billing.creditcard.model.PaymentRequest
import br.com.unopay.api.billing.creditcard.model.PersonCreditCard
import br.com.unopay.api.billing.creditcard.service.PersonCreditCardService
import br.com.unopay.api.config.Queues
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.market.service.AuthorizedMemberCandidateService
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractInstallment
import br.com.unopay.api.model.Person
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.order.model.OrderType
import br.com.unopay.api.order.model.PaymentStatus
import br.com.unopay.api.service.ContractInstallmentService
import br.com.unopay.api.service.ContractService
import br.com.unopay.api.service.PersonService
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.util.Rounder
import br.com.unopay.bootcommons.exception.BadRequestException
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
    private OrderService service
    @Autowired
    private PersonService personService
    @Autowired
    private ContractInstallmentService installmentService
    @Autowired
    private ContractService contractService
    @Autowired
    private PersonCreditCardService userCreditCardService
    @Autowired
    private FixtureCreator fixtureCreator
    @Autowired
    private AuthorizedMemberCandidateService candidateService

    private Contract contractUnderTest
    private ContractInstallment installmentUnderTest

    private Notifier notifierMock = Mock(Notifier)

    def setup(){
        contractUnderTest = fixtureCreator.createPersistedContract(fixtureCreator.createContractor(),
                fixtureCreator.createProductWithSameIssuerOfHirer())
        installmentService.create(contractUnderTest)
        installmentUnderTest = installmentService.findByContractId(contractUnderTest.id).find()
        service.notifier = notifierMock
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

    def 'given an previous contract for the same contractor and product should return error in an adhesion order'(){
        given:
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("type", OrderType.ADHESION)
            add("status", PaymentStatus.WAITING_PAYMENT)
            add("person", contractUnderTest.getContractor().getPerson())
            add("product", contractUnderTest.getProduct())
        }})

        when:
        service.create(creditOrder)

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'CONTRACT_ALREADY_EXISTS'
    }

    def 'given a unknown order when trying process the payment should return error'(){
        given:
        Order unknownOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("status", PaymentStatus.WAITING_PAYMENT)
        }})

        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        service.requestPayment(unknownOrder.id, order.paymentRequest)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ORDER_NOT_FOUND'
    }

    def 'given a unknown order when trying process the payment for the contractor should return error'(){
        given:
        def contractor = fixtureCreator.createContractor()
        Order unknownOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("type", OrderType.ADHESION)
            add("status", PaymentStatus.WAITING_PAYMENT)
            add("person", contractor.getPerson())
        }})

        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        service.requestPayment(contractor, unknownOrder.id, order.paymentRequest)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ORDER_NOT_FOUND'
    }

    def 'given a known order from another contractor when trying process the payment for the contractor should return error'(){
        given:
        def contractor = fixtureCreator.createContractor()
        Order unknownOrder = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("type", OrderType.ADHESION)
            add("status", PaymentStatus.WAITING_PAYMENT)
            add("person", contractor.getPerson())
        }})

        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        service.requestPayment(fixtureCreator.createContractor(), unknownOrder.id, order.paymentRequest)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ORDER_NOT_FOUND'
    }

    def 'given a known non-adhesion order when trying process the payment should return error'(){
        given:
        def type = orderType
        Order knownOrder = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("type", type)
        }})

        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        service.requestPayment(knownOrder.id, order.paymentRequest)

        then:
        def ex = thrown(BadRequestException)
        assert ex.errors.first().logref == 'INVALID_ORDER_TYPE'

        where:
        _ | orderType
        _ | OrderType.INSTALLMENT_PAYMENT
        _ | OrderType.CREDIT
    }

    def 'given a known adhesion order when trying process the payment should not return error'(){
        given:
        Order knownOrder = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("type", OrderType.ADHESION)
            add("status", PaymentStatus.WAITING_PAYMENT)
        }})

        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        service.requestPayment(knownOrder.id, order.paymentRequest)

        then:
        notifierMock.notify(Queues.ORDER_CREATED, knownOrder)
    }

    def 'given a known adhesion order when trying process the payment for the contractor should not return error'(){
        def contractor = fixtureCreator.createContractor()
        Order knownOrder = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("type", OrderType.ADHESION)
            add("status", PaymentStatus.WAITING_PAYMENT)
            add("person", contractor.getPerson())
        }})

        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        service.requestPayment(contractor, knownOrder.id, order.paymentRequest)

        then:
        notifierMock.notify(Queues.ORDER_CREATED, knownOrder)
    }

    def 'given a known adhesion order which is already paid when trying process the payment should return error'(){
        given:
        Order knownOrder = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("type", OrderType.ADHESION)
            add("status", PaymentStatus.PAID)
        }})

        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        service.requestPayment(knownOrder.id, order.paymentRequest)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ALREADY_PAID_ORDER'
    }

    def 'given a known adhesion order which is already paid when trying process the payment for the contractor should return error'(){
        given:
        def contractor = fixtureCreator.createContractor()
        Order knownOrder = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("type", OrderType.ADHESION)
            add("status", PaymentStatus.PAID)
            add("person", contractor.getPerson())
        }})

        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        service.requestPayment(contractor, knownOrder.id, order.paymentRequest)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ALREADY_PAID_ORDER'
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
        notifierMock.notify(Queues.ORDER_UPDATED, result)
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

    def 'given a known contractor and adhesion order should not return error'(){
        given:
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        fixtureCreator.createInstrumentToProduct(product, contractor)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("type", OrderType.ADHESION)
            add("paymentInstrument", null)
        }})

        when:
        def created = service.create(creditOrder)
        def result = service.findById(created.id)

        then:
        result
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

    def 'when create an installment order should get the last installment value'(){
        given:
        fixtureCreator.createInstrumentToProduct(contractUnderTest.product, contractUnderTest.contractor)
        Order creditOrderA = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractUnderTest.contractorPerson())
            add("product", contractUnderTest.product)
            add("type", OrderType.INSTALLMENT_PAYMENT)
            add("contract", contractUnderTest)
            add("paymentInstrument", null)
        }})

        def created = service.create(creditOrderA)
        contractService.markInstallmentAsPaidFrom(created)

        def installments = installmentService.findByContractId(contractUnderTest.getId())
        def currentInstallmentToPay = installments.findAll { it.paymentValue == null }.sort { it.installmentNumber }.first()
        def newExpectedValue = 666.72
        installmentService.update(currentInstallmentToPay.id, new ContractInstallment().with {
            value = newExpectedValue
            installmentNumber = currentInstallmentToPay.installmentNumber
            it})
        Order creditOrderB = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractUnderTest.contractorPerson())
            add("product", contractUnderTest.product)
            add("type", OrderType.INSTALLMENT_PAYMENT)
            add("contract", contractService.findById(contractUnderTest.id))
            add("paymentInstrument", null)
        }})
        when:
        def lastOrder = service.create(creditOrderB)
        Order result = service.findById(lastOrder.id)

        then:
        result.value == newExpectedValue

    }


    def 'given an adhesion order for a product without the membership fee then the payment value should be the product installment value'(){
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

    def 'given an adhesion order with candidates should create the member candidates'(){
        given:
        Person person =  Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", person)
            add("product", product)
            add("type", OrderType.ADHESION)
            add("contract", contractUnderTest)
        }})
        def candidate = fixtureCreator.createAuthorizedMemberCandidateToPersist()
        creditOrder.candidates = [candidate] as Set

        when:
        def created = service.create(creditOrder)
        def candidates = candidateService.findByOrderId(created.id)

        then:
        that candidates, hasSize(1)
    }

    def 'given an adhesion order without the type should not be created'(){
        given:
        Person person =  Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", person)
            add("product", product)
            add("type",null)
            add("contract", contractUnderTest)
        }})

        when:
        service.create(creditOrder)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'TYPE_REQUIRED'
    }

    def 'given an adhesion order with candidates should be created including candidates annuity total in value'(){
        given:
        Person person =  Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def membershipFee = 0.0
        def product = fixtureCreator.createProductWithSameIssuerOfHirer(membershipFee)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", person)
            add("product", product)
            add("type", OrderType.ADHESION)
            add("contract", contractUnderTest)
        }})
        def candidateA = fixtureCreator.createAuthorizedMemberCandidateToPersist()
        def candidateB = fixtureCreator.createAuthorizedMemberCandidateToPersist()
        creditOrder.candidates = [candidateA, candidateB] as Set

        when:
        def created = service.create(creditOrder)


        then:
        created.value == Rounder.round((product.annuity + (product.memberAnnuity * 2)) / product.paymentInstallments)
    }

    def 'given a non adhesion order with candidates should not create member candidates'(){
        given:
        def type = orderType
        Person person =  Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", person)
            add("product", product)
            add("type", type)
            add("contract", contractUnderTest)
        }})
        def candidate = fixtureCreator.createAuthorizedMemberCandidateToPersist()
        creditOrder.candidates = [candidate] as Set

        when:
        def created = service.create(creditOrder)
        def candidates = candidateService.findByOrderId(created.id)

        then:
        that candidates, hasSize(0)

        where:
        _ | orderType
        _ | OrderType.CREDIT
        _ | OrderType.INSTALLMENT_PAYMENT
    }

    def 'given an adhesion order for product with membership fee then the payment value should be membership fee value'(){
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

    def 'given a unknown contractor and unknown person on ADHESION order should be created'(){
        given:
        Person person =  Fixture.from(Person.class).gimme("physical")
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

    def 'should create an ADHESION order with payment method'(){
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
        result.paymentMethod != null
    }

    @Unroll
    'should create an #type order with payment method'(){
        given:
        def orderType = type
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        def instrument = fixtureCreator.createInstrumentToProduct(product, contractor)
        fixtureCreator.createInstrumentToProduct(product, contractor)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("type", orderType)
            add("contract", contractUnderTest)
            add("paymentInstrument", instrument)
            add("value", 10.0)
        }})
        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result.paymentMethod != null

        where:
        _ | type
        _ | OrderType.INSTALLMENT_PAYMENT
        _ | OrderType.CREDIT
    }


    def """given an order with the creditCard payment method and a user without a card
        token then the payment method should be changed to the ticket method"""(){
        given:
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        def instrument = fixtureCreator.createInstrumentToProduct(product, contractor)
        fixtureCreator.createInstrumentToProduct(product, contractor)
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("contract", contractUnderTest)
            add("paymentInstrument", instrument)
            add("type", OrderType.INSTALLMENT_PAYMENT)
            add("paymentMethod", PaymentMethod.CARD)
            add("value", 10.0)
        }})
        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result.paymentMethod == PaymentMethod.BOLETO
    }


    def """given an order with the ticket payment method and a user with a card
        token then the payment method should be changed to card"""(){
        given:
        def userContractor = fixtureCreator.createContractorUser()
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        def instrument = fixtureCreator.createInstrumentToProduct(product, userContractor.contractor)
        fixtureCreator.createInstrumentToProduct(product, userContractor.contractor)
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("creditCard", new Rule() {{
            add("method", PaymentMethod.CARD)
            add("storeCard", true)
        }})
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("person", userContractor.contractor.person)
            add("product", product)
            add("contract", contractUnderTest)
            add("paymentInstrument", instrument)
            add("paymentMethod", PaymentMethod.BOLETO)
            add("paymentRequest", paymentRequest)
            add("type", OrderType.CREDIT)
            add("value", 10.0)
        }})

        when:
        def created = service.create(userContractor.email, creditOrder)
        Order result = service.findById(created.id)

        then:
        result.paymentMethod == PaymentMethod.CARD
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

    def 'given an order without card information and for a product which accept only credit card payment should not be created'(){
        given:
        def product = fixtureCreator.createProductPFWithMethods([PaymentMethod.CARD])
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("product", product)
            add("paymentInstrument", null)
            add("type", OrderType.ADHESION)
            add("paymentMethod", PaymentMethod.CARD)
        }})
        def creditCard = new CreditCard()
        creditCard.setHolderName("Name")
        creditCard.setNumber("1234")
        creditCard.setExpiryMonth(null)
        creditCard.setExpiryYear("2089")
        creditCard.setToken(null)
        def paymentRequest = new PaymentRequest()
        paymentRequest.setCreditCard(creditCard)
        creditOrder.paymentRequest = paymentRequest


        when:
        service.create(creditOrder)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'RECURRENCE_PAYMENT_INFORMATION_REQUIRED'
    }

    def 'given an order without card token and for a product which accept only ticket payment should be created'(){
        given:
        def product = fixtureCreator.createProductPFWithMethods([PaymentMethod.BOLETO])
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("product", product)
            add("paymentInstrument", null)
            add("type", OrderType.ADHESION)
            add("paymentMethod", PaymentMethod.CARD)
        }})
        def creditCard = new CreditCard()
        creditCard.setHolderName("Name")
        creditCard.setNumber("1234")
        creditCard.setExpiryMonth("12")
        creditCard.setExpiryYear("2089")
        creditCard.setToken(null)
        def paymentRequest = new PaymentRequest()
        paymentRequest.setCreditCard(creditCard)
        creditOrder.paymentRequest = paymentRequest

        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result != null
    }


    def 'given an order without card token and for a order with ticket payment method should be created'(){
        given:
        def product = fixtureCreator.createProductPFWithMethods([PaymentMethod.BOLETO, PaymentMethod.CARD])
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("product", product)
            add("paymentInstrument", null)
            add("type", OrderType.ADHESION)
            add("paymentMethod", PaymentMethod.BOLETO)
        }})
        def creditCard = new CreditCard()
        creditCard.setHolderName("Name")
        creditCard.setNumber("1234")
        creditCard.setExpiryMonth("12")
        creditCard.setExpiryYear("2089")
        creditCard.setToken(null)
        def paymentRequest = new PaymentRequest()
        paymentRequest.setCreditCard(creditCard)
        creditOrder.paymentRequest = paymentRequest


        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result != null
    }

    def 'given an order with card token and for a product which accept all payment methods should be created'(){
        given:
        def product = fixtureCreator.createProductPFWithMethods([PaymentMethod.BOLETO, PaymentMethod.CARD])
        Order creditOrder = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("product", product)
            add("paymentInstrument", null)
            add("type", OrderType.ADHESION)
        }})
        def creditCard = new CreditCard()
        creditCard.setHolderName("Name")
        creditCard.setNumber("1234")
        creditCard.setExpiryMonth("12")
        creditCard.setExpiryYear("2089")
        creditCard.setToken("token")
        def paymentRequest = new PaymentRequest()
        paymentRequest.setCreditCard(creditCard)
        creditOrder.paymentRequest = paymentRequest

        when:
        def created = service.create(creditOrder)
        Order result = service.findById(created.id)

        then:
        result != null
    }

    def 'given an adhesion order and issuer without hirer document should not be created'(){
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

    def "given an adhesion order for an unknown contractor with unknown email and createUser disabled should be created"(){
        given:
        Person person = Fixture.from(Person.class).gimme("physical", new Rule(){{
            add("physicalPersonDetail.email", 'new@valid.com.br')
        }})
        def creditOrder = fixtureCreator.createOrder(contractUnderTest)
        creditOrder.setPerson(person)
        creditOrder.type = OrderType.ADHESION
        creditOrder.createUser = false


        when:
        service.create(creditOrder)

        then:
        notThrown(ConflictException)
    }

    def "given an adhesion order for an unknown contractor with known email and createUser disabled should return error"(){
        given:
        def user = fixtureCreator.createUser()
        Person person = Fixture.from(Person.class).gimme("physical", new Rule(){{
            add("physicalPersonDetail.email", user.getEmail())
        }})
        def creditOrder = fixtureCreator.createOrder(contractUnderTest)
        creditOrder.setPerson(person)
        creditOrder.type = OrderType.ADHESION
        creditOrder.createUser = false


        when:
        service.create(creditOrder)

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'USER_ALREADY_EXISTS'
    }

    def "given an adhesion order for an unknown contractor with a known email and createUser enabled should return error"(){
        given:
        def user = fixtureCreator.createUser()
        Person person = Fixture.from(Person.class).gimme("physical", new Rule(){{
            add("physicalPersonDetail.email", user.getEmail())
        }})
        def creditOrder = fixtureCreator.createOrder(contractUnderTest)
        creditOrder.setPerson(person)
        creditOrder.type = OrderType.ADHESION
        creditOrder.createUser = createUser


        when:
        service.create(creditOrder)

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'USER_ALREADY_EXISTS'

        where:
        _ | createUser
        _ | true
        _ | null
    }

    def "given an adhesion order for an unknown contractor with an invalid email and any createUser should return error"(){
        given:
        def invalidMail = mail
        Person person = Fixture.from(Person.class).gimme("physical", new Rule(){{
            add("physicalPersonDetail.email", invalidMail)
        }})
        def creditOrder = fixtureCreator.createOrder(contractUnderTest)
        creditOrder.setPerson(person)
        creditOrder.type = OrderType.ADHESION
        creditOrder.createUser = createUser


        when:
        service.create(creditOrder)

        then:
        def ex = thrown(BadRequestException)
        assert ex.errors.first().logref == 'INVALID_EMAIL'

        where:
        mail       | createUser
        ''         | true
        "a%%%.com" | null
        'ze@'      | false
    }

    def "given an adhesion order for an unknown contractor with an empty email and createUser disable should not return error"(){
        given:
        Person person = Fixture.from(Person.class).gimme("physical", new Rule(){{
            add("physicalPersonDetail.email", null)
        }})
        def creditOrder = fixtureCreator.createOrder(contractUnderTest)
        creditOrder.setPerson(person)
        creditOrder.type = OrderType.ADHESION
        creditOrder.createUser = createUser


        when:
        service.create(creditOrder)

        then:
        notThrown(ConflictException)

        where:
        _ | createUser
        _ | null
        _ | false
    }

    def "given an adhesion order for an unknown contractor with known email should return error"(){
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
        Order orderWithStoreCard = crateOrderWithStoreCard(creditCard)

        when:
        PersonCreditCard found = userCreditCardService.findByNumberForPerson(creditCard.number, orderWithStoreCard.getPerson())

        then:
        found

    }

    def 'when create order with known card token should be created'(){
        given:
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard")
        creditCard.token = 'DSDSFSDFDSFSD'
        def user = crateOrderWithStoreCard(creditCard, true)

        when:
        crateOrderWithStoreCard(creditCard, false, user)

        then:
        that userCreditCardService.findAll(), hasSize(1)
    }

    def 'when create order with a valid credit card without a token should be created'(){
        given:
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard")
        creditCard.token = null


        when:
        crateAdhesionOrderWithStoreCard(creditCard, true)

        then:
        that userCreditCardService.findAll(), hasSize(1)
    }

    def 'when create order with unknown card token should not be created'(){
        given:
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard")
        creditCard.token = null
        def user = crateOrderWithStoreCard(creditCard, false)
        creditCard.token = 'DSDSFSDFDSFSD'

        when:
        crateOrderWithStoreCard(creditCard, false,user)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

    private Order crateOrderWithStoreCard(creditCard = Fixture.from(CreditCard).gimme("payzenCard"), Boolean storeCard = true,
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
        order
    }


    private Order crateAdhesionOrderWithStoreCard(creditCard = Fixture.from(CreditCard).gimme("payzenCard"), Boolean storeCard = true) {
        PaymentRequest paymentRequest = Fixture.from(PaymentRequest).gimme("creditCard", new Rule() {{
            add("method", PaymentMethod.CARD)
            add("storeCard", storeCard)
            add("creditCard", creditCard)
        }})
        Order order = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("product", fixtureCreator.createProductWithSameIssuerOfHirer())
            add("paymentInstrument", null)
            add("type", OrderType.ADHESION)
        }})
        order.type = OrderType.ADHESION
        order.paymentRequest = paymentRequest
        service.create(order)
    }

}
