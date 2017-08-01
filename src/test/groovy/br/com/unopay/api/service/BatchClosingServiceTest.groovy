package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import static br.com.six2six.fixturefactory.Fixture.from
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.config.Queues
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.model.BatchClosing
import br.com.unopay.api.model.BatchClosingItem
import br.com.unopay.api.model.BatchClosingSituation
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.DocumentSituation
import br.com.unopay.api.model.IssueInvoiceType
import br.com.unopay.api.model.ServiceAuthorize
import br.com.unopay.api.notification.service.NotificationService
import br.com.unopay.bootcommons.exception.NotFoundException
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
    Notifier notifierMock = Mock(Notifier)

    void setup(){
        service.notificationService = notificationServiceMock
        service.notifier = notifierMock
    }

    def 'should return all batch closing with payment date before today and finalized'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("situation", BatchClosingSituation.FINALIZED)
            add("issuer", issuer)
            add("paymentReleaseDateTime", instant("1 day ago"))
        }})

        when:
        def result = service.findFinalizedByIssuerAndPaymentBeforeToday(issuer.id)

        then:
        that result, hasSize(1)
    }

    def 'should not return batch closing with today payment date and finalized'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("situation", BatchClosingSituation.FINALIZED)
            add("issuer", issuer)
            add("paymentReleaseDateTime", instant("today"))
        }})

        when:
        def result = service.findFinalizedByIssuerAndPaymentBeforeToday(issuer.id)

        then:
        that result, hasSize(0)
    }

    def 'given a batch processing should not open concurrent process'(){
        given:
        List<Contract> contracts = from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = from(Establishment.class).uses(jpaProcessor).gimme("valid")
        from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("situation", BatchClosingSituation.PROCESSING_AUTOMATIC_BATCH)
            add("establishment", establishment)
        }})
        createServiceAuthorizations(contracts, establishment, 1)
        when:
        service.create(new BatchClosing(){{
            setEstablishment(establishment)
            setClosingDateTime(instant("1 day ago"))
        }})

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'BATCH_ALREADY_RUNNING'
    }

    def 'when create should queue'(){
        given:
        List<Contract> contracts = from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizations(contracts, establishment, 1)
        BatchClosing closing = new BatchClosing().with { closingDateTime = instant("1 day ago"); it }

        when:
        service.create(closing)

        then:
        1 * notifierMock.notify(Queues.UNOPAY_BATCH_CLOSING, closing)
    }

    def 'should ever create new batch closing when processed'(){
        given:
        List<Contract> contracts = from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = from(Establishment.class).uses(jpaProcessor).gimme("valid")
        def totalByHirer = createServiceAuthorizationsAt(contracts, establishment, "2 day ago")
        service.process(establishment.id, instant("1 day ago"))
        serviceAuthorizeService.findAll().each { it.batchClosingDateTime = null; serviceAuthorizeService.save(it)}

        when:
        service.process(establishment.id, instant("1 day ago"))
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings, hasSize(2)
        bachClosings.find().value == totalByHirer.entrySet().find().value
        bachClosings.last().value == totalByHirer.entrySet().find().value
    }

    def 'given a canceled batch closing should create new batch closing'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        List<Contract> contracts = from(Contract.class).uses(jpaProcessor).gimme(1, "valid", new Rule(){{
            add("issueInvoice", true)
        }})
        def totalByHirer = createServiceAuthorizationsAt(contracts, user.establishment, "2 day ago")
        def created = service.process(user.establishment.id, instant("1 day ago"))
        service.cancel(user.email, created.id)

        when:
        service.process(user.establishment.id, instant("1 day ago"))
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(user.establishment.id)

        then:
        that bachClosings, hasSize(2)
        bachClosings.find().value == totalByHirer.entrySet().find().value
        bachClosings.last().value == totalByHirer.entrySet().find().value
    }

    def 'given a finalized batch closing should create new batch closing'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid", new Rule(){{
            add("issueInvoice", false)
        }})
        def totalByHirer = createServiceAuthorizationsAt(contracts, user.establishment, "2 day ago")
        service.process(user.establishment.id, instant("1 day ago"))
        serviceAuthorizeService.findAll().each { it.batchClosingDateTime = null; serviceAuthorizeService.save(it)}

        when:
        service.process(user.establishment.id, instant("1 day ago"))
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(user.establishment.id)

        then:
        that bachClosings, hasSize(2)
        bachClosings.find().value == totalByHirer.entrySet().find().value
        bachClosings.last().value == totalByHirer.entrySet().find().value

    }

    def 'should create batch closing by establishment when manual process'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizationsAt(contracts, establishment, "2 day ago")

        when:
        service.process(establishment.id, instant("1 day ago"))
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings, hasSize(1)
    }

    def 'should create batch closing by establishment with invalid date should not be processd'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizationsAt(contracts, establishment, "2 day ago")

        when:
        service.process(establishment.id, instant("2 day ago"))
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings, hasSize(0)
    }

    def 'given a known batch closing when update should be revised'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("establishment", user.establishment)
            add("situation", BatchClosingSituation.PAYMENT_RELEASED)
        }})

        when:
        service.review(batchClosing.id, BatchClosingSituation.DOCUMENT_RECEIVED)
        def result = service.findById(batchClosing.id)

        then:
        result.situation == BatchClosingSituation.DOCUMENT_RECEIVED
    }

    def 'given a known batch closing with canceled situation should not be revised'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("establishment", user.establishment)
            add("situation", BatchClosingSituation.CANCELED)
        }})

        when:
        service.review(batchClosing.id, BatchClosingSituation.DOCUMENT_RECEIVED)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'BATCH_CANCELED'
    }

    def 'given a known batch closing when update to canceled should not be revised'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("establishment", user.establishment)
            add("situation", BatchClosingSituation.CANCELED)
        }})

        when:
        service.review(batchClosing.id, BatchClosingSituation.CANCELED)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'SITUATION_NOT_ALLOWED'
    }

    def 'given a known batch closing with finalized situation should not be revised'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("establishment", user.establishment)
            add("situation", BatchClosingSituation.FINALIZED)
        }})

        when:
        service.review(batchClosing.id, BatchClosingSituation.DOCUMENT_RECEIVED)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'BATCH_FINALIZED'
    }

    def 'given a unknown batch closing should not be revised'(){
        when:
        service.review('', BatchClosingSituation.DOCUMENT_RECEIVED)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'BATCH_CLOSING_NOT_FOUND'
    }

    def 'given a known batch closing without finalized situation when canceled should update items invoice information'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        BatchClosing batchClosing = createNotFinishedBatch(user)

        when:
        service.cancel(user.email, batchClosing.id)
        def result = service.findById(batchClosing.id)

        then:
        result.batchClosingItems.every {
            it.invoiceDocumentSituation == DocumentSituation.CANCELED
        }
    }

    def 'given a known batch closing without finalized situation when canceled should reset service authorized batch closing date'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        BatchClosing batchClosing = createNotFinishedBatch(user)

        when:
        service.cancel(user.email, batchClosing.id)
        def result = service.findById(batchClosing.id)

        then:
        result.batchClosingItems.every {
            it.serviceAuthorize.batchClosingDateTime == null
        }
    }

    def 'given a unknown batch closing should not be canceled'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()

        when:
        service.cancel(user.email, '')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'BATCH_CLOSING_NOT_FOUND'
    }

    def 'given a known batch closing without finalized situation should be canceled'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        BatchClosing batchClosing = createNotFinishedBatch(user)

        when:
        service.cancel(user.email, batchClosing.id)
        def result = service.findById(batchClosing.id)

        then:
        result.situation == BatchClosingSituation.CANCELED
    }


    def 'given a known batch closing with finalized situation should not be canceled'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("establishment", user.establishment)
            add("situation", BatchClosingSituation.FINALIZED)
        }})

        when:
        service.cancel(user.email, batchClosing.id)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'BATCH_FINALIZED'
    }

    def 'given a known batch closing with canceled situation should not be canceled'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("establishment", user.establishment)
            add("situation", BatchClosingSituation.CANCELED)
        }})

        when:
        service.cancel(user.email, batchClosing.id)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'BATCH_CANCELED'
    }

    def 'given a known batch closing from other establishment should not be canceled'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("establishment", user.establishment)
        }})
        fixtureCreator.createBatchItems(batchClosing)

        when:
        service.cancel(fixtureCreator.createUser().email, batchClosing.id)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_BATCH'
    }



    def 'given a known batch closing with issue invoice situation should update only invoice item fields'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issueInvoice", true)
            add("establishment", user.establishment)
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
        service.updateInvoiceInformation(user.email, batchClosingItems)
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
        def user = fixtureCreator.createEstablishmentUser()
        def (List<BatchClosingItem> batchClosingItems, BatchClosing batchClosing) = addInvoiceInformation(user)

        when:
        service.updateInvoiceInformation(user.email, batchClosingItems)
        def result = service.findById(batchClosing.id)

        then:
        result.batchClosingItems.every {
                    it.invoiceDocumentSituation == DocumentSituation.APPROVED
        }
    }


    def 'given a known batch closing with issue invoice situation should update batch situation to received'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        def (List<BatchClosingItem> batchClosingItems, BatchClosing batchClosing) = addInvoiceInformation(user)

        when:
        service.updateInvoiceInformation(user.email, batchClosingItems)
        def result = service.findById(batchClosing.id)

        then:
        result.situation == BatchClosingSituation.DOCUMENT_RECEIVED
    }

    def 'given a known batch closing without issue invoice situation should not be received'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issueInvoice", false)
            add("establishment", user.establishment)
        }})
        List<BatchClosingItem> batchClosingItems = fixtureCreator.createBatchItems(batchClosing)

        batchClosingItems.each {
            it.invoiceNumber = "54654687646798"
            it.invoiceDocumentUri = "file://teste.temp"
            it.invoiceDocumentSituation = DocumentSituation.PENDING
        }

        when:
        service.updateInvoiceInformation(user.email, batchClosingItems)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INVOICE_NOT_REQUIRED_FOR_BATCH'
        assert ex.errors.first().arguments.find() == batchClosing.id
    }

    def 'given a known batch closing but unknown batch item should return error'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issueInvoice", true)
            add("establishment", user.establishment)
        }})
        List<BatchClosingItem> batchClosingItems = fixtureCreator.createBatchItems(batchClosing)

        batchClosingItems.each {
            it.id = ''
            it.invoiceNumber = "54654687646798"
            it.invoiceDocumentUri = "file://teste.temp"
            it.invoiceDocumentSituation = DocumentSituation.PENDING
        }

        when:
        service.updateInvoiceInformation(user.email, batchClosingItems)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'BATCH_CLOSING_ITEM_NOT_FOUND'
    }

    def 'given a known batch closing from other establishment should return error'(){
        given:
        def user = fixtureCreator.createEstablishmentUser()
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("issueInvoice", true)
            add("establishment", user.establishment)
        }})
        List<BatchClosingItem> batchClosingItems = fixtureCreator.createBatchItems(batchClosing)

        batchClosingItems.each {
            it.invoiceNumber = "54654687646798"
            it.invoiceDocumentUri = "file://teste.temp"
            it.invoiceDocumentSituation = DocumentSituation.PENDING
        }

        when:
        service.updateInvoiceInformation(fixtureCreator.createUser().email, batchClosingItems)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_BATCH'
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
        service.process(establishment.id)

        then:
        1 * notificationServiceMock.sendBatchClosedMail(_, _)
    }

    def 'should create batch closing by establishment'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        def (Establishment establishmentA, Establishment establishmentsB) = Fixture.from(Establishment.class)
                                                                                .uses(jpaProcessor).gimme(2, "valid")
        createServiceAuthorizations(contracts, establishmentA,1)
        createServiceAuthorizations(contracts, establishmentsB,3)

        when:
        service.process(establishmentA.id)
        service.process(establishmentsB.id)
        Set<BatchClosing> bachClosingsA = service.findByEstablishmentId(establishmentA.id)
        Set<BatchClosing> bachClosingsB = service.findByEstablishmentId(establishmentsB.id)

        then:
        that bachClosingsA, hasSize(1)
        that bachClosingsA.find().batchClosingItems, hasSize(1)
        that bachClosingsB, hasSize(1)
        that bachClosingsB.find().batchClosingItems, hasSize(3)
    }

    def 'when create batch closing should update service authorize closing date'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizations(contracts, establishment)

        when:
        service.process(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        bachClosings.every {
            it.batchClosingItems.every {
                it.serviceAuthorize.batchClosingDateTime > instant("1 second ago")
            }
        }
    }

    def 'given authorizations of one or more days ago should be processed'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizationsAt(contracts, establishment, "1 day ago")

        when:
        service.process(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings, hasSize(1)
    }

    def 'given authorizations of today should not be processed'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizationsAt(contracts, establishment, "today")

        when:
        service.process(establishment.id)
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
        service.process(establishment.id)
        service.process(establishment.id)
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
        service.process(establishment.id)
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
        service.process(establishment.id)
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
        service.process(establishment.id)
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
        service.process(establishment.id)
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
        service.process(establishments.find().id)
        service.process(establishments.last().id)
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
        service.process(establishments.find().id)
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
        service.process(establishment.id)
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
                serviceAuthorizeService.save(cloned.with {id = null; it})
            }
        }
        return sumValueByHirer
    }

    private BatchClosing createNotFinishedBatch(user) {
        BatchClosing batchClosing = from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("establishment", user.establishment)
                add("situation", BatchClosingSituation.DOCUMENT_RECEIVED)
            }
        })
        fixtureCreator.createBatchItems(batchClosing)
        batchClosing
    }

    private List addInvoiceInformation(user) {
        BatchClosing batchClosing = from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("issueInvoice", true)
                add("establishment", user.establishment)
            }
        })
        List<BatchClosingItem> batchClosingItems = fixtureCreator.createBatchItems(batchClosing)

        batchClosingItems.each {
            it.invoiceNumber = "54654687646798"
            it.invoiceDocumentUri = "file://teste.temp"
            it.invoiceDocumentSituation = DocumentSituation.PENDING
        }
        [batchClosingItems, batchClosing]
    }

}
