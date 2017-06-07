package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Event
import br.com.unopay.api.bacen.model.ServiceType
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractEstablishment
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.ContractorInstrumentCredit
import br.com.unopay.api.model.CreditSituation
import br.com.unopay.api.model.ServiceAuthorize
import br.com.unopay.api.repository.ContractorInstrumentCreditRepository
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnauthorizedException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import br.com.unopay.api.bacen.model.Service
import groovy.time.TimeCategory
import org.apache.commons.beanutils.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Unroll

class ServiceAuthorizeServiceTest  extends SpockApplicationTests {

    @Autowired
    ServiceAuthorizeService service

    @Autowired
    SetupCreator setupCreator

    @Autowired
    ContractService contractService

    @Autowired
    ContractorInstrumentCreditService contractorInstrumentCreditService

    @Autowired
    PaymentInstrumentService paymentInstrumentService


    @Autowired
    ContractorInstrumentCreditRepository contractorInstrumentCreditRepository

    @Autowired
    PasswordEncoder passwordEncoder

    Contractor contractorUnderTest
    Contract contractUnderTest
    UserDetail userUnderTest
    Event eventUnderTest
    ContractorInstrumentCredit instrumentCreditUnderTest
    Establishment establishmentUnderTest

    def setup(){
        instrumentCreditUnderTest = createInstrumentCredit()
        contractorUnderTest = instrumentCreditUnderTest.contract.contractor
        contractUnderTest = instrumentCreditUnderTest.contract
        eventUnderTest = setupCreator.createEvent(ServiceType.FUEL_ALLOWANCE)
        userUnderTest = setupCreator.createUser()
        establishmentUnderTest = setupCreator.createEstablishment()
        Integer.mixin(TimeCategory)
        Date.mixin(TimeCategory)
    }

