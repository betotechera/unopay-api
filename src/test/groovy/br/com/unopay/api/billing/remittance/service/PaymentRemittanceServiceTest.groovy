package br.com.unopay.api.billing.remittance.service

import static br.com.six2six.fixturefactory.Fixture.from
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.BankAccount
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.billing.remittance.cnab240.Cnab240Generator
import br.com.unopay.api.billing.remittance.cnab240.LayoutExtractorSelector
import br.com.unopay.api.billing.remittance.cnab240.RemittanceExtractor
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayout.getBatchSegmentA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS
import br.com.unopay.api.billing.remittance.model.PaymentOperationType
import br.com.unopay.api.billing.remittance.model.PaymentRemittance
import br.com.unopay.api.billing.remittance.model.PaymentTransferOption
import br.com.unopay.api.billing.remittance.model.RemittancePayer
import br.com.unopay.api.billing.remittance.model.RemittanceSituation
import br.com.unopay.api.billing.remittance.model.filter.RemittanceFilter
import br.com.unopay.api.config.Queues
import br.com.unopay.api.credit.model.Credit
import br.com.unopay.api.credit.model.CreditInsertionType
import br.com.unopay.api.credit.model.CreditSituation
import br.com.unopay.api.fileuploader.service.FileUploaderService
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.model.BatchClosing
import br.com.unopay.api.model.BatchClosingSituation
import br.com.unopay.api.notification.service.NotificationService
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import spock.lang.Unroll
import static spock.util.matcher.HamcrestSupport.that

class PaymentRemittanceServiceTest extends SpockApplicationTests {

    @Autowired
    PaymentRemittanceService service

    @Autowired
    PaymentRemittanceItemService itemService

    @Autowired
    FixtureCreator fixtureCreator

    Cnab240Generator cnab240GeneratorMock = Mock(Cnab240Generator)
    FileUploaderService uploaderServiceMock = Mock(FileUploaderService)
    LayoutExtractorSelector extractorSelectorMock = Mock(LayoutExtractorSelector)
    RemittanceExtractor extractorMock = Mock(RemittanceExtractor)
    Notifier notifierMock = Mock(Notifier)
    NotificationService notificationServiceMock = Mock(NotificationService)

    void setup() {
        service.cnab240Generator = cnab240GeneratorMock
        service.fileUploaderService = uploaderServiceMock
        service.layoutExtractorSelector = extractorSelectorMock
        service.notificationService = notificationServiceMock
        service.notifier = notifierMock
        cnab240GeneratorMock.generate(_,_) >> '005;006'
        extractorSelectorMock.define(getBatchSegmentA(),_) >> extractorMock
    }

    def 'when process debit cnab should notify credit'(){
        given:
        def remittancePersisted = createRemittanceFoCredit()
        def wrongRemittance = remittancePersisted.with { operationType = PaymentOperationType.DEBIT; it  }
        def currentDate = instant("now")
        MockMultipartFile file = createCnabFile(wrongRemittance, currentDate)
        extractorMock.extractOnLine(OCORRENCIAS, _) >> '00'
        when:
        service.processReturn(file)

        then:
        remittancePersisted.remittanceItems.size() * notifierMock.notify(Queues.CREDIT_PROCESSED, _)
    }

    def 'given a debit cnab with ocurrences should not notify credit'(){
        given:
        def remittancePersisted = createRemittanceFoCredit()
        def wrongRemittance = remittancePersisted.with { operationType = PaymentOperationType.DEBIT; it  }
        def currentDate = instant("now")
        MockMultipartFile file = createCnabFile(wrongRemittance, currentDate)
        extractorMock.extractOnLine(OCORRENCIAS, _) >> '01'
        when:
        service.processReturn(file)

        then:
        0 * notifierMock.notify(Queues.CREDIT_PROCESSED, _)
    }

    def 'when process cnab then the issuer bank agreement number field should be equals persisted remittance'(){
        given:
        def remittancePersisted = createRemittanceForBatch()
        def wrongRemittance = remittancePersisted.with { payer.accountNumber = 'AAAAA'; it }
        def currentDate = instant("now")
        MockMultipartFile file = createCnabFile(wrongRemittance, currentDate)
        extractorMock.extractOnLine(OCORRENCIAS, _) >> '00'
        when:
        service.processReturn(file)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'REMITTANCE_WITH_INVALID_DATA'
    }

