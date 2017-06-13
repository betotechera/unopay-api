package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.ServiceType
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.config.Queues
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.model.CargoContract
import br.com.unopay.api.model.CargoProfile
import br.com.unopay.api.model.ComplementaryTravelDocument
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.ContractorInstrumentCredit
import br.com.unopay.api.model.CreditInsertionType
import br.com.unopay.api.model.DocumentCaveat
import br.com.unopay.api.model.FreightReceipt
import br.com.unopay.api.model.PaymentSource
import br.com.unopay.api.model.ReasonReceiptSituation
import br.com.unopay.api.model.ReceiptSituation
import br.com.unopay.api.model.ReceiptStep
import br.com.unopay.api.model.TravelDocument
import br.com.unopay.api.model.TravelDocumentSituation
import br.com.unopay.api.model.TravelSituation
import br.com.unopay.api.model.filter.TravelDocumentFilter
import br.com.unopay.api.pamcary.service.PamcaryService
import br.com.unopay.api.repository.ContractorInstrumentCreditRepository
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import groovy.time.TimeCategory
import org.apache.commons.beanutils.BeanUtils
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
    ComplementaryTravelDocumentService complementaryTravelDocumentService

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

    Notifier notifierMock = Mock(Notifier)

    ContractorInstrumentCredit instrumentCreditUnderTest

    void setup(){
        instrumentCreditUnderTest = setupCreator.createContractorInstrumentCredit()
        instrumentCreditUnderTest.serviceType = ServiceType.FUEL_ALLOWANCE
        contractorInstrumentCreditRepository.save(instrumentCreditUnderTest)
        currentUser = setupCreator.createUser()
        service.pamcaryService = pamcaryServiceMock
        service.notifier = notifierMock
        paymentInstrumentService.changePassword(instrumentCreditUnderTest.getPaymentInstrumentId(), currentPassword)
        Integer.mixin(TimeCategory)
    }

    def 'when receipt freight should notify partner'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        1 * notifierMock.notify(Queues.PAMCARY_TRAVEL_DOCUMENTS, _)
    }

    def 'when receipted freight should create travel document with accepted receipt situation'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        travelDocumentService.findAll().find().receiptSituation == ReceiptSituation.ACCEPTED
    }

    def 'when receipted freight should create cargo contract with collected step'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        cargoContractService.findAll().find().receiptStep == ReceiptStep.COLLECTED
    }

    def 'when receipted freight should create cargo contract with establishment payment'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        cargoContractService.findAll().find().paymentSource == PaymentSource.ESTABLISHMENT
    }

    def 'when receipted freight should create cargo contract with finalized travel situation'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        cargoContractService.findAll().find().travelSituation == TravelSituation.FINISHED
    }

    def 'when receipted freight should create travel document with digitized situation'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        travelDocumentService.findAll().find().situation == TravelDocumentSituation.DIGITIZED
    }

    def 'given a travel document with caveat when receipted freight should create travel document with caveat reason'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()
        freightReceipt.cargoContract.travelDocuments.find().caveat = DocumentCaveat.S

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        travelDocumentService.findAll().find().reasonReceiptSituation == ReasonReceiptSituation.CAVEAT_DOCUMENTATION
    }

    def 'given a travel document without caveat when receipted freight should create travel document with reason ok'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()
        freightReceipt.cargoContract.travelDocuments.find().caveat = DocumentCaveat.N

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        travelDocumentService.findAll().find().reasonReceiptSituation == ReasonReceiptSituation.DOCUMENTATION_OK
    }

    def 'when receipted freight should create complementary document with accepted receipt situation'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        complementaryTravelDocumentService.findAll().find().receiptSituation == ReceiptSituation.ACCEPTED
    }

    def 'when receipted freight should create complementary document with digitized situation'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        complementaryTravelDocumentService.findAll().find().situation == TravelDocumentSituation.DIGITIZED
    }

    def 'given a complementary document with caveat when receipted freight should create travel document with caveat reason'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()
        freightReceipt.cargoContract.complementaryTravelDocuments.find().caveat = DocumentCaveat.S

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        complementaryTravelDocumentService.findAll().find().reasonReceiptSituation == ReasonReceiptSituation.CAVEAT_DOCUMENTATION
    }

    def 'given a complementary document without caveat when receipted freight should create travel document with reason ok'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()
        freightReceipt.cargoContract.complementaryTravelDocuments.find().caveat = DocumentCaveat.N

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        complementaryTravelDocumentService.findAll().find().reasonReceiptSituation == ReasonReceiptSituation.DOCUMENTATION_OK
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
        that travelDocumentService.findAll(), hasSize(1)
    }

    def 'given a valid freight receipt then travel documents should be saved with current delivered date'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()

        when:
        service.receipt(currentUser.email,freightReceipt)

        then:
        travelDocumentService.findAll().find().deliveryDateTime > 1.second.ago
        travelDocumentService.findAll().find().deliveryDateTime < 1.second.from.now
        complementaryTravelDocumentService.findAll().find().deliveryDateTime > 1.second.ago
        complementaryTravelDocumentService.findAll().find().deliveryDateTime < 1.second.from.now
    }

    def 'given unknown travel document when freight receipt should not be receipted'(){
        given:
        FreightReceipt freightReceipt = createFreightReceipt()
        freightReceipt.with {
            cargoContract.travelDocuments.find().id = ''
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
        def filter = new TravelDocumentFilter()

        when:
        service.listDocuments(filter)

        then:
        1 * pamcaryServiceMock.searchDoc(filter) >> receipt.cargoContract
    }

    def 'when list documents should save returned documents'(){
        given:
        def cargo = createCargoContract()
        def filter = new TravelDocumentFilter()
        when:
        service.listDocuments(filter)

        then:
        1 * pamcaryServiceMock.searchDoc(filter) >> cargo
        that cargoContractService.findAll(), hasSize(1)
        that travelDocumentService.findAll(), hasSize(1)
        that complementaryTravelDocumentService.findAll(), hasSize(1)
    }

    def 'when list known documents should update returned documents'(){
        given:
        def cargo = createCargoContract()
        def filter = new TravelDocumentFilter()
        def expectedCaveat = DocumentCaveat.N
        List<TravelDocument> documents = Fixture.from(TravelDocument.class).gimme(1, "toPersist", new Rule(){{
            add("documentNumber", cargo.travelDocuments.find().documentNumber)
        }})
        List<ComplementaryTravelDocument> complementary = Fixture.from(ComplementaryTravelDocument.class).gimme(1,"valid", new Rule(){{
            add("documentNumber", cargo.complementaryTravelDocuments.find().documentNumber)
        }})
        CargoContract cloneBean = BeanUtils.cloneBean(cargo)
        cloneBean.with {
            id = null; caveat = expectedCaveat; travelDocuments = documents
            complementaryTravelDocuments = complementary
            it
        }
        when:
        service.listDocuments(filter)
        service.listDocuments(filter)

        then:
        2 * pamcaryServiceMock.searchDoc(filter) >>> [cargo,cloneBean]
        that cargoContractService.findAll(), hasSize(1)
        cargoContractService.findAll().find().caveat == expectedCaveat
        that travelDocumentService.findAll(), hasSize(1)
        travelDocumentService.findAll().find().type == documents.find().type
        that complementaryTravelDocumentService.findAll(), hasSize(1)
        complementaryTravelDocumentService.findAll().find().type == complementary.find().type
    }

    def 'Must return not found if TravelDocumentFilter does not exists'(){
        given:

        when:
        service.listDocuments(new TravelDocumentFilter())

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CARGO_CONTRACT_NOT_FOUND'
    }

    private CargoContract createCargoContract(){
        CargoContract cargo = Fixture.from(CargoContract.class).gimme("valid", new Rule(){{
            add("contract", instrumentCreditUnderTest.contract)
        }})
        List<TravelDocument> documents = Fixture.from(TravelDocument.class).gimme(1, "valid", new Rule(){{
            add("contract", instrumentCreditUnderTest.contract)
        }})
        List<ComplementaryTravelDocument> complementaryDocuments = Fixture.
                from(ComplementaryTravelDocument.class).gimme(1,"valid")
        cargo.setTravelDocuments(documents)
        cargo.setComplementaryTravelDocuments(complementaryDocuments)
        return cargo
    }

    private FreightReceipt createFreightReceipt() {
        def credit = instrumentCreditUnderTest
        CargoContract cargo = Fixture.from(CargoContract.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("contract", instrumentCreditUnderTest.contract)
        }})
        List<ComplementaryTravelDocument> complementaryDocuments = Fixture.from(ComplementaryTravelDocument.class).uses(jpaProcessor).gimme(1,"valid")
        List<TravelDocument> documents = Fixture.from(TravelDocument.class).uses(jpaProcessor).gimme(1, "valid", new Rule(){{
            add("contract", instrumentCreditUnderTest.contract)
        }})
        cargo.setTravelDocuments(documents)
        cargo.setComplementaryTravelDocuments(complementaryDocuments)
        return new FreightReceipt() {{
            setContract(credit.contract)
            setContractor(credit.contract.contractor)
            setEstablishment(setupCreator.createEstablishment())
            setServiceType(ServiceType.FREIGHT_RECEIPT)
            setCargoContract(cargo)
            setCreditInsertionType(CreditInsertionType.CREDIT_CARD)
            setInstrumentPassword(currentPassword)
            setFuelEvent(setupCreator.createEvent(ServiceType.FUEL_ALLOWANCE))
            setFuelSupplyQuantity(3D)
            setFuelSupplyValue(10.0)
        }}
    }
}
