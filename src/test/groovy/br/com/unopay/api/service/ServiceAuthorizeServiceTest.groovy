package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Event
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractEstablishment
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.ContractorInstrumentCredit
import br.com.unopay.api.model.ServiceAuthorize
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import groovy.time.TimeCategory
import org.apache.commons.beanutils.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
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

    Contractor contractorUnderTest
    Contract contractUnderTest
    UserDetail userUnderTest
    Event eventUnderTest
    ContractorInstrumentCredit instrumentCreditUnderTest
    Establishment establishmentUnderTest

    def setup(){
        instrumentCreditUnderTest = setupCreator.createContractorInstrumentCredit()
        def cloned = BeanUtils.cloneBean(instrumentCreditUnderTest.contract)
        contractorInstrumentCreditService.insert(instrumentCreditUnderTest.paymentInstrumentId, instrumentCreditUnderTest)
        instrumentCreditUnderTest.with { contract.product.serviceType = cloned.product.serviceType }
        contractorUnderTest = instrumentCreditUnderTest.contract.contractor
        contractUnderTest = instrumentCreditUnderTest.contract
        eventUnderTest = setupCreator.createEvent(contractUnderTest.serviceType.find())
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
        addContractsToEstablishment(userEstablishment.establishment).find()

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
        def contracts = addContractsToEstablishment(userEstablishment.establishment)
        def instrumentCredit = createCreditInstrumentWithContract(contracts.find())
        serviceAuthorize.with {
            contract.id = contracts.find().id
            contractor = contracts.find().contractor
            contractorInstrumentCredit = instrumentCredit
        }

        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.contract.id in contracts*.id
    }

    void 'when user not is establishment type then the contract without establishment should be authorized'(){
        given:
        def anotherContract = setupCreator
                .createPersistedContract(setupCreator.createContractor(), instrumentCreditUnderTest.contract.product)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def instrumentCredit = createCreditInstrumentWithContract(anotherContract)
        serviceAuthorize.with {
            contract.id = anotherContract.id
            contractor = anotherContract.contractor
            contractorInstrumentCredit = instrumentCredit
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

    void 'when user not is establishment type then the contract with another establishment should not be authorized'(){
        given:
        def anotherContracts = addContractsToEstablishment(setupCreator.createEstablishment())
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

    void 'when user is establishment type then the contract with another establishment should not be authorized'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def anotherContracts = addContractsToEstablishment(setupCreator.createEstablishment())
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

    void 'when user is establishment type then the contract belongs to another establishment should not be authorized'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        addContractsToEstablishment(userEstablishment.establishment)
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        def contracts = addContractsToEstablishment(setupCreator.createEstablishment())
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


    void'when user is not establishment type then the establishment document should be required'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            establishment.person.document = null
        }

        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ESTABLISHMENT_DOCUMENT_REQUIRED'
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
        def establishmentContracts = addContractsToEstablishment(userEstablishment.establishment)

        def instrumentCredit = createCreditInstrumentWithContract(establishmentContracts.find())
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            contractorInstrumentCredit = instrumentCredit
        }

        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.contractorInstrumentCredit.id == instrumentCredit.id
    }

    void 'when user is establishment type then the contractor payment instrument credit with another contract should not be authorized'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def establishmentContracts = addContractsToEstablishment(userEstablishment.establishment)

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
        def establishmentContracts = addContractsToEstablishment(userEstablishment.establishment)

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
        def establishmentContracts = addContractsToEstablishment(userEstablishment.establishment)

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
        def establishmentContracts = addContractsToEstablishment(userEstablishment.establishment)

        def instrumentCredit = createCreditInstrumentWithContract(establishmentContracts.find())
        paymentInstrumentService.save(instrumentCredit.paymentInstrument.with { password = null; it })
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            contractorInstrumentCredit = instrumentCredit
            contractor.password = "123456"
        }

        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        result.id != null
    }



    void 'given a payment instrument without password then the new password should be required'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def establishmentContracts = addContractsToEstablishment(userEstablishment.establishment)

        def serviceAuthorize = physicalContractorWithoutPassword(establishmentContracts.find(), userEstablishment)
        serviceAuthorize.with {
            contractor.password = null
        }

        when:
        service.create(userEstablishment.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CONTRACTOR_PASSWORD_REQUIRED'
    }

    void 'given a payment instrument without password then password of the legal contractor should be required'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def establishmentContracts = addContractsToEstablishment(userEstablishment.establishment)

        def instrumentCredit = createCreditInstrumentWithContract(establishmentContracts.find())
        paymentInstrumentService.save(instrumentCredit.paymentInstrument.with { password = null; it })
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            contract = establishmentContracts.find()
            establishment = userEstablishment.establishment
            contractor = establishmentContracts.find().contractor
            contractorInstrumentCredit = instrumentCredit
            contractor.password = null
        }

        when:
        service.create(userEstablishment.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CONTRACTOR_PASSWORD_REQUIRED'
    }

    private ContractorInstrumentCredit createCreditInstrumentWithContract(Contract contract) {
        def instrumentCredit = setupCreator
                .createContractorInstrumentCredit(contract.contractor, contract)
        contractorInstrumentCreditService.insert(instrumentCredit.paymentInstrumentId, instrumentCredit)
    }

    private ServiceAuthorize createServiceAuthorize() {
        return Fixture.from(ServiceAuthorize.class).gimme("valid").with {
            contract = contractUnderTest
            contractor = contractorUnderTest
            event = eventUnderTest
            contractorInstrumentCredit = instrumentCreditUnderTest
            establishment = establishmentUnderTest
            it
        }
    }

    private List addContractsToEstablishment(Establishment establishmentUnderTest) {
        ContractEstablishment contractEstablishment = Fixture.from(ContractEstablishment.class).gimme("valid")
        contractEstablishment.with { establishment = establishmentUnderTest }
        def contractA = setupCreator
                .createPersistedContract(setupCreator.createContractor(), instrumentCreditUnderTest.contract.product)
        def contractB = setupCreator.createPersistedContract(setupCreator.createContractor(), instrumentCreditUnderTest.contract.product)
        contractService.addEstablishments(contractA.id, contractEstablishment)
        contractService.addEstablishments(contractB.id, contractEstablishment.with {id = null; it })
        [contractB, contractA]
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


}