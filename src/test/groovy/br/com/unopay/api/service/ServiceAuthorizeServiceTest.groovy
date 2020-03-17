package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.credit.service.InstrumentBalanceService
import br.com.unopay.api.network.model.Event
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.scheduling.model.Scheduling

import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.infra.UnopayEncryptor
import br.com.unopay.api.market.model.ContractorBonus
import br.com.unopay.api.market.model.HirerNegotiation
import br.com.unopay.api.market.model.AuthorizedMemberCandidate
import br.com.unopay.api.market.service.ContractorBonusService
import br.com.unopay.api.market.service.DealService
import br.com.unopay.api.model.AuthorizationSituation
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.Deal
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.Person
import br.com.unopay.api.model.Product
import br.com.unopay.api.model.ServiceAuthorize
import br.com.unopay.api.model.ServiceAuthorizeEvent
import br.com.unopay.api.network.model.Establishment
import br.com.unopay.api.network.model.EstablishmentEvent
import br.com.unopay.api.network.model.ServiceType
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.util.Rounder
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnauthorizedException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import groovy.time.TimeCategory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Unroll

class ServiceAuthorizeServiceTest extends SpockApplicationTests {

    @Autowired
    ServiceAuthorizeService service

    @Autowired
    FixtureCreator fixtureCreator

    @Autowired
    ContractService contractService

    @Autowired
    private DealService dealCloseService

    @Autowired
    PaymentInstrumentService paymentInstrumentService

    @Autowired
    ProductService productService

    @Autowired
    UnopayEncryptor encryptor


    @Autowired
    InstrumentBalanceService instrumentBalanceService

    @Autowired
    PasswordEncoder passwordEncoder

    @Autowired
    ContractorBonusService contractorBonusService

    Contractor contractorUnderTest
    Contract contractUnderTest
    UserDetail userUnderTest
    Product productUnderTest
    Establishment establishmentUnderTest
    EstablishmentEvent establishmentEventUnderTest
    PaymentInstrument paymentInstrumentUnderTest


    def setup() {
        contractUnderTest = fixtureCreator.createPersistedContract()
        paymentInstrumentUnderTest = fixtureCreator.createInstrumentToProduct(contractUnderTest.product)
        productUnderTest = contractUnderTest.product
        contractorUnderTest = contractUnderTest.contractor
        userUnderTest = fixtureCreator.createUser()
        establishmentUnderTest = fixtureCreator.createEstablishment()
        establishmentEventUnderTest = fixtureCreator.createEstablishmentEvent(establishmentUnderTest,
                paymentInstrumentUnderTest.availableBalance)
        updateBalance(paymentInstrumentUnderTest, establishmentEventUnderTest)
    }