    void 'new service authorize should be created'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created  = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.id != null
    }

    void 'new service authorize should be created with product fee value'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created  = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.valueFee == eventUnderTest.service.taxVal.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    void 'when new service authorize created should generate authorization number'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.authorizationNumber = null
        when:
        def created  = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.authorizationNumber != null
    }

    void 'new service authorize should be created with current authorization date'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created  = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.authorizationDateTime > 1.second.ago
        result.authorizationDateTime < 1.second.from.now
    }

    void 'new service authorize should be created with current solicitation date'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created  = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.solicitationDateTime > 1.second.ago
        result.solicitationDateTime < 1.second.from.now
    }

    void 'given a event value less than credit balance should archive last credit balance'(){
        given:

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            event.id = eventUnderTest.id
            eventValue = instrumentCreditUnderTest.availableBalance
        }
        when:
        def created = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.lastInstrumentCreditBalance == instrumentCreditUnderTest.availableBalance
    }

    void 'given a event value less than credit balance should archive current credit balance'(){
        given:

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            event.id = eventUnderTest.id
            eventValue = instrumentCreditUnderTest.availableBalance
        }
        when:
        def created = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.currentInstrumentCreditBalance == 0.0
    }

    void 'given a event value less than credit balance should subtract credit balance'(){
        given:

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            event.id = eventUnderTest.id
            eventValue = instrumentCreditUnderTest.availableBalance
        }
        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def result = contractorInstrumentCreditRepository.findById(instrumentCreditUnderTest.id)
        result.get().availableBalance == 0.0
    }

    void 'given a event value less than credit balance should change credit status to processing'(){
        given:

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            event.id = eventUnderTest.id
            eventValue = instrumentCreditUnderTest.availableBalance
        }
        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def result = contractorInstrumentCreditRepository.findById(instrumentCreditUnderTest.id)
        result.get().situation == CreditSituation.PROCESSING
    }

    void 'given a event value greater than credit balance when validate event should return error'(){
        given:

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            event.id = eventUnderTest.id
            eventValue = contractorInstrumentCredit.availableBalance + 0.1
        }
        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE'
    }

    @Unroll
    void 'when validate event value equals #quantityUnderTest should return error'(){
        given:
        def serviceUnderTest = Fixture.from(Service.class).uses(jpaProcessor).gimme("valid",new Rule(){{
            add("type", ServiceType.FUEL_ALLOWANCE)
        }})
        Event eventUnderTest = Fixture.from(Event.class).uses(jpaProcessor).gimme("withoutRequestQuantity", new Rule(){{
            add("service", serviceUnderTest)
        }})
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            event.id = eventUnderTest.id
            eventQuantity = quantityUnderTest
            eventValue = valueUnderTest
        }
        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED'

        where:
        quantityUnderTest | valueUnderTest
        1D                | 0.0
        2D                | -0.1
    }

    @Unroll
    void 'given a event with request quantity when validate event quantity equals #quantityUnderTest should return error'(){
        given:
        def serviceUnderTest = Fixture.from(Service.class).uses(jpaProcessor).gimme("valid",new Rule(){{
            add("type", ServiceType.FUEL_ALLOWANCE)
        }})
        Event eventUnderTest = Fixture.from(Event.class).uses(jpaProcessor).gimme("withRequestQuantity", new Rule(){{
            add("service", serviceUnderTest)
        }})

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            event.id = eventUnderTest.id
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

    void 'when try authorize service without event should not be authorized'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            serviceType = ServiceType.FREIGHT_RECEIPT
        }

        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EVENT_NOT_ACCEPTED'
    }

    void 'given a expired credit should not be authorized'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        contractorInstrumentCreditRepository.save(instrumentCreditUnderTest.with { expirationDateTime = 1.day.ago; it })

        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CREDIT_EXPIRED'
    }

    @Unroll
    void 'given a credit #situationUnderTest should not be authorized'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        contractorInstrumentCreditRepository.save(instrumentCreditUnderTest.with { situation = situationUnderTest; it })

        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CREDIT_UNAVAILABLE'

        where:
        _|situationUnderTest
        _|CreditSituation.CANCELED
        _|CreditSituation.EXPIRED
        _|CreditSituation.PROCESSING
        _|CreditSituation.TO_COLLECT
        _|CreditSituation.CONFIRMED
    }

    @Unroll
    void 'service #serviceTypeUnderTest should be authorized'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            serviceType = serviceTypeUnderTest
            event = setupCreator.createEvent(serviceTypeUnderTest)
        }

        when:
        def created  = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.id != null

        where:
        _|serviceTypeUnderTest
        _|ServiceType.FUEL_ALLOWANCE
        _|ServiceType.FREIGHT_RECEIPT
    }

    @Unroll
    void 'service #serviceTypeUnderTest should not be authorized'(){
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
        _|serviceTypeUnderTest
        _|ServiceType.FREIGHT
        _|ServiceType.ELECTRONIC_TOLL
    }

    void 'service authorize should be create with current user'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created  = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.user.id == userUnderTest.id
    }

    void 'service authorize contractor should be contract contractor when authorize'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created  = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.contractor.id == result.contract.contractor.id
    }


    void 'given unknown contractor service should not be authorized'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contractor.person.document.number = '55555'
            contractor.id = null
        }
        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INVALID_CONTRACTOR'
    }

    void 'when user is establishment type then the establishment should be the user establishment'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        setupCreator.addContractsToEstablishment(userEstablishment.establishment, instrumentCreditUnderTest).find()

        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.establishment.id == userEstablishment.establishment.id
    }

    void 'when user is establishment type then the contract should belongs to establishment'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def contracts = setupCreator.addContractsToEstablishment(userEstablishment.establishment, instrumentCreditUnderTest)
        def instrumentCredit = createCreditInstrumentWithContract(contracts.find())
        serviceAuthorize.with {
            contract.id = contracts.find().id
            contractor = contracts.find().contractor
            contractorInstrumentCredit = instrumentCredit
            contractorInstrumentCredit.paymentInstrument.password = instrumentCredit.paymentInstrument.password
        }

        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.contract.id in contracts*.id
    }

    void 'when user not is establishment type when the contract without establishment should be authorized'(){
        given:
        def anotherContract = setupCreator
                .createPersistedContract(setupCreator.createContractor(), instrumentCreditUnderTest.contract.product)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def instrumentCredit = createCreditInstrumentWithContract(anotherContract)
        serviceAuthorize.with {
            contract.id = anotherContract.id
            contractor = anotherContract.contractor
            contractorInstrumentCredit = instrumentCredit
            contractorInstrumentCredit.paymentInstrument.password = instrumentCredit.paymentInstrument.password
        }

        when:
        def created = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.contract.id == anotherContract.id
    }

    @Unroll
    void 'given a #situation contract should not be authorized'(){
        given:
        def anotherContract = setupCreator
                .createPersistedContract(setupCreator.createContractor(),
                instrumentCreditUnderTest.contract.product, setupCreator.createHirer(), situation)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with { contract.id = anotherContract.id }

        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_ACTIVATED'

        where:
        _|situation
        _|ContractSituation.CANCELLED
        _|ContractSituation.EXPIRED
        _|ContractSituation.FINALIZED
        _|ContractSituation.SUSPENDED
    }

    void 'given a contract finalized should not be authorized'(){
        given:
        def anotherContract = setupCreator
                .createContract(setupCreator.createContractor(),
                instrumentCreditUnderTest.contract.product, setupCreator.createHirer())
        anotherContract.with {
            situation = ContractSituation.ACTIVE
            begin = 2.day.ago
            end = 1.day.ago
        }
        contractService.save(anotherContract)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with { contract.id = anotherContract.id }

        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_IN_PROGRESS'
    }

    void 'given a contract does not begin should not be authorized'(){
        given:
        def anotherContract = setupCreator
                .createContract(setupCreator.createContractor(),
                instrumentCreditUnderTest.contract.product, setupCreator.createHirer())
        anotherContract.with {
            situation = ContractSituation.ACTIVE
            begin = 1.day.from.now
            end = 2.day.from.now
        }
        contractService.save(anotherContract)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with { contract.id = anotherContract.id }

        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_IN_PROGRESS'
    }

    void 'when user not is establishment type when the contract with another establishment should not be authorized'(){
        given:
        def anotherContracts = setupCreator.addContractsToEstablishment(setupCreator.createEstablishment(), instrumentCreditUnderTest)
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

    void 'when user is establishment type when the contract with another establishment should not be authorized'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def anotherContracts = setupCreator.addContractsToEstablishment(setupCreator.createEstablishment(), instrumentCreditUnderTest)
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

    void 'when user is establishment type when the contract belongs to another establishment should not be authorized'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        setupCreator.addContractsToEstablishment(userEstablishment.establishment, instrumentCreditUnderTest)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def contracts = setupCreator.addContractsToEstablishment(setupCreator.createEstablishment(), instrumentCreditUnderTest)
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

    void'when user is not establishment type then the establishment should be required'(){
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

    void 'given a unknown establishment when user is not establishment type should be authorized'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            establishment.person.document.number = ''
        }

        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ESTABLISHMENT_NOT_FOUND'
    }

    void 'when user is establishment type then the contractor payment instrument credit should belongs to contract'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def establishmentContracts = setupCreator.addContractsToEstablishment(userEstablishment.establishment, instrumentCreditUnderTest)

        def instrumentCredit = createCreditInstrumentWithContract(establishmentContracts.find())
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
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

    void 'when user is establishment type when the contractor payment instrument credit with another contract should not be authorized'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def establishmentContracts = setupCreator.addContractsToEstablishment(userEstablishment.establishment, instrumentCreditUnderTest)

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

    void 'given a payment instrument without password then birth date of the contractor should be informed'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def establishmentContracts = setupCreator.addContractsToEstablishment(userEstablishment.establishment, instrumentCreditUnderTest)

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

    void 'given a payment instrument without password then birth date of the physical contractor should be right'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def establishmentContracts = setupCreator.addContractsToEstablishment(userEstablishment.establishment, instrumentCreditUnderTest)

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
        def userEstablishment = setupCreator.createEstablishmentUser()
        def establishmentContracts = setupCreator.addContractsToEstablishment(userEstablishment.establishment, instrumentCreditUnderTest)

        def instrumentCredit = createCreditInstrumentWithContract(establishmentContracts.find())
        paymentInstrumentService.save(instrumentCredit.paymentInstrument.with { password = null; it })
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            contractorInstrumentCredit = instrumentCredit
            contractorInstrumentCredit.paymentInstrument.password = "123456"
        }

        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.id != null
    }

    void 'given payment instrument with password when the contractor password is same of payment instrument password should be authorized'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def establishmentContracts = setupCreator.addContractsToEstablishment(userEstablishment.establishment, instrumentCreditUnderTest)

        def instrumentCredit = createCreditInstrumentWithContract(establishmentContracts.find())
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            contractorInstrumentCredit = instrumentCredit
            contractorInstrumentCredit.paymentInstrument.password = instrumentCredit.paymentInstrument.password
        }

        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.id != null
    }

    void 'given payment instrument with password when the contractor password not is same of payment instrument password should not be authorized'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def establishmentContracts = setupCreator.addContractsToEstablishment(userEstablishment.establishment, instrumentCreditUnderTest)

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

    void 'given a payment instrument without password then the password should be required'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def establishmentContracts = setupCreator.addContractsToEstablishment(userEstablishment.establishment, instrumentCreditUnderTest)

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

    void 'given a payment instrument without password then password of the legal contractor should be required'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def establishmentContracts = setupCreator.addContractsToEstablishment(userEstablishment.establishment, instrumentCreditUnderTest)

        def instrumentCredit = createCreditInstrumentWithContract(establishmentContracts.find())
        paymentInstrumentService.save(instrumentCredit.paymentInstrument.with { password = null; it })
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            contractorInstrumentCredit = instrumentCredit
            contractorInstrumentCredit.paymentInstrument.password = null
        }

        when:
        service.create(userEstablishment.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INSTRUMENT_PASSWORD_REQUIRED'
    }

    void 'given a payment instrument without password when password of the legal contractor should update instrument password'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def establishmentContracts = setupCreator.addContractsToEstablishment(userEstablishment.establishment, instrumentCreditUnderTest)
        def expectedPassword = '1235555AAAA'
        def instrumentCredit = createCreditInstrumentWithContract(establishmentContracts.find())
        paymentInstrumentService.save(instrumentCredit.paymentInstrument.with { password = null; it })

        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            contractorInstrumentCredit = instrumentCredit
            contractorInstrumentCredit.paymentInstrument.password = expectedPassword
        }
        when:
        service.create(userEstablishment.email, serviceAuthorize)
        def result = paymentInstrumentService.findById(instrumentCredit.paymentInstrument.id)

        then:
        passwordEncoder.matches(expectedPassword, result.password)
    }

    void 'given a payment instrument without password when password of the physical contractor present should update instrument password'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def establishmentContracts = setupCreator.addContractsToEstablishment(userEstablishment.establishment, instrumentCreditUnderTest)
        def expectedPassword = '1235555AAAA'
        def serviceAuthorize = physicalContractorWithoutPassword(establishmentContracts.find(), userEstablishment)
        serviceAuthorize.with {
            contractorInstrumentCredit.paymentInstrument.password = expectedPassword
        }

        when:
        service.create(userEstablishment.email, serviceAuthorize)

        then:
        def result = paymentInstrumentService.findById(serviceAuthorize.contractorInstrumentCredit.paymentInstrumentId)
        passwordEncoder.matches(expectedPassword, result.password)
    }

    private ContractorInstrumentCredit createCreditInstrumentWithContract(Contract contract) {
        def instrumentCredit = setupCreator
                .createContractorInstrumentCredit(contract.contractor, contract)
        setupCreator.encodeInstrumentPassword(instrumentCredit)
        def password = instrumentCredit.paymentInstrument.password
        contractorInstrumentCreditService.insert(instrumentCredit.paymentInstrumentId, instrumentCredit)
        instrumentCredit.with { paymentInstrument.password = password; it }
    }



    private ServiceAuthorize createServiceAuthorize() {
        setupCreator.encodeInstrumentPassword(instrumentCreditUnderTest)
        return Fixture.from(ServiceAuthorize.class).gimme("valid").with {
            contract = contractUnderTest
            contractor = contractorUnderTest
            event = eventUnderTest
            contractorInstrumentCredit = instrumentCreditUnderTest
            establishment = establishmentUnderTest
            serviceType = ServiceType.FUEL_ALLOWANCE
            it
        }
    }



    private Contract addPhysicalContractorToContract(Contract contract) {
        Contractor contractor = setupCreator.createContractor("physical")
        contract.contractor = contractor
        contractService.save(contract)
    }

    private ServiceAuthorize physicalContractorWithoutPassword(Contract contractParam, userEstablishment) {
        def contractResult = addPhysicalContractorToContract(contractParam)
        def instrumentCredit = createCreditInstrumentWithContract(contractResult)
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

    private ContractorInstrumentCredit createInstrumentCredit() {
        def instrumentCreditUnderTest = setupCreator.createContractorInstrumentCredit()
        def cloned = BeanUtils.cloneBean(instrumentCreditUnderTest.contract)
        contractorInstrumentCreditService.insert(instrumentCreditUnderTest.paymentInstrumentId, instrumentCreditUnderTest)
        instrumentCreditUnderTest.with { contract.product.serviceTypes = cloned.product.serviceTypes }
        instrumentCreditUnderTest
    }

}
