package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.*
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.credit.model.ContractorInstrumentCredit
import br.com.unopay.api.credit.model.CreditSituation
import br.com.unopay.api.credit.repository.ContractorInstrumentCreditRepository
import br.com.unopay.api.credit.service.InstrumentBalanceService
import br.com.unopay.api.credit.service.InstrumentBalanceServiceTest
import br.com.unopay.api.function.FixtureFunctions
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.infra.UnopayEncryptor
import br.com.unopay.api.model.*
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.util.Rounder
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnauthorizedException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import groovy.time.TimeCategory
import org.joda.time.DateTimeComparator
import org.joda.time.DateTimeFieldType
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
    ContractorInstrumentCredit instrumentCreditUnderTest
    Establishment establishmentUnderTest
    EstablishmentEvent establishmentEventUnderTest
    PaymentInstrument paymentInstrumentUnderTest
    DateTimeComparator timeComparator = DateTimeComparator.getInstance(DateTimeFieldType.minuteOfDay())

    def setup() {
        instrumentCreditUnderTest = fixtureCreator.createContractorInstrumentCreditPersisted()
        paymentInstrumentUnderTest = instrumentCreditUnderTest.paymentInstrument
        productUnderTest = instrumentCreditUnderTest.contract.product
        contractorUnderTest = instrumentCreditUnderTest.contract.contractor
        contractUnderTest = instrumentCreditUnderTest.contract
        userUnderTest = fixtureCreator.createUser()
        establishmentUnderTest = fixtureCreator.createEstablishment()
        establishmentEventUnderTest = fixtureCreator.createEstablishmentEvent(establishmentUnderTest,
                instrumentCreditUnderTest.availableBalance)
        instrumentBalanceService.add(instrumentCreditUnderTest.paymentInstrumentId, establishmentEventUnderTest.value)
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
        result.lastInstrumentCreditBalance == instrumentCreditUnderTest.availableBalance
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
        def result = instrumentBalanceService.findByInstrumentId(instrumentCreditUnderTest.paymentInstrumentId)
        result.value == 0.0
    }

    void 'given a event value greater than credit balance when validate event should return error'() {
        given:

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(establishmentUnderTest, instrumentCreditUnderTest.availableBalance + 0.1)
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
                instrumentCreditUnderTest.availableBalance + 0.1, eventUnderTest)

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

    void 'service authorize contractor should be contract contractor when authorize'() {
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.contractor.id == result.contract.contractor.id
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

        fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest).find()
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)
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

        def instrumentCredit = createCreditInstrumentWithContract(contracts.find())
        serviceAuthorize.with {
            contract.id = contracts.find().id
            contractor = contracts.find().contractor
            establishmentEvent.id = establishmentEventTest.id
            contractorInstrumentCredit = instrumentCredit
            contractorInstrumentCredit.paymentInstrument.password = instrumentCredit.paymentInstrument.password
        }
        instrumentBalanceService.add(serviceAuthorize.contractorInstrumentCredit.paymentInstrumentId,
                establishmentEventTest.value)

        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.contract.id in contracts*.id
    }

    void 'when user not is establishment type when the contract without establishment should be authorized'() {
        given:
        def anotherContract = fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), instrumentCreditUnderTest.contract.product)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def instrumentCredit = createCreditInstrumentWithContract(anotherContract)
        def establishmentEventTest = fixtureCreator.createEstablishmentEvent(establishmentUnderTest, instrumentCredit.availableBalance)
        serviceAuthorize.with {
            contract.id = anotherContract.id
            establishmentEvent = establishmentEventTest
            contractor = anotherContract.contractor
            contractorInstrumentCredit = instrumentCredit
            contractorInstrumentCredit.paymentInstrument.password = instrumentCredit.paymentInstrument.password
        }
        instrumentBalanceService.add(serviceAuthorize.contractorInstrumentCredit.paymentInstrumentId,
                establishmentEventTest.value)

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
                instrumentCreditUnderTest.contract.product, fixtureCreator.createHirer(), situation)
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
                instrumentCreditUnderTest.contract.product, fixtureCreator.createHirer())
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
                instrumentCreditUnderTest.contract.product, fixtureCreator.createHirer())
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

    void 'when user not is establishment type when the contract with another establishment should not be authorized'() {
        given:
        def anotherContracts = fixtureCreator.addContractsToEstablishment(fixtureCreator.createEstablishment(), productUnderTest)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract.id = anotherContracts.find().id
            establishment.id = establishmentUnderTest.id
            contractor = anotherContracts.find().contractor
        }

        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT'
    }

    void 'when user is establishment type when the contract with another establishment should not be authorized'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def anotherContracts = fixtureCreator.addContractsToEstablishment(fixtureCreator.createEstablishment(), productUnderTest)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract.id = anotherContracts.find().id
            establishment.id = establishmentUnderTest.id
            contractor = anotherContracts.find().contractor
        }

        when:
        service.create(userEstablishment.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT'
    }

    void 'when user is establishment type when the contract belongs to another establishment should not be authorized'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def contracts = fixtureCreator.addContractsToEstablishment(fixtureCreator.createEstablishment(), productUnderTest)
        serviceAuthorize.with {
            contract.id = contracts.find().id
            contractor = contracts.find().contractor
        }

        when:
        service.create(userEstablishment.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT'
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
        def instrumentCredit = createCreditInstrumentWithContract(establishmentContracts.find())
        instrumentBalanceService.add(instrumentCredit.paymentInstrumentId, establishmentEventTest.value)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            establishmentEvent = establishmentEventTest
            contractor = establishmentContracts.find().contractor
            contractorInstrumentCredit = instrumentCredit
            contractorInstrumentCredit.paymentInstrument.password = instrumentCredit.paymentInstrument.password
        }

        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.contractorInstrumentCredit.id == instrumentCredit.id
    }

    void 'when user is establishment type when the contractor payment instrument credit with another contract should not be authorized'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)

        def instrumentCredit = createCreditInstrumentWithContract(contractUnderTest)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            contractorInstrumentCredit = instrumentCredit
        }

        when:
        service.create(userEstablishment.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CREDIT_NOT_QUALIFIED_FOR_THIS_CONTRACT'
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

    void 'given a payment instrument without password then birth date of the legal contractor should not be required'() {
        given:
        def userEstablishment = fixtureCreator.createEstablishmentUser()
        def establishmentEvent = fixtureCreator.createEstablishmentEvent(userEstablishment.establishment)
        ServiceAuthorize serviceAuthorize = serviceAuthorizeWithoutPassword(userEstablishment, "1223456")
        instrumentBalanceService.add(serviceAuthorize.contractorInstrumentCredit.paymentInstrumentId,
                                                                            establishmentEvent.value)
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
            contractorInstrumentCredit.paymentInstrument.password = '123456'
        }
        instrumentBalanceService.add(serviceAuthorize.contractorInstrumentCredit.paymentInstrumentId,
                                                                        establishmentEventTest.value)
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
        def instrumentCredit = createCreditInstrumentWithContract(establishmentContracts.find())
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            establishmentEvent = establishmentEventTest
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            contractorInstrumentCredit = instrumentCredit
            contractorInstrumentCredit.paymentInstrument.password = instrumentCredit.paymentInstrument.password
        }
        instrumentBalanceService.add(serviceAuthorize.contractorInstrumentCredit.paymentInstrumentId,
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

        def instrumentCredit = createCreditInstrumentWithContract(establishmentContracts.find())
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            contractorInstrumentCredit = instrumentCredit
            contractorInstrumentCredit.paymentInstrument.password = 'otherPassword'
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
            contractorInstrumentCredit.paymentInstrument.password = null
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
        def instrumentCredit = createCreditInstrumentWithContract(establishmentContracts.find())
        paymentInstrumentService.save(instrumentCredit.paymentInstrument.with { password = null; it })

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            establishmentEvent = establishmentEventTest
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            contractorInstrumentCredit = instrumentCredit
            contractorInstrumentCredit.paymentInstrument.password = expectedPassword
        }
        instrumentBalanceService.add(serviceAuthorize.contractorInstrumentCredit.paymentInstrumentId,
                establishmentEventTest.value)
        when:
        service.create(userEstablishment.email, serviceAuthorize)
        def result = paymentInstrumentService.findById(instrumentCredit.paymentInstrument.id)

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
            contractorInstrumentCredit.paymentInstrument.password = expectedPassword
        }
        instrumentBalanceService.add(serviceAuthorize.contractorInstrumentCredit.paymentInstrumentId,
                establishmentEventTest.value)

        when:
        service.create(userEstablishment.email, serviceAuthorize)

        then:
        def result = paymentInstrumentService.findById(serviceAuthorize.contractorInstrumentCredit.paymentInstrumentId)
        passwordEncoder.matches(expectedPassword, result.password)
    }

    private ContractorInstrumentCredit createCreditInstrumentWithContract(Contract contract) {
        fixtureCreator.createContractorInstrumentCreditPersisted(contract)
    }

    private ServiceAuthorize createServiceAuthorize() {
        return Fixture.from(ServiceAuthorize.class).gimme("valid").with {
            contract = contractUnderTest
            contractor = contractorUnderTest
            contractorInstrumentCredit = instrumentCreditUnderTest
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
        def instrumentCredit = fixtureCreator.createContractorInstrumentCreditPersisted(contractResult)
        paymentInstrumentService.save(instrumentCredit.paymentInstrument.with { password = null; it })
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = contractResult
            establishment = userEstablishment.establishment
            contractor = contractResult.contractor
            contractorInstrumentCredit = instrumentCredit
        }
        serviceAuthorize
    }

    private ServiceAuthorize serviceAuthorizeWithoutPassword(UserDetail userEstablishment, String pwd) {
        def establishmentContracts = fixtureCreator.addContractsToEstablishment(userEstablishment.establishment, productUnderTest)

        def instrumentCredit = createCreditInstrumentWithContract(establishmentContracts.find())
        paymentInstrumentService.save(instrumentCredit.paymentInstrument.with { password = null; it })
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            contractorInstrumentCredit = instrumentCredit
            contractorInstrumentCredit.paymentInstrument.password = pwd
        }
        serviceAuthorize
    }
}