    void 'new service authorize should be created'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.id != null
    }
    void 'new service authorize is created should be able to rate'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def created = service.create(userUnderTest, serviceAuthorize)

        when:
        service.rate(created.id,35)
        def result = service.findById(created.id)

        then:
        assert result.rating != null
    }

    void 'service authorize should be created by scheduling'() {
        given:
        def contract = fixtureCreator.createPersistedContractWithProductIssuerAsHirer()
        def scheduling = createScheduling(contract)
        def serviceAuthorize = fixtureCreator.createServiceAuthorizeByScheduling(contract, scheduling.paymentInstrument.clone(), scheduling.user)
        serviceAuthorize.schedulingToken = scheduling.token
        serviceAuthorize.contract = null
        serviceAuthorize.contractor = null
        serviceAuthorize.authorizedMember = null
        serviceAuthorize.paymentInstrument.id = null

        when:
        def created = service.create(serviceAuthorize.user, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.schedulingToken == scheduling.token
               result.scheduling.id == scheduling.id
               result.contract.id == scheduling.contract.id
               result.contractor.id == scheduling.contractor.id
               result.paymentInstrument.id == scheduling.paymentInstrument.id
               result.authorizedMember.id == scheduling.authorizedMember.id
    }

    void 'given a known service authorize when cancel should be cancelled'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def created = service.create(userUnderTest, serviceAuthorize)

        when:
        service.cancel(created.id)
        def result = service.findById(created.id)

        then:
        result.situation == AuthorizationSituation.CANCELED
    }

    void 'given a known service authorize when cancel should give back instrument credit'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def previousBalance = instrumentBalanceService.findByInstrumentId(serviceAuthorize.instrumentId()).value
        def created = service.create(userUnderTest, serviceAuthorize)

        when:
        service.cancel(created.id)

        then:
        instrumentBalanceService.findByInstrumentId(created.instrumentId()).value == previousBalance
    }

    void 'given a known service authorize with closed batch when cancel should return error'() {
        given:
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).uses(jpaProcessor).gimme("valid",
                new Rule(){{
            add("contract",contractUnderTest)
            add("contractor",contractorUnderTest)
            add("paymentInstrument",paymentInstrumentUnderTest)
            add("situation",AuthorizationSituation.CLOSED_PAYMENT_BATCH)
            add("establishment",establishmentUnderTest)
        }})

        when:
        service.cancel(serviceAuthorize.id)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'AUTHORIZATION_IN_BATCH_PROCESSING'
    }

    void 'given a known service authorize already cancelled situation when cancel should return error'() {
        given:
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).uses(jpaProcessor).gimme("valid",
                new Rule(){{
                    add("contract",contractUnderTest)
                    add("contractor",contractorUnderTest)
                    add("paymentInstrument",paymentInstrumentUnderTest)
                    add("situation",AuthorizationSituation.CANCELED)
                    add("establishment",establishmentUnderTest)
                }})

        when:
        service.cancel(serviceAuthorize.id)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'AUTHORIZATION_CANNOT_BE_CANCELLED'
    }


    void 'given a event with request quantity and service authorize event without quantity should return error'() {
        given:
        fixtureCreator.createNegotiation(contractUnderTest.hirer, contractUnderTest.product)
        def event = fixtureCreator.createEvent(ServiceType.DOCTORS_APPOINTMENTS, true)
        def establishmentEvent = fixtureCreator.createEstablishmentEvent(establishmentUnderTest, null, event)
        updateBalance(paymentInstrumentUnderTest, establishmentEvent)
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule(){{
            add("contract",contractUnderTest)
            add("contractor",contractorUnderTest)
            add("paymentInstrument",paymentInstrumentUnderTest)
            add("authorizeEvents",[new ServiceAuthorizeEvent(establishmentEvent)])
            add("establishment",establishmentUnderTest)
        }})
        serviceAuthorize.authorizeEvents.each { it.eventQuantity = quantity }

        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EVENT_QUANTITY_REQUIRED'

        where:
        _ | quantity
        _ | null
        _ | 0
        _ | -1
    }


    void 'given a event with request quantity and service authorize event with quantity should be create with valid total'() {
        given:
        fixtureCreator.createNegotiation(contractUnderTest.hirer, contractUnderTest.product)
        def event = fixtureCreator.createEvent(ServiceType.DOCTORS_APPOINTMENTS, true)
        def establishmentEvent = fixtureCreator.createEstablishmentEvent(establishmentUnderTest, null, event)
        establishmentEvent.value = establishmentEvent.value * quantity
        updateBalance(paymentInstrumentUnderTest, establishmentEvent)
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule(){{
            add("contract",contractUnderTest)
            add("contractor",contractorUnderTest)
            add("paymentInstrument",paymentInstrumentUnderTest)
            add("authorizeEvents",[new ServiceAuthorizeEvent(establishmentEvent)])
            add("establishment",establishmentUnderTest)
        }})

        serviceAuthorize.authorizeEvents.find { it.eventQuantity = quantity }
        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.total == Rounder.round(serviceAuthorize.authorizeEvents.find().eventValue * quantity)

        where:
        _ | quantity
        _ | 5
        _ | 15
        _ | 2
    }

    void 'given a service authorize without active hirer negotiation should not be created'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize(fixtureCreator.createNegotiation())

        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'HIRER_NEGOTIATION_NOT_FOUND'
    }

    void 'given a service authorize without active hirer negotiation and issuer as hirer should be created'() {
        given:
        def candidates = Fixture.from(AuthorizedMemberCandidate).gimme(2, "valid") as Set
        def product = fixtureCreator.createProductWithSameIssuerOfHirer()
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def createUser= true
        Contract contract =  dealCloseService.closeWithIssuerAsHirer(new Order(person, product, createUser), candidates)
        def instrument = fixtureCreator.createInstrumentToProduct(product)
        updateBalance(instrument, establishmentEventUnderTest)
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule(){ {
            add("contract",contract)
            add("contractor",contract.contractor)
            add("paymentInstrument",instrument)
            add("authorizeEvents", [new ServiceAuthorizeEvent(establishmentEventUnderTest)])
            add("establishment",establishmentUnderTest)
        }})

        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def found = service.findById(created.id)

        then:
        found
    }

    void """given a service authorize without password in exceptional circumstance with issuers
          permission to authorize service without contractor password should be created"""() {
        given:
        def serviceAuthorize = createServiceAuthorizeInExceptionalCircumstance()
        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.id != null
    }

    void """given a service authorize without password and without issuer's
permission to authorize service without contractor password  in exceptional circumstance should not be created"""() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.paymentInstrument.password = null
        serviceAuthorize.exceptionalCircumstance = true


        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'SERVICE_AUTHORIZE_SHOULD_NOT_HAVE_EXCEPTIONAL_CIRCUMSTANCE'
    }

    void 'given a service authorize without password should not be created'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.paymentInstrument.password = null
        serviceAuthorize.exceptionalCircumstance = false

        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        thrown(UnauthorizedException)
    }

    void """given a service with event value greater than instrument balance
                and partial payment defined should be created"""() {
        given:
        def additionalValue = 100
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(establishmentUnderTest,
                paymentInstrumentUnderTest.availableBalance + additionalValue)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventTest)]
        serviceAuthorize.partialPayment = true

        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.paymentInstrument.availableBalance == BigDecimal.ZERO
    }

    void """given a service with event value greater than instrument balance
                and partial payment defined should be created with partial as paid value"""() {
        given:
        def additionalValue = 100
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(establishmentUnderTest,
                paymentInstrumentUnderTest.availableBalance + additionalValue)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventTest)]
        serviceAuthorize.partialPayment = true

        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.paid == paymentInstrumentUnderTest.availableBalance
    }

    void """given a service with event value greater than instrument balance
                and partial payment not defined should not be created"""() {
        given:
        def additionalValue = 100
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(establishmentUnderTest,
                paymentInstrumentUnderTest.availableBalance + additionalValue)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventTest)]
        serviceAuthorize.partialPayment = false

        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE'
    }

    void 'new service authorize should be created with product fee value'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.authorizeEvents.collect { it.valueFee }.sum() ==
                Rounder.round(establishmentEventUnderTest.event.service.feeVal)
    }

    void 'new service authorize should be created total of events'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.authorizeEvents.collect { it.eventValue }.sum() == Rounder.round(created.total)
    }

    void 'given a service authorize with defined total should be created without defined total'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize().with { total = 100; it }

        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.authorizeEvents.collect { it.eventValue }.sum() == Rounder.round(created.total)
    }

    void 'given a service authorize with defined paid should be created without defined paid'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize().with { paid = 100; it }

        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.authorizeEvents.collect { it.eventValue }.sum() == Rounder.round(created.paid)
    }

    void 'when try create authorize without events should return error'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.authorizeEvents = invalidVallue

        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EVENTS_REQUIRED'

        where:
        _ | invalidVallue
        _ | null
        _ | []
    }

    void 'when new service authorize created should generate authorization number'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.authorizationNumber = null
        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.authorizationNumber != null
    }

    void 'new service authorize should be created with current authorization date'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        timeComparator.compare(result.authorizationDateTime, new Date()) == 0
    }

    void 'given a event value less than credit balance should archive last credit balance'() {
        given:

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        serviceAuthorize.with {
            authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventUnderTest)]
        }
        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.lastInstrumentCreditBalance == paymentInstrumentUnderTest.availableBalance
    }


    void 'given a event value less than credit balance should archive current credit balance'() {
        given:

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        serviceAuthorize.with {
            authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventUnderTest)]
        }
        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.currentInstrumentCreditBalance == 0.0
    }

    void 'given a event value less than credit balance should subtract instrument balance'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventUnderTest)]
        }
        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        def result = instrumentBalanceService.findByInstrumentId(paymentInstrumentUnderTest.id)
        result.value == 0.0
    }

    void 'given a event value greater than credit balance when validate event should return error'() {
        given:

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(establishmentUnderTest,
                                                            paymentInstrumentUnderTest.availableBalance + 0.1)
        serviceAuthorize.with {
            authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventTest)]
        }
        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE'
    }


    @Unroll
    void 'service #serviceTypeUnderTest should be authorized'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventUnderTest).with {
                serviceType = serviceTypeUnderTest; event = fixtureCreator.createEvent(serviceTypeUnderTest); it}]
        }

        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.id != null

        where:
        _ | serviceTypeUnderTest
        _ | ServiceType.DOCTORS_APPOINTMENTS
        _ | ServiceType.MEDICINES
        _ | ServiceType.DENTISTRY
        _ | ServiceType.DIAGNOSIS_AND_THERAPY
    }


    void 'service authorize should be create with current user'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.user.id == userUnderTest.id
    }

    void 'when service authorize then contractor should be the contract contractor'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def authorize = service.findById(created.id)

        then:
        assert authorize.contractor.id == authorize.contract.contractor.id
    }


    void 'given unknown contractor service should not be authorized'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contractor.id = '1144'
        }
        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INVALID_CONTRACTOR'
    }

    void 'when user is establishment type then the establishment should be the user establishment'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()

        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)
        instrumentBalanceService.add(paymentInstrumentUnderTest.id, establishmentEventTest.value)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventTest)]
        }

        when:
        def created = service.create(userEstablishment, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.establishment.id == userEstablishment.establishment.id
    }

    void 'when user is establishment type then the contract should belongs to establishment'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()

        def contracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        def newContract = contracts.find()
        ServiceAuthorize serviceAuthorize = createServiceAuthorize(fixtureCreator.createNegotiation(newContract.hirer, newContract.product))
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)

        def instrument = fixtureCreator.createInstrumentToProduct(contracts.find().product)
        serviceAuthorize.with {
            contract.id = newContract.id
            contractor = newContract.contractor
            authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventTest)]
            paymentInstrument = instrument
            paymentInstrument.password = instrument.password
        }
        instrumentBalanceService.add(serviceAuthorize.paymentInstrument.id, establishmentEventTest.value)

        when:
        def created = service.create(userEstablishment, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.contract.id in contracts*.id
    }

    void 'when user not is establishment type when the contract without establishment should be authorized'() {
        given:
        def anotherContract = fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), contractUnderTest.product)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize(fixtureCreator.createNegotiation(anotherContract.hirer, anotherContract.product))
        def instrument = fixtureCreator.createInstrumentToProduct(anotherContract.product)
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(establishmentUnderTest, instrument.availableBalance)
        serviceAuthorize.with {
            contract.id = anotherContract.id
            authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventTest)]
            contractor = anotherContract.contractor
            paymentInstrument = instrument
            paymentInstrument.password = instrument.password
        }
        instrumentBalanceService.add(serviceAuthorize.paymentInstrument.id, establishmentEventTest.value)

        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.contract.id == anotherContract.id
    }

    @Unroll
    void 'given a #situation contract should not be authorized'() {
        given:
        def anotherContract = fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(),
                contractUnderTest.product, fixtureCreator.createHirer(), situation)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with { contract.id = anotherContract.id }

        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_ACTIVATED'

        where:
        _ | situation
        _ | ContractSituation.CANCELLED
        _ | ContractSituation.EXPIRED
        _ | ContractSituation.FINALIZED
        _ | ContractSituation.SUSPENDED
    }

    void 'given a contract finalized should not be authorized'() {
        given:
        def anotherContract = fixtureCreator
                .createContract(fixtureCreator.createContractor(),
                contractUnderTest.product, fixtureCreator.createHirer())
        anotherContract.with {
            situation = ContractSituation.ACTIVE
            begin = instant("2 days ago")
            end = instant("1 day ago")
        }
        contractService.create(anotherContract)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with { contract.id = anotherContract.id }

        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_IN_PROGRESS'
    }

    void 'given a contract does not begin should not be authorized'() {
        given:
        def anotherContract = fixtureCreator
                .createContract(fixtureCreator.createContractor(),
                contractUnderTest.product, fixtureCreator.createHirer())
        anotherContract.with {
            situation = ContractSituation.ACTIVE
            begin = instant("1 day from now")
            end = instant("2 day from now")
        }
        contractService.create(anotherContract)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with { contract.id = anotherContract.id }

        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_IN_PROGRESS'
    }

    void 'when user is not establishment type then the establishment should be required'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            establishment.id = null
        }

        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ESTABLISHMENT_REQUIRED'
    }

    void 'given a unknown establishment when user is not establishment type should be authorized'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            establishment.id = '1155'
        }

        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ESTABLISHMENT_NOT_FOUND'
    }

    void 'when user is establishment type then the contractor payment instrument credit should belongs to contract'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        establishmentContracts.each { fixtureCreator.createNegotiation(it.hirer, it.product)}
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)
        def instrument = fixtureCreator.createInstrumentToProduct(establishmentContracts.find().product)
        instrumentBalanceService.add(instrument.id, establishmentEventTest.value)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventTest)]
            contractor = establishmentContracts.find().contractor
            paymentInstrument = instrument
            paymentInstrument.password = instrument.password
        }

        when:
        def created = service.create(userEstablishment, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.paymentInstrument.id == instrument.id
    }

    void 'when user is establishment type when the contractor instrument credit with another contract should not be authorized'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def instrument = fixtureCreator.createInstrumentToProduct()
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = contractUnderTest
            establishment = userEstablishment.establishment
            contractor = contractUnderTest.contractor
            paymentInstrument = instrument
        }
        when:
        service.create(userEstablishment, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INSTRUMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT'
    }

    void 'given a payment instrument without password then birth date of the contractor should be informed'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        establishmentContracts.each { fixtureCreator.createNegotiation(it.hirer, it.product)}
        def serviceAuthorize = physicalContractorWithoutPassword(establishmentContracts.find(), userEstablishment)
        serviceAuthorize.with {
            contractor.person.physicalPersonDetail.birthDate = null
        }

        when:
        service.create(userEstablishment, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CONTRACTOR_BIRTH_DATE_REQUIRED'
    }

    void 'given a payment instrument without password then birth date of the physical contractor should be right'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        establishmentContracts.each { fixtureCreator.createNegotiation(it.hirer, it.product)}
        Date.mixin(TimeCategory)
        Integer.mixin(TimeCategory)
        def serviceAuthorize = physicalContractorWithoutPassword(establishmentContracts.find(), userEstablishment)
        serviceAuthorize.with {
            contractor.birthDate += 1.year
        }

        when:
        service.create(userEstablishment, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INCORRECT_CONTRACTOR_BIRTH_DATE'
    }

    void 'given a payment instrument without password then birth date of the legal contractor should not be required'(){
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentEvent = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)
        ServiceAuthorize serviceAuthorize = serviceAuthorizeWithoutPassword(userEstablishment, "1223456")
        instrumentBalanceService.add(serviceAuthorize.paymentInstrument.id,  establishmentEvent.value)
        serviceAuthorize.authorizeEvents = [new ServiceAuthorizeEvent(establishmentEvent)]
        when:
        def created = service.create(userEstablishment, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.id != null
    }

    void 'when authorize service should archive and encrypt typed password'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        establishmentContracts.each { fixtureCreator.createNegotiation(it.hirer, it.product)}
        def serviceAuthorize = physicalContractorWithoutPassword(establishmentContracts.find(), userEstablishment)
        serviceAuthorize.with {
            authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventTest)]
            paymentInstrument.password = '123456'
        }
        instrumentBalanceService.add(serviceAuthorize.paymentInstrument.id, establishmentEventTest.value)
        when:
        def created = service.create(userEstablishment, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.typedPassword != null
        encryptor.decrypt(result.typedPassword) == '123456'
    }

    void 'given payment instrument with password when the contractor password is same of payment instrument password should be authorized'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        establishmentContracts.each { fixtureCreator.createNegotiation(it.hirer, it.product)}
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)
        def instrument = fixtureCreator.createInstrumentToProduct(establishmentContracts.find().product)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventTest)]
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            paymentInstrument = instrument
            paymentInstrument.password = instrument.password
        }
        instrumentBalanceService.add(serviceAuthorize.paymentInstrument.id,
                establishmentEventTest.value)

        when:
        def created = service.create(userEstablishment, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.id != null
    }

    void 'given payment instrument with password when the contractor password not is same of payment instrument password should not be authorized'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        establishmentContracts.each { fixtureCreator.createNegotiation(it.hirer, it.product)}
        def instrument = fixtureCreator.createInstrumentToProduct(establishmentContracts.find().product)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            paymentInstrument = instrument
            paymentInstrument.password = 'otherPassword'
        }

        when:
        service.create(userEstablishment, serviceAuthorize)

        then:
        thrown(UnauthorizedException)
    }

    void 'given a payment instrument without password then the password should be required'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        establishmentContracts.each { fixtureCreator.createNegotiation(it.hirer, it.product)}
        def serviceAuthorize = physicalContractorWithoutPassword(establishmentContracts.find(), userEstablishment)
        serviceAuthorize.with {
            paymentInstrument.password = null
        }

        when:
        service.create(userEstablishment, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INSTRUMENT_PASSWORD_REQUIRED'
    }

    void 'given a payment instrument without password then password of the legal contractor should be required'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        ServiceAuthorize serviceAuthorize = serviceAuthorizeWithoutPassword(userEstablishment, null)

        when:
        service.create(userEstablishment, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INSTRUMENT_PASSWORD_REQUIRED'
    }

    void 'given a payment instrument without password when password of the legal contractor should update instrument password'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        establishmentContracts.each { fixtureCreator.createNegotiation(it.hirer, it.product)}
        def expectedPassword = '1235555AAAA'
        def instrument = fixtureCreator.createInstrumentToProduct(establishmentContracts.find().product)
        paymentInstrumentService.save(instrument.with { password = null; it })

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventTest)]
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            paymentInstrument = instrument
            paymentInstrument.password = expectedPassword
        }
        instrumentBalanceService.add(serviceAuthorize.paymentInstrument.id,
                establishmentEventTest.value)
        when:
        service.create(userEstablishment, serviceAuthorize)
        def result = paymentInstrumentService.findById(instrument.id)

        then:
        passwordEncoder.matches(expectedPassword, result.password)
    }

    void 'given a payment instrument without password when password of the physical contractor present should update instrument password'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        establishmentContracts.each { fixtureCreator.createNegotiation(it.hirer, it.product)}
        def expectedPassword = '1235555AAAA'
        def serviceAuthorize = physicalContractorWithoutPassword(establishmentContracts.find(), userEstablishment)
        serviceAuthorize.with {
            authorizeEvents = [new ServiceAuthorizeEvent(establishmentEventTest)]
            paymentInstrument.password = expectedPassword
        }
        instrumentBalanceService.add(serviceAuthorize.paymentInstrument.id,
                establishmentEventTest.value)

        when:
        service.create(userEstablishment, serviceAuthorize)

        then:
        def result = paymentInstrumentService.findById(serviceAuthorize.paymentInstrument.id)
        passwordEncoder.matches(expectedPassword, result.password)
    }

    void 'given service authorize with known authorizedMember should save it'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember(serviceAuthorize.contractor)
        serviceAuthorize.authorizedMember = authorizedMember

        when:
        def created = service.create(userUnderTest, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.id
    }

    void """given serviceAuthorize with authorizedMember that doesn't belong to serviceAuthorize
            contract should throw error"""() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        serviceAuthorize.authorizedMember = authorizedMember

        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'AUTHORIZED_MEMBER_NOT_FOUND'
    }

    void 'given service authorize with unknown authorizedMember should throw error'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.id = '123'
        serviceAuthorize.authorizedMember = authorizedMember

        when:
        service.create(userUnderTest, serviceAuthorize)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'AUTHORIZED_MEMBER_NOT_FOUND'
    }

    void 'create Service Authorize with Product with bonus percentage should create Contractor Bonus'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.contract.product.setBonusPercentage((0.3).toDouble())
        productService.save(serviceAuthorize.contract.product)

        when:
        service.create(userUnderTest, serviceAuthorize)
        List<ContractorBonus> list = contractorBonusService
                .getBonusesToProcessForPayer(serviceAuthorize.establishment.documentNumber())

        then:
        !list.empty
    }

    void 'create Service Authorize should create Contractor Bonus based on its data'() {

        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.contract.product.setBonusPercentage((0.3).toDouble())
        productService.save(serviceAuthorize.contract.product)

        when:
        service.create(userUnderTest, serviceAuthorize)
        List<ContractorBonus> list = contractorBonusService
                .getBonusesToProcessForPayer(serviceAuthorize.establishment.documentNumber())

        then:
        list.first().contractor == serviceAuthorize.contractor
        list.first().product == serviceAuthorize.contract.product
        list.first().payer == serviceAuthorize.establishment.person
        list.first().sourceIdentification == serviceAuthorize.authorizationNumber
        list.first().sourceValue == serviceAuthorize.paid
    }

    void 'create Service Authorize with Product without bonusPercentage should not create Contractor Bonus'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.contract.product.setBonusPercentage((0.0).toDouble())
        productService.save(serviceAuthorize.contract.product)

        when:
        service.create(userUnderTest, serviceAuthorize)
        List<ContractorBonus> list = contractorBonusService
                .getBonusesToProcessForPayer(serviceAuthorize.establishment.documentNumber())

        then:
        list.empty
    }

    private ServiceAuthorize createServiceAuthorize(HirerNegotiation hirerNegotiation = null) {
        if(!hirerNegotiation) {
            fixtureCreator.createNegotiation(contractUnderTest.hirer, contractUnderTest.product)
        }
        return Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule(){ {
            add("contract",contractUnderTest)
            add("contractor",contractorUnderTest)
            add("paymentInstrument",paymentInstrumentUnderTest)
            add("authorizeEvents", [new ServiceAuthorizeEvent(establishmentEventUnderTest)])
            add("establishment",establishmentUnderTest)
        }})
    }

    private Contract addPhysicalContractorToContract(Contract contract) {
        Contractor contractor = fixtureCreator.createContractor("physical")
        contract.contractor = contractor
        contractService.create(contract)
    }

    private ServiceAuthorize physicalContractorWithoutPassword(Contract contractParam, userEstablishment) {
        def contractResult = addPhysicalContractorToContract(contractParam)
        def instrument = fixtureCreator.createInstrumentToProduct(contractResult.product)
        paymentInstrumentService.save(instrument.with { password = null; it })
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = contractResult
            establishment = userEstablishment.establishment
            contractor = contractResult.contractor
            paymentInstrument = instrument
        }
        serviceAuthorize
    }

    private ServiceAuthorize serviceAuthorizeWithoutPassword(UserDetail userEstablishment, String pwd) {
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        establishmentContracts.each { fixtureCreator.createNegotiation(it.hirer, it.product)}
        def instrument = fixtureCreator.createInstrumentToProduct(establishmentContracts.find().product)
        paymentInstrumentService.save(instrument.with { password = null; it })
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            paymentInstrument = instrument
            paymentInstrument.password = pwd
        }
        serviceAuthorize
    }

    private void updateBalance(PaymentInstrument paymentInstrumentUnderTest, EstablishmentEvent establishmentEventUnderTest) {
        instrumentBalanceService.add(paymentInstrumentUnderTest.id, establishmentEventUnderTest.value)
        def balance = instrumentBalanceService.findByInstrumentId(paymentInstrumentUnderTest.id)
        paymentInstrumentUnderTest.setBalance(balance)
    }

    private ServiceAuthorize createServiceAuthorizeInExceptionalCircumstance() {
        def issuer = Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("authorizeServiceWithoutContractorPassword", true)
        }})
        def product = fixtureCreator.createProductWithIssuer(issuer)
        def contractor = fixtureCreator.createContractor()
        def contract = fixtureCreator.createPersistedContract(contractor, product)
        def paymentInstrument = fixtureCreator.createInstrumentToProduct(product)
        paymentInstrument.password = null
        def establishment = fixtureCreator.createEstablishment()
        def event = fixtureCreator.createEstablishmentEvent(establishment,
                paymentInstrument.availableBalance)
        fixtureCreator.createNegotiation(contract.hirer, contract.product)

        updateBalance(paymentInstrument, event)

        Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule(){ {
            add("contract",contract)
            add("contractor",contractor)
            add("paymentInstrument",paymentInstrument)
            add("authorizeEvents", [new ServiceAuthorizeEvent(event)])
            add("establishment",establishment)
            add("exceptionalCircumstance", true)
        }})
    }

    private Scheduling createScheduling(Contract contract) {
        def scheduling = Fixture.from(Scheduling.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("contract", contract)
            add("contractor", contract.contractor)
            add("authorizedMember", fixtureCreator.createPersistedAuthorizedMember(contract.contractor))
            add("paymentInstrument", fixtureCreator.createInstrumentToProduct(contract.product, contract.contractor))
            add("branch", fixtureCreator.createBranchForContract(contract))
            add("user", fixtureCreator.createUser())
            add("events", has(1).of(Event.class, "valid"))
        }})

        scheduling
    }

}
