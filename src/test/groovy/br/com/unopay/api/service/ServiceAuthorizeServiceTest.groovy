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

    Contractor contractorUnderTest
    Contract contractUnderTest
    UserDetail userUnderTest
    Event eventUnderTest
    ContractorInstrumentCredit instrumentCreditUnderTest
    Establishment establishmentUnderTest

    def setup(){
        instrumentCreditUnderTest = setupCreator.createContractorInstrumentCredit()
        contractorInstrumentCreditService.insert(instrumentCreditUnderTest.paymentInstrumentId, instrumentCreditUnderTest)
        contractorUnderTest = instrumentCreditUnderTest.contract.contractor
        contractUnderTest = instrumentCreditUnderTest.contract
        eventUnderTest = setupCreator.createEvent(contractUnderTest.serviceType.find())
        userUnderTest = setupCreator.createUser()
        establishmentUnderTest = setupCreator.createEstablishment()
        Integer.mixin(TimeCategory)
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
        serviceAuthorize.with {
            contract.id = contracts.find().id
            contractor = contracts.find().contractor
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
        serviceAuthorize.with {
            contract.id = anotherContract.id
            contractor = anotherContract.contractor
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
        def contractB = setupCreator.createPersistedContract(setupCreator.createContractor(), contractA.product)
        contractService.addEstablishments(contractA.id, contractEstablishment)
        contractService.addEstablishments(contractB.id, contractEstablishment.with {id = null; it })
        [contractB, contractA]
    }


}
