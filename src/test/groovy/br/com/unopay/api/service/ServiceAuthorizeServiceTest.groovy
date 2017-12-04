package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.EstablishmentEvent
import br.com.unopay.api.bacen.model.Event
import br.com.unopay.api.bacen.model.Service
import br.com.unopay.api.bacen.model.ServiceType
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.credit.service.InstrumentBalanceService
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.infra.UnopayEncryptor
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.Product
import br.com.unopay.api.model.ServiceAuthorize
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
    PaymentInstrumentService paymentInstrumentService

    @Autowired
    UnopayEncryptor encryptor


    @Autowired
    InstrumentBalanceService instrumentBalanceService

    @Autowired
    PasswordEncoder passwordEncoder

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
        instrumentBalanceService.add(paymentInstrumentUnderTest.id, establishmentEventUnderTest.value)
        def balance = instrumentBalanceService.findByInstrumentId(paymentInstrumentUnderTest.id)
        paymentInstrumentUnderTest.setBalance(balance)
    }

    void 'new service authorize should be created'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.id != null
    }

    void 'new service authorize should be created with product fee value'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.valueFee == Rounder.round(establishmentEventUnderTest.event.service.feeVal)
    }

    void 'when new service authorize created should generate authorization number'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.authorizationNumber = null
        when:
        def created = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.authorizationNumber != null
    }

    void 'new service authorize should be created with current authorization date'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        timeComparator.compare(result.authorizationDateTime, new Date()) == 0
    }

    void 'new service authorize should be created with current solicitation date'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        timeComparator.compare(result.solicitationDateTime, new Date()) == 0
    }

    void 'given a event value less than credit balance should archive last credit balance'() {
        given:

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        serviceAuthorize.with {
            establishmentEvent = establishmentEventUnderTest
        }
        when:
        def created = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.lastInstrumentCreditBalance == paymentInstrumentUnderTest.availableBalance
    }


    void 'given a event value less than credit balance should archive current credit balance'() {
        given:

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        serviceAuthorize.with {
            establishmentEvent.id = establishmentEventUnderTest.id
        }
        when:
        def created = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.currentInstrumentCreditBalance == 0.0
    }

    void 'given a event value less than credit balance should subtract instrument balance'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            establishmentEvent.id = establishmentEventUnderTest.id
        }
        when:
        service.create(userUnderTest.email, serviceAuthorize)

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
            establishmentEvent = establishmentEventTest
        }
        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE'
    }

    @Unroll
    void 'given a event with request quantity when validate event quantity equals #quantityUnderTest should return error'() {
        given:
        def serviceUnderTest = Fixture.from(Service.class).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("type", ServiceType.FUEL_ALLOWANCE)
        }})
        Event eventUnderTest = Fixture.from(Event.class).uses(jpaProcessor).gimme("withRequestQuantity", new Rule() {{
            add("service", serviceUnderTest)
        }})

        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(establishmentUnderTest,
                paymentInstrumentUnderTest.availableBalance + 0.1, eventUnderTest)

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            establishmentEvent = establishmentEventTest
            eventQuantity = quantityUnderTest
        }
        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EVENT_QUANTITY_GREATER_THAN_ZERO_REQUIRED'

        where:
        _ | quantityUnderTest
        _ | 0D
        _ | -1D

    }

    @Unroll
    void 'service #serviceTypeUnderTest should be authorized'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            serviceType = serviceTypeUnderTest
            event = fixtureCreator.createEvent(serviceTypeUnderTest)
        }

        when:
        def created = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.id != null

        where:
        _ | serviceTypeUnderTest
        _ | ServiceType.FUEL_ALLOWANCE
        _ | ServiceType.FREIGHT_RECEIPT
    }

    @Unroll
    void 'service #serviceTypeUnderTest should not be authorized'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            serviceType = serviceTypeUnderTest
        }

        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'SERVICE_NOT_ACCEPTABLE'

        where:
        _ | serviceTypeUnderTest
        _ | ServiceType.FREIGHT
        _ | ServiceType.ELECTRONIC_TOLL
    }

    void 'service authorize should be create with current user'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.user.id == userUnderTest.id
    }

    void 'when service authorize then contractor should be the contract contractor'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created = service.create(userUnderTest.email, serviceAuthorize)
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
        service.create(userUnderTest.email, serviceAuthorize)

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
            establishmentEvent.id = establishmentEventTest.id
        }

        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.establishment.id == userEstablishment.establishment.id
    }

    void 'when user is establishment type then the contract should belongs to establishment'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def contracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)

        def instrument = fixtureCreator.createInstrumentToProduct(contracts.find().product)
        serviceAuthorize.with {
            contract.id = contracts.find().id
            contractor = contracts.find().contractor
            establishmentEvent.id = establishmentEventTest.id
            paymentInstrument = instrument
            paymentInstrument.password = instrument.password
        }
        instrumentBalanceService.add(serviceAuthorize.paymentInstrument.id, establishmentEventTest.value)

        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.contract.id in contracts*.id
    }

    void 'when user not is establishment type when the contract without establishment should be authorized'() {
        given:
        def anotherContract = fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), contractUnderTest.product)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def instrument = fixtureCreator.createInstrumentToProduct(anotherContract.product)
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(establishmentUnderTest, instrument.availableBalance)
        serviceAuthorize.with {
            contract.id = anotherContract.id
            establishmentEvent = establishmentEventTest
            contractor = anotherContract.contractor
            paymentInstrument = instrument
            paymentInstrument.password = instrument.password
        }
        instrumentBalanceService.add(serviceAuthorize.paymentInstrument.id, establishmentEventTest.value)

        when:
        def created = service.create(userUnderTest.email, serviceAuthorize)
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
        service.create(userUnderTest.email, serviceAuthorize)

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
        service.create(userUnderTest.email, serviceAuthorize)

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
        service.create(userUnderTest.email, serviceAuthorize)

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
        service.create(userUnderTest.email, serviceAuthorize)

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
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ESTABLISHMENT_NOT_FOUND'
    }

    void 'when user is establishment type then the contractor payment instrument credit should belongs to contract'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)
        def instrument = fixtureCreator.createInstrumentToProduct(establishmentContracts.find().product)
        instrumentBalanceService.add(instrument.id, establishmentEventTest.value)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            establishmentEvent = establishmentEventTest
            contractor = establishmentContracts.find().contractor
            paymentInstrument = instrument
            paymentInstrument.password = instrument.password
        }

        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
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
        service.create(userEstablishment.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INSTRUMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT'
    }

    void 'given a payment instrument without password then birth date of the contractor should be informed'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)

        def serviceAuthorize = physicalContractorWithoutPassword(establishmentContracts.find(), userEstablishment)
        serviceAuthorize.with {
            contractor.person.physicalPersonDetail.birthDate = null
        }

        when:
        service.create(userEstablishment.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CONTRACTOR_BIRTH_DATE_REQUIRED'
    }

    void 'given a payment instrument without password then birth date of the physical contractor should be right'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        Date.mixin(TimeCategory)
        Integer.mixin(TimeCategory)
        def serviceAuthorize = physicalContractorWithoutPassword(establishmentContracts.find(), userEstablishment)
        serviceAuthorize.with {
            contractor.birthDate += 1.year
        }

        when:
        service.create(userEstablishment.email, serviceAuthorize)

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
        serviceAuthorize.establishmentEvent = establishmentEvent
        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.id != null
    }

    void 'when authorize service should archive and encrypt typed password'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)

        def serviceAuthorize = physicalContractorWithoutPassword(establishmentContracts.find(), userEstablishment)
        serviceAuthorize.with {
            establishmentEvent = establishmentEventTest
            paymentInstrument.password = '123456'
        }
        instrumentBalanceService.add(serviceAuthorize.paymentInstrument.id, establishmentEventTest.value)
        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.typedPassword != null
        encryptor.decrypt(result.typedPassword) == '123456'
    }

    void 'given payment instrument with password when the contractor password is same of payment instrument password should be authorized'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)
        def instrument = fixtureCreator.createInstrumentToProduct(establishmentContracts.find().product)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            establishmentEvent = establishmentEventTest
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            paymentInstrument = instrument
            paymentInstrument.password = instrument.password
        }
        instrumentBalanceService.add(serviceAuthorize.paymentInstrument.id,
                establishmentEventTest.value)

        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.id != null
    }

    void 'given payment instrument with password when the contractor password not is same of payment instrument password should not be authorized'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)

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
        service.create(userEstablishment.email, serviceAuthorize)

        then:
        thrown(UnauthorizedException)
    }

    void 'given a payment instrument without password then the password should be required'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)

        def serviceAuthorize = physicalContractorWithoutPassword(establishmentContracts.find(), userEstablishment)
        serviceAuthorize.with {
            paymentInstrument.password = null
        }

        when:
        service.create(userEstablishment.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INSTRUMENT_PASSWORD_REQUIRED'
    }

    void 'given a payment instrument without password then password of the legal contractor should be required'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        ServiceAuthorize serviceAuthorize = serviceAuthorizeWithoutPassword(userEstablishment, null)

        when:
        service.create(userEstablishment.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INSTRUMENT_PASSWORD_REQUIRED'
    }

    void 'given a payment instrument without password when password of the legal contractor should update instrument password'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        def expectedPassword = '1235555AAAA'
        def instrument = fixtureCreator.createInstrumentToProduct(establishmentContracts.find().product)
        paymentInstrumentService.save(instrument.with { password = null; it })

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            establishmentEvent = establishmentEventTest
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            paymentInstrument = instrument
            paymentInstrument.password = expectedPassword
        }
        instrumentBalanceService.add(serviceAuthorize.paymentInstrument.id,
                establishmentEventTest.value)
        when:
        service.create(userEstablishment.email, serviceAuthorize)
        def result = paymentInstrumentService.findById(instrument.id)

        then:
        passwordEncoder.matches(expectedPassword, result.password)
    }

    void 'given a payment instrument without password when password of the physical contractor present should update instrument password'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        def expectedPassword = '1235555AAAA'
        def serviceAuthorize = physicalContractorWithoutPassword(establishmentContracts.find(), userEstablishment)
        serviceAuthorize.with {
            establishmentEvent = establishmentEventTest
            paymentInstrument.password = expectedPassword
        }
        instrumentBalanceService.add(serviceAuthorize.paymentInstrument.id,
                establishmentEventTest.value)

        when:
        service.create(userEstablishment.email, serviceAuthorize)

        then:
        def result = paymentInstrumentService.findById(serviceAuthorize.paymentInstrument.id)
        passwordEncoder.matches(expectedPassword, result.password)
    }

    private ServiceAuthorize createServiceAuthorize() {
        return Fixture.from(ServiceAuthorize.class).gimme("valid").with {
            contract = contractUnderTest
            contractor = contractorUnderTest
            paymentInstrument = paymentInstrumentUnderTest
            establishmentEvent = establishmentEventUnderTest
            establishment = establishmentUnderTest
            serviceType = ServiceType.FUEL_ALLOWANCE
            it
        }
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
}