    def 'when process cnab then the issuer bank code field should be equals persisted remittance'(){
        given:
        def remittancePersisted = createRemittanceForBatch()
        def wrongRemittance = remittancePersisted.with { payer.agency = 'BBBB'; it }
        def currentDate = instant("now")
        MockMultipartFile file = createCnabFile(wrongRemittance, currentDate)
        extractorMock.extractOnLine(OCORRENCIAS, _) >> '00'
        when:
        service.processReturn(file)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'REMITTANCE_WITH_INVALID_DATA'
    }

    def 'when process cnab then the issuer document field should be equals persisted remittance'(){
        given:
        def remittancePersisted = createRemittanceForBatch()
        def wrongRemittance = remittancePersisted.with { payer.documentNumber = 'AAAAA'; it }
        def currentDate = instant("now")
        MockMultipartFile file = createCnabFile(wrongRemittance, currentDate)
        extractorMock.extractOnLine(OCORRENCIAS, _) >> '00'
        when:
        service.processReturn(file)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'REMITTANCE_WITH_INVALID_DATA'
    }

    def 'when process return should update return date'(){
        given:
        def remittance = createRemittanceForBatch()
        def currentDate = instant("now")
        MockMultipartFile file = createCnabFile(remittance, currentDate)
        extractorMock.extractOnLine(OCORRENCIAS, _) >> '00'
        when:
        service.processReturn(file)
        def result = service.findByPayerDocument(remittance.payer.documentNumber).find()

        then:
        result.submissionReturnDateTime < instant("1 second from now")
        result.submissionReturnDateTime > instant("1 second ago")
    }

    def 'given a cnab with right debit should have successfully situation'(){
        given:
        def result = createRemittanceForBatch()
        def currentDate = instant("now")
        MockMultipartFile file = createCnabFile(result, currentDate)
        extractorMock.extractOnLine(OCORRENCIAS, _) >> '00'
        when:
        service.processReturn(file)

        then:
        def documents = result.remittanceItems.collect { it.payee.documentNumber }
        documents.every {
            itemService.findByEstablishmentDocument(it)?.situation == RemittanceSituation.RETURN_PROCESSED_SUCCESSFULLY
        }
    }

    def 'given a cnab without right debit should have error situation'(){
        given:
        def result = createRemittanceForBatch()
        def currentDate = instant("now")
        MockMultipartFile file = createCnabFile(result, currentDate)
        extractorMock.extractOnLine(OCORRENCIAS, _) >> '01'
        when:
        service.processReturn(file)

        then:
        def documents = result.remittanceItems.collect { it.payee.documentNumber }
        documents.every {
            itemService.findByEstablishmentDocument(it)?.situation == RemittanceSituation.RETURN_PROCESSED_WITH_ERROR
        }
    }

    def 'given valid cnab file should update occurrence code'(){
        given:
        def result = createRemittanceForBatch()
        def currentDate = instant("now")
        MockMultipartFile file = createCnabFile(result, currentDate)
        extractorMock.extractOnLine(OCORRENCIAS, _) >> '00'
        when:
        service.processReturn(file)

        then:
        def documents = result.remittanceItems.collect { it.payee.documentNumber }
        documents.every {
            itemService.findByEstablishmentDocument(it)?.occurrenceCode == '00'
        }
    }

    def 'a created remittance should have remittance file generated situation'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        def issuerBanK = issuer.paymentAccount.bankAccount.bacenCode()
        createBatchForBank(issuerBanK, issuer)

        when:
        service.createForBatch(issuer.id)
        def result = service.findByPayerDocument(issuer.documentNumber())

