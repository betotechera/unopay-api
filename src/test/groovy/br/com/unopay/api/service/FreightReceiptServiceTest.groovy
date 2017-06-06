package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.ServiceType
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.CargoContract
import br.com.unopay.api.model.ComplementaryTravelDocument
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.ContractorInstrumentCredit
import br.com.unopay.api.model.FreightReceipt
import br.com.unopay.api.model.TravelDocument
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import groovy.time.TimeCategory
import static org.hamcrest.Matchers.hasSize
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll
import static spock.util.matcher.HamcrestSupport.that

class FreightReceiptServiceTest extends SpockApplicationTests {

    @Autowired
    FreightReceiptService service

    @Autowired
    SetupCreator setupCreator

    @Autowired
    CargoContractService cargoContractService

    @Autowired
    TravelDocumentService travelDocumentService

    @Autowired
    ContractService contractService

    UserDetail currentUser

    ContractorInstrumentCredit instrumentCreditUnderTest

    void setup(){
        instrumentCreditUnderTest = setupCreator.createContractorInstrumentCredit()
        currentUser = setupCreator.createUser()
        Integer.mixin(TimeCategory)
    }

    void 'given unknown contractor service should not be receipted'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()
        freightReceipt.with {
            contractor.person.document.number = '55555'
            contractor.id = null
        }
        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INVALID_CONTRACTOR'
    }

    void 'given a contract finalized should not be receipted'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()
        def anotherContract = setupCreator
                .createContract(setupCreator.createContractor(),
                freightReceipt.contract.product, setupCreator.createHirer())
        anotherContract.with {
            situation = ContractSituation.ACTIVE
            begin = 2.day.ago
            end = 1.day.ago
        }
        contractService.save(anotherContract)

        freightReceipt.with { contract.id = anotherContract.id }

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_IN_PROGRESS'
    }

    void 'given a contract does not begin should not be receipted'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()
        def anotherContract = setupCreator
                .createContract(setupCreator.createContractor(),
                freightReceipt.contract.product, setupCreator.createHirer())
        anotherContract.with {
            situation = ContractSituation.ACTIVE
            begin = 1.day.from.now
            end = 2.day.from.now
        }
        contractService.save(anotherContract)

        freightReceipt.with { contract.id = anotherContract.id }

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_IN_PROGRESS'
    }

    void 'when user not is establishment type then the contract with another establishment should not be receipted'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()
        def anotherContracts = setupCreator.addContractsToEstablishment(setupCreator.createEstablishment(), instrumentCreditUnderTest)
        freightReceipt.with {
            contract.id = anotherContracts.find().id
            establishment.id = setupCreator.createEstablishment().id
            contractor = anotherContracts.find().contractor
        }

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT'
    }

    void 'when user is establishment type then the contract with another establishment should not be receipted'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        def anotherContracts = setupCreator.addContractsToEstablishment(setupCreator.createEstablishment(), instrumentCreditUnderTest)
        FreightReceipt freightReceipt = createFreightReceipt()
        freightReceipt.with {
            contract.id = anotherContracts.find().id
            establishment.id = setupCreator.createEstablishment().id
            contractor = anotherContracts.find().contractor
        }

        when:
        service.receipt(userEstablishment.email,freightReceipt)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT'
    }


    void 'when user is establishment type then the contract belongs to another establishment should not be receipted'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        setupCreator.addContractsToEstablishment(userEstablishment.establishment, instrumentCreditUnderTest)
        FreightReceipt freightReceipt = createFreightReceipt()
        def contracts = setupCreator.addContractsToEstablishment(setupCreator.createEstablishment(), instrumentCreditUnderTest)
        freightReceipt.with {
            contract.id = contracts.find().id
            contractor = contracts.find().contractor
        }

        when:
        service.receipt(userEstablishment.email,freightReceipt)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT'
    }

    @Unroll
    void 'given a #situation contract should not be receipted'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()
        def anotherContract = setupCreator
                .createPersistedContract(setupCreator.createContractor(),
                freightReceipt.contract.product, setupCreator.createHirer(), situation)

        freightReceipt.getContract().setId(anotherContract.id)

        when:
        service.receipt(currentUser.email,freightReceipt)

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

    def 'given a valid freight receipt then travel documents should be saved'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        that cargoContractService.findAll(), hasSize(1)
        that travelDocumentService.findAll(), hasSize(2)
    }

    def 'given a valid freight receipt then the cargo contract should be saved'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        that cargoContractService.findAll(), hasSize(1)
    }


    private FreightReceipt createFreightReceipt() {
        def credit = instrumentCreditUnderTest
        CargoContract cargo = Fixture.from(CargoContract.class).gimme("valid", new Rule(){{
            add("contract", credit.contract)
        }})
        ComplementaryTravelDocument complementaryDocument = Fixture.from(ComplementaryTravelDocument.class).gimme("valid")
        List<TravelDocument> documents = Fixture.from(TravelDocument.class).gimme(2, "valid", new Rule(){{
            add("contract", credit.contract)
            add("complementaryTravelDocument", complementaryDocument)
        }})
        return new FreightReceipt() {{
            setContract(credit.contract)
            setContractor(credit.contract.contractor)
            setEstablishment(setupCreator.createEstablishment())
            setServiceType(ServiceType.FREIGHT_RECEIPT)
            setTravelDocuments(documents)
            setCargoContract(cargo)
        }}
    }
}
