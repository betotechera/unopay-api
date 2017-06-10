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
import br.com.unopay.api.model.CreditInsertionType
import br.com.unopay.api.model.FreightReceipt
import br.com.unopay.api.model.TravelDocument
import br.com.unopay.api.model.filter.TravelDocumentFilter
import br.com.unopay.api.pamcary.model.TravelDocumentsWrapper
import br.com.unopay.api.pamcary.service.PamcaryService
import br.com.unopay.api.repository.ContractorInstrumentCreditRepository
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.bootcommons.exception.NotFoundException
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

    @Autowired
    ContractorInstrumentCreditRepository contractorInstrumentCreditRepository

    @Autowired
    PaymentInstrumentService paymentInstrumentService

    @Autowired
    ServiceAuthorizeService serviceAuthorizeService

    UserDetail currentUser
    String currentPassword = '5544&SD%%DF'

    PamcaryService pamcaryServiceMock = Mock(PamcaryService)

    ContractorInstrumentCredit instrumentCreditUnderTest

    void setup(){
        instrumentCreditUnderTest = setupCreator.createContractorInstrumentCredit()
        instrumentCreditUnderTest.serviceType = ServiceType.FUEL_ALLOWANCE
        contractorInstrumentCreditRepository.save(instrumentCreditUnderTest)
        currentUser = setupCreator.createUser()
        service.pamcaryService = pamcaryServiceMock
        paymentInstrumentService.changePassword(instrumentCreditUnderTest.getPaymentInstrumentId(), currentPassword)
        Integer.mixin(TimeCategory)
    }

    def 'given a valid freight receipt then should be authorize fuel supply'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        that serviceAuthorizeService.findAll(), hasSize(1)
        serviceAuthorizeService.findAll()
                .find { it.contract.id == freightReceipt.contract.id && it.serviceType == ServiceType.FUEL_ALLOWANCE}
    }


    def 'given a valid freight receipt with invalid event should not be authorize fuel supply'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()
        freightReceipt.setFuelEvent(setupCreator.createEvent(ServiceType.FREIGHT))

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'FUEL_EVENT_NOT_FOUND'
    }

    def 'given a valid freight receipt without fuel credit should not be authorize fuel supply'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()
        instrumentCreditUnderTest.serviceType = ServiceType.FREIGHT
        contractorInstrumentCreditRepository.save(instrumentCreditUnderTest)

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CREDIT_FOR_SERVICE_TYPE_NOT_FOUND'
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

    void 'when user is establishment type when the contract with another establishment should not be receipted'(){
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


    void 'when user is establishment type when the contract belongs to another establishment should not be receipted'(){
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

    def 'given unknown travel document when freight receipt should not be receipted'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()
        freightReceipt.with {
            travelDocuments.find().id = ''
        }

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'TRAVEL_DOCUMENT_NOT_FOUND'
    }

    def 'given unknown cargo contract when freight receipt should not be receipted'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()
        freightReceipt.with {
            cargoContract.id = ''
        }

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CARGO_CONTRACT_NOT_FOUND'
    }

    def 'given a valid freight receipt then the cargo contract should be saved'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        that cargoContractService.findAll(), hasSize(1)
    }

    def 'when list documents should list from pamcary service'(){
        given:
        def receipt = createFreightReceipt()
        def wrapper = new TravelDocumentsWrapper().with {
            cargoContract = receipt.cargoContract
            travelDocuments = receipt.travelDocuments
            it
        }
        def filter = new TravelDocumentFilter()

        when:
        service.listDocuments(filter)

        then:
        1 * pamcaryServiceMock.searchDoc(filter) >> wrapper
    }

    def 'when list documents should save returned documents'(){
        given:
        CargoContract cargo = Fixture.from(CargoContract.class).gimme("valid", new Rule(){{
            add("contract", instrumentCreditUnderTest.contract)
        }})
        ComplementaryTravelDocument complementaryDocument = Fixture.from(ComplementaryTravelDocument.class).gimme("valid")
        List<TravelDocument> documents = Fixture.from(TravelDocument.class).gimme(2, "valid", new Rule(){{
            add("contract", instrumentCreditUnderTest.contract)
            add("complementaryTravelDocument", complementaryDocument)
        }})
        def wrapper = new TravelDocumentsWrapper().with {
            cargoContract = cargo
            travelDocuments = documents
            it
        }
        def filter = new TravelDocumentFilter()
        when:
        service.listDocuments(filter)

        then:
        1 * pamcaryServiceMock.searchDoc(filter) >> wrapper
        that cargoContractService.findAll(), hasSize(1)
        that travelDocumentService.findAll(), hasSize(2)
    }


    private FreightReceipt createFreightReceipt() {
        def credit = instrumentCreditUnderTest
        CargoContract cargo = Fixture.from(CargoContract.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("contract", credit.contract)
        }})
        ComplementaryTravelDocument complementaryDocument = Fixture.from(ComplementaryTravelDocument.class).uses(jpaProcessor).gimme("valid")
        List<TravelDocument> documents = Fixture.from(TravelDocument.class).uses(jpaProcessor).gimme(2, "valid", new Rule(){{
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
            setCreditInsertionType(CreditInsertionType.CREDIT_CARD)
            setInstrumentPassword(currentPassword)
            setFuelEvent(setupCreator.createEvent(ServiceType.FUEL_ALLOWANCE))
            setFuelSupplyQuantity(3D)
            setFuelSupplyValue(10.0)
        }}
    }
}
