package br.com.unopay.api.order.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.market.service.AuthorizedMemberService
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractInstallment
import br.com.unopay.api.model.Person
import br.com.unopay.api.notification.model.EventType
import br.com.unopay.api.notification.service.NotificationService
import br.com.unopay.api.order.model.OrderType
import br.com.unopay.api.order.model.PaymentStatus
import br.com.unopay.api.service.ContractInstallmentService
import br.com.unopay.api.service.ContractService
import br.com.unopay.api.service.ProductService
import br.com.unopay.api.uaa.service.UserDetailService
import org.springframework.beans.factory.annotation.Autowired

class OrderProcessorTest extends SpockApplicationTests{

    @Autowired
    private OrderService service
    @Autowired
    private OrderProcessor processor
    @Autowired
    private ContractInstallmentService installmentService
    @Autowired
    private ContractService contractService
    @Autowired
    private ContractorInstrumentCreditService instrumentCreditService
    @Autowired
    private AuthorizedMemberService authorizedMemberService
    @Autowired
    private UserDetailService userDetailService
    @Autowired
    private FixtureCreator fixtureCreator
    @Autowired
    private ProductService productService

    private Contract contractUnderTest
    private ContractInstallment installmentUnderTest

    private NotificationService notificationServiceMock = Mock(NotificationService)
    private Notifier notifierMock = Mock(Notifier)

    def setup(){
        contractUnderTest = fixtureCreator.createPersistedContract(fixtureCreator.createContractor(),
                fixtureCreator.createProductWithSameIssuerOfHirer())
        installmentService.create(contractUnderTest)
        installmentUnderTest = installmentService.findByContractId(contractUnderTest.id).find()
        service.notifier = notifierMock
        processor.notificationService = notificationServiceMock
    }

    def 'given a credit order with paid status and credit type should call credit service'(){
        given:
        Contractor contractor = fixtureCreator.createContractor("physical")
        def paid = fixtureCreator.createPersistedOrderWithStatus(PaymentStatus.PAID, OrderType.CREDIT, contractor)

        when:
        processor.process(paid)

        then:
        def result = instrumentCreditService.findByContractorId(contractor.id)
        result.availableBalance == paid.value
    }

    def 'given a adhesion order with paid status should create the contract'(){
        given:
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def paid = fixtureCreator.createPersistedAdhesionOrder(person)

        when:
        processor.process(paid)
        Optional<Contract> contract = contractService.findByContractorAndProductCode(person.documentNumber(), paid.getProductCode())

        then:
        contract.isPresent()
    }

    def 'given a adhesion order with paid status should create the user'(){
        given:
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def paid = fixtureCreator.createPersistedAdhesionOrder(person)

        when:
        processor.process(paid)
        def userDetail = userDetailService.getByEmail(person.physicalPersonEmail)

        then:
        userDetail != null
    }

    def 'given a adhesion order with paid status and a password should create the user with the same password'(){
        given:
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def paid = fixtureCreator.createPersistedAdhesionOrder(person)
        def expectedPassword = '45687998'
        paid.userPassword = expectedPassword
        paid.setCreateUser(true)

        when:
        processor.process(paid)
        def userDetail = userDetailService.getByEmail(person.physicalPersonEmail)

        then:
        userDetailService.passwordEncoder.matches(expectedPassword,userDetail.password)
    }

    def 'given a adhesion order with candidates should create authorized members'(){
        given:
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def paid = fixtureCreator.createPersistedAdhesionOrder(person)
        fixtureCreator.createAuthorizedMemberCandidateForOrder(paid)

        when:
        processor.process(paid)
        Optional<Contract> contract = contractService.findByContractorAndProductCode(person.documentNumber(), paid.productCode)

        then:
        authorizedMemberService.countByContract(contract.get().id) == 1
    }

    def 'given a installment payment order with paid status should mark installment as paid'(){
        given:
        Contractor contractor = fixtureCreator.createContractor("physical")
        def paid = fixtureCreator.createPersistedOrderWithStatus(PaymentStatus.PAID, OrderType.INSTALLMENT_PAYMENT, contractor)

        when:
        processor.process(paid)

        then:
        def result = installmentService.findByContractId(paid.getContractId())
        result.sort{ it.installmentNumber }.find().paymentValue == paid.value
    }

    def 'given a adhesion order without membership fee should mark installment as paid'(){
        given:
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def paid = fixtureCreator.createPersistedAdhesionOrder(person)
        productService.save(paid.product.with {membershipFee = null; it })

        when:
        processor.process(paid)

        then:
        def contract = contractService.findByContractorAndProductCode(person.documentNumber(), paid.productCode)
        def result = installmentService.findByContractId(contract.get().id)
        result.sort{ it.installmentNumber }.find().paymentValue == paid.productInstallmentValue
    }

    def 'given a adhesion order with membership fee should not mark installment as paid'(){
        given:
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def paid = fixtureCreator.createPersistedAdhesionOrder(person)
        paid.setProduct(paid.product.with {membershipFee = 150.0; it })

        when:
        processor.process(paid)

        then:
        def contract = contractService.findByContractorAndProductCode(person.documentNumber(), paid.productCode)
        def result = installmentService.findByContractId(contract.get().id)
        !result.sort{ it.installmentNumber }.find().paymentValue
    }

    def 'given a known credit order with paid status when process should insert credit to payment instrument'() {
        given:
        Contractor contractor = fixtureCreator.createContractor("physical")

        def orderA = fixtureCreator.createPersistedOrderWithStatus(PaymentStatus.PAID, OrderType.CREDIT, contractor)

        when:
        processor.process(orderA)
        def credit = instrumentCreditService.findByContractorId(contractor.id)

        then:
        orderA.value == credit.value

    }

    def 'given a credit order with paid status should send payment approved email'(){
        given:
        def paid = fixtureCreator.createPersistedOrderWithStatus(PaymentStatus.PAID)

        when:
        processor.process(paid)

        then:
        1 * notificationServiceMock.sendPaymentEmail(_, EventType.PAYMENT_APPROVED)
        0 * notificationServiceMock.sendPaymentEmail(_, EventType.PAYMENT_DENIED)
    }

    def 'given a credit order with payment denied status should send payment denied email'(){
        given:
        def paid = fixtureCreator.createPersistedOrderWithStatus(PaymentStatus.PAYMENT_DENIED)

        when:
        processor.process(paid)

        then:
        1 * notificationServiceMock.sendPaymentEmail(_, EventType.PAYMENT_DENIED)
        0 * notificationServiceMock.sendPaymentEmail(_, EventType.PAYMENT_APPROVED)
    }
}