        then:
        that result, hasSize(1)
        result.find().situation == RemittanceSituation.REMITTANCE_FILE_GENERATED
        result.every { it.situation == RemittanceSituation.REMITTANCE_FILE_GENERATED }
    }

    def 'a credit remittance should have debit operation type'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        createCredit(issuer)

        when:
        service.createForCredit(issuer.id)
        def result = service.findByPayerDocument(issuer.documentNumber())

        then:
        result.find().operationType == PaymentOperationType.DEBIT
    }

    def 'a batch remittance should have credit operation type'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        def issuerBanK = issuer.paymentAccount.bankAccount.bacenCode()
        createBatchForBank(issuerBanK, issuer)

        when:
        service.createForBatch(issuer.id)
        def result = service.findByPayerDocument(issuer.documentNumber())

        then:
        result.find().operationType == PaymentOperationType.CREDIT
    }

    def 'should not send email when a new batch remittance is created'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        def issuerBanK = issuer.paymentAccount.bankAccount.bacenCode()
        createBatchForBank(issuerBanK, issuer)

        when:
        service.createForBatch(issuer.id)

        then:
        0 * notificationServiceMock.sendRemittanceCreatedMail(issuer.financierMailForRemittance,_)
    }

    def 'when try create remittance when has running should return error'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        def payer = from(RemittancePayer.class).gimme("valid", new Rule(){{
            add("documentNumber", issuer.documentNumber())
        }})
        PaymentRemittance remittance = from(PaymentRemittance.class).gimme("valid", new Rule(){{
            add("situation", RemittanceSituation.PROCESSING)
            add("payer", payer)
        }})
        service.save(remittance)

        when:
        service.execute(new RemittanceFilter(){{
            setId(issuer.id)
        }})

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'REMITTANCE_ALREADY_RUNNING'
    }

    def 'when create should queue'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        def filter = new RemittanceFilter(){{ setId(issuer.id)}}

        when:
        service.execute(filter)

        then:
        1 * notifierMock.notify(Queues.PAYMENT_REMITTANCE, filter)
    }

    def 'should send email when a new credit remittance is created'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        def hirer = fixtureCreator.createHirer()
        from(Credit.class).uses(jpaProcessor).gimme(1, "allFields", new Rule() {{
            add("issuer", issuer)
            add("hirer",  hirer)
            add("situation", CreditSituation.PROCESSING)
            add("creditInsertionType", CreditInsertionType.DIRECT_DEBIT)
        }})

        when:
        service.createForCredit(issuer.id)

        then:
        1 * notificationServiceMock.sendRemittanceCreatedMail(issuer.financierMailForRemittance,_)
    }


    def 'should create a new remittance from credit when execute'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        def hirer = fixtureCreator.createHirer()
        from(Credit.class).uses(jpaProcessor).gimme(1, "allFields", new Rule() {{
                add("issuer", issuer)
                add("hirer",  hirer)
                add("situation", CreditSituation.PROCESSING)
                add("creditInsertionType", CreditInsertionType.DIRECT_DEBIT)
        }})

        when:
        service.createForCredit(issuer.id)
        def result = service.findByPayerDocument(issuer.documentNumber())

        then:
        that result, hasSize(1)
    }

    def 'should create a new remittance from credit with right value'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        def hirer = fixtureCreator.createHirer()
        Set<Credit> credits = from(Credit.class).uses(jpaProcessor).gimme(3, "allFields", new Rule() {{
                add("issuer", issuer)
                add("hirer", hirer)
                add("situation", CreditSituation.PROCESSING)
                add("creditInsertionType", CreditInsertionType.DIRECT_DEBIT)
        }})
        BigDecimal total = credits.collect { it.value }.sum()
        when:
        service.createForCredit(issuer.id)
        def result = service.findByPayerDocument(issuer.documentNumber())

        then:
        that result, hasSize(1)
        result.find().getTotal() == total
    }

    @Unroll
    'should not create a remittance with #situation credit situation'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        def product = fixtureCreator.createProduct()
        def hirer = fixtureCreator.createHirer()
        def creditSituation = situation
        from(Credit.class).uses(jpaProcessor).gimme(3, "allFields", new Rule() {{
            add("issuer", issuer)
            add("hirer", hirer)
            add("situation", creditSituation)
            add("creditInsertionType", CreditInsertionType.DIRECT_DEBIT)
            add("product", product)
        }})

        when:
        service.createForCredit(issuer.id)
        def result = service.findByPayerDocument(issuer.documentNumber())

        then:
        that result, hasSize(0)

        where:
        _ | situation
        _ | CreditSituation.AVAILABLE
        _ | CreditSituation.CANCELED
        _ | CreditSituation.CONFIRMED
        _ | CreditSituation.EXPIRED
        _ | CreditSituation.TO_COLLECT
    }

    @Unroll
    'should not create a remittance with #insertion credit insertion type'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        def hirer = fixtureCreator.createHirer()
        def insertionType = insertion
        from(Credit.class).uses(jpaProcessor).gimme(3, "allFields", new Rule() {{
            add("issuer", issuer)
            add("hirer", hirer)
            add("situation", CreditSituation.PROCESSING)
            add("creditInsertionType", insertionType)
        }})

        when:
        service.createForCredit(issuer.id)
        def result = service.findByPayerDocument(issuer.documentNumber())

        then:
        that result, hasSize(0)

        where:
        _ | insertion
        _ | CreditInsertionType.BOLETO
        _ | CreditInsertionType.CREDIT_CARD
    }

    def 'should ever create a new remittance when execute'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        def issuerBanK = issuer.paymentAccount.bankAccount.bacenCode()
        createBatchForBank(issuerBanK, issuer)
        service.createForBatch(issuer.id)

        when:
        createBatchForBank(issuerBanK, issuer)
        service.createForBatch(issuer.id)
        def result = service.findByPayerDocument(issuer.documentNumber())

        then:
        that result, hasSize(2)
    }

    def 'a remittanceItem to the same bank of the issuer and remittanceItem of other banks should have different transfer option'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        def issuerBanK = issuer.paymentAccount.bankAccount.bacenCode()
        def(BankAccount bankAccountA, BankAccount bankAccountB, BankAccount bankAccountC) = from(BankAccount.class)
                                                                    .uses(jpaProcessor).gimme(3, "valid", new Rule(){{
            add("bank.bacenCode", uniqueRandom(473, 477, issuerBanK))
        }})
        def (Establishment a, Establishment b, Establishment c) = from(Establishment.class)
                                                                     .uses(jpaProcessor).gimme(3,"valid", new Rule(){{
            add("bankAccount", uniqueRandom(bankAccountA, bankAccountB, bankAccountC))
        }})
        from(BatchClosing.class).uses(jpaProcessor).gimme(3, "valid", new Rule(){{
            add("situation", BatchClosingSituation.FINALIZED)
            add("issuer", issuer)
            add("establishment", uniqueRandom(a,b,c))
            add("paymentReleaseDateTime", instant("1 day ago"))
        }})

        when:
        service.createForBatch(issuer.id)
        def result = service.findByPayerDocument(issuer.documentNumber())

        then:
        that result, hasSize(1)
        result.find { it.remittanceItems
                .findAll { it.payee.bankCode == issuerBanK }
                .every{ it.transferOption == PaymentTransferOption.CURRENT_ACCOUNT_CREDIT}
        }
        result.find { it.remittanceItems
                .findAll { it.payee.bankCode in [473, 477] }
                .every { it.transferOption == PaymentTransferOption.DOC_TED}
        }
    }

    def 'given a issuer without batch closed should not create remittance'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()

        when:
        service.createForBatch(issuer.id)
        def result = service.findByPayerDocument(issuer.documentNumber())

        then:
        result.isEmpty()
    }

    def 'when create payment remittance should update cnab240'(){
        given:
        def issuer = issuerWithBatchClosed()

        when:
        service.createForBatch(issuer.id)

        then:
        1 * uploaderServiceMock.uploadCnab240(!null,!null)
    }

    def 'when create payment remittance should generate cnab240 String'(){
        given:
        def issuer = issuerWithBatchClosed()

        when:
        service.createForBatch(issuer.id)

        then:
        1 * cnab240GeneratorMock.generate(!null,!null) >> '005;006'
    }

    def 'payment remittance should be created'(){
        given:
        PaymentRemittance paymentRemittance = from(PaymentRemittance.class).gimme("valid")

        when:
        PaymentRemittance created = service.save(paymentRemittance)
        def result = service.findById(created.id)

        then:
        result.id != null
    }

    def 'when create payment remittance should generate a sequential remittance number'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        when:
        from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("situation", BatchClosingSituation.FINALIZED)
            add("issuer", issuer)
            add("paymentReleaseDateTime", instant("1 day ago"))
        }})
        service.createForBatch(issuer.id)
        from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("situation", BatchClosingSituation.FINALIZED)
            add("issuer", issuer)
            add("paymentReleaseDateTime", instant("1 day ago"))
        }})
        service.createForBatch(issuer.id)
        def result = service.findByPayerDocument(issuer.documentNumber())

        then:
        result.find().number == '1'
        result.last().number == '2'
    }

    def 'payment remittance should be created with right issuer'(){
        given:
        def issuer = issuerWithBatchClosed()

        when:
        service.createForBatch(issuer.id)
        def result = service.findByPayerDocument(issuer.documentNumber())

        then:
        result.find().payer.documentNumber == issuer.documentNumber()
        result.find().payer.bankCode == issuer.paymentAccount.bankAccount.bacenCode()
    }

    def 'should create payment remittance by Issuer'(){
        given:
        def issuer = fixtureCreator.createIssuer()
        from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("situation", BatchClosingSituation.FINALIZED)
            add("issuer", issuer)
            add("paymentReleaseDateTime", instant("1 day ago"))
        }})

        when:
        service.createForBatch(issuer.id)
        def all = service.findByPayerDocument(issuer.documentNumber())

        then:
        that all, hasSize(1)
        that all.find().remittanceItems, hasSize(1)
    }

    def 'remittance item value should be a sum of batch closing value by establishment'(){
        given:
        def issuer = fixtureCreator.createIssuer()
        def issuerBanK = issuer.paymentAccount.bankAccount.bacenCode()
        BankAccount bankAccount = from(BankAccount.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("bank.bacenCode", issuerBanK)
        }})
        def (Establishment establishmentA, Establishment establishmentB) = from(Establishment.class)
                                                                    .uses(jpaProcessor).gimme(2, "valid", new Rule() {{
            add("bankAccount", bankAccount)
        }})
        def (BatchClosing batchClosingA, BatchClosing batchClosingB) = from(BatchClosing.class)
                                                                .uses(jpaProcessor).gimme(2, "valid", new Rule() {{
            add("situation", BatchClosingSituation.FINALIZED)
            add("establishment", uniqueRandom(establishmentA, establishmentB))
            add("issuer", issuer)
            add("paymentReleaseDateTime", instant("1 day ago"))
        }})

        when:
        service.createForBatch(issuer.id)
        def all = service.findByPayerDocument(issuer.documentNumber())

        then:
        that all, hasSize(1)
        that all.find().remittanceItems, hasSize(2)
        all.find().remittanceItems.find {
            batchClosingA.establishmentDocument() == it.payee.documentNumber }.value == batchClosingA.value
        all.find().remittanceItems.find {
            batchClosingB.establishmentDocument() == it.payee.documentNumber }.value == batchClosingB.value
    }

    def 'remittance item value should be a sum of batch closing value of establishment'(){
        given:
        def issuer = fixtureCreator.createIssuer()
        Establishment establishment = from(Establishment.class).uses(jpaProcessor).gimme("valid")
        def (BatchClosing batchClosingA, BatchClosing batchClosingB) = from(BatchClosing.class)
                                                                    .uses(jpaProcessor).gimme(2, "valid", new Rule() {{
            add("situation", BatchClosingSituation.FINALIZED)
            add("establishment", establishment)
            add("issuer", issuer)
            add("paymentReleaseDateTime", instant("1 day ago"))
        }})

        when:
        service.createForBatch(issuer.id)
        def all = service.findByPayerDocument(issuer.documentNumber())

        then:
        that all, hasSize(1)
        that all.find().remittanceItems, hasSize(1)
        all.find().remittanceItems.find().value == batchClosingA.value + batchClosingB.value
    }

    private Issuer issuerWithBatchClosed() {
        Issuer issuer = fixtureCreator.createIssuer()
        from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("situation", BatchClosingSituation.FINALIZED)
                add("issuer", issuer)
                add("paymentReleaseDateTime", instant("1 day ago"))
            }
        })
        issuer
    }

    private void createBatchForBank(issuerBanK, issuer) {
        BankAccount bankAccount = from(BankAccount.class).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("bank.bacenCode", issuerBanK)
            }
        })
        Establishment establishment = from(Establishment.class).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("bankAccount", bankAccount)
            }
        })
        from(BatchClosing.class).uses(jpaProcessor).gimme(3, "valid", new Rule() {
            {
                add("situation", BatchClosingSituation.FINALIZED)
                add("issuer", issuer)
                add("establishment", establishment)
                add("paymentReleaseDateTime", instant("1 day ago"))
            }
        })
    }

    private PaymentRemittance createRemittanceForBatch(){
        Issuer issuer = fixtureCreator.createIssuer()
        def issuerBanK = issuer.paymentAccount.bankAccount.bacenCode()
        createBatchForBank(issuerBanK, issuer)
        service.createForBatch(issuer.id)
        return service.findByPayerDocument(issuer.documentNumber()).find()
    }

    private PaymentRemittance createRemittanceFoCredit(){
        Issuer issuer = fixtureCreator.createIssuer()
        createCredit(issuer)
        service.createForCredit(issuer.id)
       return service.findByPayerDocument(issuer.documentNumber()).find()
    }

    private void createCredit(issuer) {
        def hirer = fixtureCreator.createHirer()
        from(Credit.class).uses(jpaProcessor).gimme(1, "allFields", new Rule() {
            {
                add("issuer", issuer)
                add("hirer", hirer)
                add("situation", CreditSituation.PROCESSING)
                add("creditInsertionType", CreditInsertionType.DIRECT_DEBIT)
            }
        })
    }


    private MockMultipartFile createCnabFile(PaymentRemittance remittance, Date currentDate) {
        String cnab240 = new Cnab240Generator().generate(remittance, currentDate)
        new MockMultipartFile('file', cnab240.getBytes())
    }
}
