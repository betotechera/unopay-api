package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.BatchClosing
import br.com.unopay.api.model.BatchClosingItem
import br.com.unopay.api.model.BatchClosingSituation
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.DocumentSituation
import br.com.unopay.api.model.IssueInvoiceType
import br.com.unopay.api.model.ServiceAuthorize
import br.com.unopay.api.notification.service.NotificationService
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.apache.commons.beanutils.BeanUtils
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.springframework.beans.factory.annotation.Autowired
import static spock.util.matcher.HamcrestSupport.that

class BatchClosingServiceTest extends SpockApplicationTests {

    @Autowired
    BatchClosingService service

    @Autowired
    ServiceAuthorizeService serviceAuthorizeService

    @Autowired
    FixtureCreator fixtureCreator

    NotificationService notificationServiceMock = Mock(NotificationService)

    void setup(){
        service.notificationService = notificationServiceMock
    }

    def 'given a known batch closing with issue invoice situation should update only invoice item fields'(){
        given:
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issueInvoice", true)
        }})
        List<BatchClosingItem> batchClosingItems = fixtureCreator.createBatchItems(batchClosing)
        def expectedInvoiceNumber = "54654687646798"
        def expectedDocumentUri = "file://teste.temp"
        batchClosingItems.each {
            it.invoiceNumber = expectedInvoiceNumber
            it.invoiceDocumentUri = expectedDocumentUri
            it.batchClosing = new BatchClosing()
            it.issueInvoiceType = IssueInvoiceType.BY_AUTHORIZATION
            it.serviceAuthorize = new ServiceAuthorize()
        }

        when:
        service.updateInvoiceInformation(batchClosingItems)
        def result = service.findById(batchClosing.id)

        then:
        result.batchClosingItems.every {
            it.invoiceNumber == expectedInvoiceNumber &&
            it.invoiceDocumentUri == expectedDocumentUri &&
            it.issueInvoiceType == IssueInvoiceType.BY_BATCH
        }
    }

    def 'given a known batch closing with issue invoice situation should update invoice documentation to approved'(){
        given:
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issueInvoice", true)
        }})
        List<BatchClosingItem> batchClosingItems = fixtureCreator.createBatchItems(batchClosing)

        batchClosingItems.each {
            it.invoiceNumber = "54654687646798"
            it.invoiceDocumentUri = "file://teste.temp"
            it.invoiceDocumentSituation = DocumentSituation.PENDING
        }

        when:
        service.updateInvoiceInformation(batchClosingItems)
        def result = service.findById(batchClosing.id)

        then:
        result.batchClosingItems.every {
                    it.invoiceDocumentSituation == DocumentSituation.APPROVED
        }
    }

    def 'given a known batch closing with issue invoice situation should update batch situation to received'(){
        given:
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issueInvoice", true)
        }})
        List<BatchClosingItem> batchClosingItems = fixtureCreator.createBatchItems(batchClosing)

        batchClosingItems.each {
            it.invoiceNumber = "54654687646798"
            it.invoiceDocumentUri = "file://teste.temp"
            it.invoiceDocumentSituation = DocumentSituation.PENDING
        }

        when:
        service.updateInvoiceInformation(batchClosingItems)
        def result = service.findById(batchClosing.id)

        then:
        result.situation == BatchClosingSituation.DOCUMENT_RECEIVED
    }

    def 'given a known batch closing without issue invoice situation should not be received'(){
        given:
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issueInvoice", false)
        }})
        List<BatchClosingItem> batchClosingItems = fixtureCreator.createBatchItems(batchClosing)

        batchClosingItems.each {
            it.invoiceNumber = "54654687646798"
            it.invoiceDocumentUri = "file://teste.temp"
            it.invoiceDocumentSituation = DocumentSituation.PENDING
        }

        when:
        service.updateInvoiceInformation(batchClosingItems)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INVOICE_NOT_REQUIRED_FOR_BATCH'
        assert ex.errors.first().arguments.find() == batchClosing.id
    }


    def 'should create batch closing'(){
        given:
        BatchClosing batchClosing = fixtureCreator.createBatchToPersist()

        when:
        def created = service.save(batchClosing)
        def result = service.findById(created.id)

        then:
        result.id != null
    }

    def 'when create batch closing should send email notification'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizations(contracts, establishment)

        when:
        service.create(establishment.id)

        then:
        1 * notificationServiceMock.sendBatchClosedMail(_, _)
    }

    def 'should create batch closing by establishment'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizations(contracts, establishment)

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings, hasSize(1)
    }

    def 'given authorizations of one or more days ago should be processed'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizationsAt(contracts, establishment, "1 day ago")

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings, hasSize(1)
    }

    def 'given authorizations only today should not be processed'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizationsAt(contracts, establishment, "1 hour ago")

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings, hasSize(0)
    }

    def 'should not create batch closing to processed authorizations by establishment'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizations(contracts, establishment, 2)

        when:
        service.create(establishment.id)
        service.create(establishment.id)
        Set<BatchClosing> batchClosings = service.findByEstablishmentId(establishment.id)

        then:
        that batchClosings, hasSize(1)
        that batchClosings.find().batchClosingItems, hasSize(2)

    }

    def 'given a contract with issue invoice should create batch closing document received situation'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid", new Rule(){{
            add("issueInvoice", true)
        }})
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizations(contracts, establishment, 2)

        when:
        service.create(establishment.id)
        Set<BatchClosing> batchClosings = service.findByEstablishmentId(establishment.id)

        then:
        that batchClosings, hasSize(1)
        batchClosings.find().situation == BatchClosingSituation.DOCUMENT_RECEIVED
    }

    def 'given a contract without issue invoice should create batch closing finalized situation'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid", new Rule(){{
            add("issueInvoice", false)
        }})
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizations(contracts, establishment, 2)

        when:
        service.create(establishment.id)
        Set<BatchClosing> batchClosings = service.findByEstablishmentId(establishment.id)

        then:
        that batchClosings, hasSize(1)
        batchClosings.find().situation == BatchClosingSituation.FINALIZED
    }

    def 'should create batch closing value by establishment'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        Map totalByHirer = createServiceAuthorizations(contracts, establishment, 3)

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        bachClosings.find()?.value == totalByHirer.entrySet().find().value
    }

    def 'should create batch closing value by hirer'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(2, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        Map totalByHirer = createServiceAuthorizations(contracts, establishment, 3)

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings.find { it.hirer.id == totalByHirer.entrySet().find().key }?.batchClosingItems, hasSize(3)
        that bachClosings.find { it.hirer.id == totalByHirer.entrySet().last().key }?.batchClosingItems, hasSize(3)
    }

    def 'should create batch closing value by establishment and hirer'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(2, "valid")
        List<Establishment> establishments = Fixture.from(Establishment.class).uses(jpaProcessor).gimme(2, "valid")
        Map totalByHirer = createServiceAuthorizations(contracts, establishments, 3)

        when:
        service.create(establishments.find().id)
        service.create(establishments.last().id)
        Set<BatchClosing> bachClosingsA = service.findByEstablishmentId(establishments.find().id)
        Set<BatchClosing> bachClosingsB = service.findByEstablishmentId(establishments.last().id)

        then:
        that bachClosingsA.find { it.hirer.id == totalByHirer.entrySet().find().key }?.batchClosingItems, hasSize(3)
        that bachClosingsB.find { it.hirer.id == totalByHirer.entrySet().last().key }?.batchClosingItems, hasSize(3)
    }

    def 'should create batch closing only by invoked establishment'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(2, "valid")
        List<Establishment> establishments = Fixture.from(Establishment.class).uses(jpaProcessor).gimme(2, "valid")
        Map totalByHirer = createServiceAuthorizations(contracts, establishments, 3)

        when:
        service.create(establishments.find().id)
        Set<BatchClosing> bachClosingsA = service.findByEstablishmentId(establishments.find().id)
        Set<BatchClosing> bachClosingsB = service.findByEstablishmentId(establishments.last().id)

        then:
        that bachClosingsA.find { it.hirer.id == totalByHirer.entrySet().find().key }?.batchClosingItems, hasSize(3)
        that bachClosingsB, hasSize(0)
    }

    def 'should create batch closing item by service authorize'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizations(contracts, establishment, 2)

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings.find()?.batchClosingItems, hasSize(2)
    }

    Map createServiceAuthorizations(List<Contract> contract, Establishment establishment, Integer authorizations = 1) {
        return createServiceAuthorizations(contract, Arrays.asList(establishment), authorizations)
    }

    Map createServiceAuthorizationsAt(List<Contract> contract, Establishment establishment, String dateAsText) {
        return createServiceAuthorizations(contract, Arrays.asList(establishment), 2, dateAsText)
    }

    Map createServiceAuthorizations(List<Contract> contracts, List<Establishment> establishments,
                                    numberOfAuthorizations = 1, String dateAsText = "1 day ago") {
        def sumValueByHirer = [:]
        (1..contracts.size()).each { Integer index ->
            def establishment = contracts.size() != establishments.size() ? establishments.find() : establishments.get(index-1)
            def instrumentCredit = fixtureCreator.createInstrumentToContract(contracts.get(index-1))
            def serviceAuthorize = fixtureCreator.createServiceAuthorize(instrumentCredit, establishment, dateAsText)
            sumValueByHirer.put(serviceAuthorize.hirerId(), serviceAuthorize.eventValue * numberOfAuthorizations)
            (1..numberOfAuthorizations).each {
                ServiceAuthorize cloned = BeanUtils.cloneBean(serviceAuthorize)
                serviceAuthorizeService.save(cloned)
            }
        }
        return sumValueByHirer
    }
}
