package br.com.unopay.api.billing.boleto.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.billing.boleto.model.Ticket
import br.com.unopay.api.billing.boleto.model.TicketPaymentSource
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.TituloDto
import br.com.unopay.api.billing.boleto.santander.service.CobrancaOnlineService
import br.com.unopay.api.billing.remittance.cnab240.LayoutExtractorSelector
import br.com.unopay.api.billing.remittance.cnab240.RemittanceExtractor
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayout.getBatchSegmentT
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CODIGO_OCORRENCIA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.IDENTIFICACAO_TITULO
import br.com.unopay.api.credit.model.Credit
import br.com.unopay.api.credit.service.CreditService
import br.com.unopay.api.fileuploader.service.FileUploaderService
import br.com.unopay.api.infra.NumberGenerator
import br.com.unopay.api.market.model.NegotiationBilling
import br.com.unopay.api.market.service.NegotiationBillingService
import br.com.unopay.api.notification.service.NotificationService
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.order.service.OrderProcessor
import br.com.unopay.api.order.service.OrderService
import br.com.unopay.api.uaa.exception.Errors
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnovationExceptions
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile

@Slf4j
class TicketServiceTest extends SpockApplicationTests{

    public static final String PAID = "06"
    @Autowired
    private TicketService service
    @Autowired
    private FixtureCreator fixtureCreator

    @Autowired
    private OrderService orderService
    String path
    Order order
    Credit credit
    FileUploaderService uploaderServiceMock = Mock(FileUploaderService)
    CobrancaOnlineService cobrancaOnlineServiceMock = Mock(CobrancaOnlineService)
    NotificationService notificationServiceMock = Mock(NotificationService)
    LayoutExtractorSelector extractorSelectorMock = Mock(LayoutExtractorSelector)
    OrderService orderServiceMock = Mock(OrderService)
    OrderProcessor orderProcessorMock = Mock(OrderProcessor)
    CreditService creditServiceMock = Mock(CreditService)
    NegotiationBillingService negotiationBillingServiceMock = Mock(NegotiationBillingService)
    NumberGenerator numberGeneratorMock = Mock(NumberGenerator)

    def cleanup() {
        service.orderService = orderService
        service.numberGenerator =  getNumberGenerator()
    }

    private NumberGenerator getNumberGenerator() {
        new NumberGenerator(service.repository)
    }

    def setup(){
        credit = Fixture.from(Credit.class).uses(jpaProcessor).gimme("allFields")
        def contract = fixtureCreator.createPersistedContract()
        order = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("contract", contract)
            add("product", contract.product)
        }})
        path = "${order.person.documentNumber()}.pdf"
        uploaderServiceMock.uploadBytes(_,_) >> path
        service.fileUploaderService = uploaderServiceMock
        cobrancaOnlineServiceMock.getTicket(_,_) >> new TituloDto().with { nossoNumero = "1234"; it }
        service.cobrancaOnlineService = cobrancaOnlineServiceMock
        service.notificationService = notificationServiceMock
        service.layoutExtractorSelector = extractorSelectorMock
        service.negotiationBillingService = negotiationBillingServiceMock
        service.orderService = orderServiceMock
        service.creditService = creditServiceMock
        service.orderProcessor = orderProcessorMock
        creditServiceMock.findById(_) >> credit
        orderServiceMock.findById(_) >> order
        orderServiceMock.findIdsByPersonEmail(_) >> []
        numberGeneratorMock.createNumber(_) >> getNumberGenerator().createNumber(10)
        service.numberGenerator = numberGeneratorMock
    }

    def 'when process cnab paid ticket the occurrence code should be paid'(){
        given:
        MockMultipartFile file = createCnabFile()
        Ticket ticket = Fixture.from(Ticket.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("sourceId", order.id)
            add("paymentSource", TicketPaymentSource.CONTRACTOR_CREDIT)
        }})
        def extractor = Mock(RemittanceExtractor)
        extractorSelectorMock.define(batchSegmentT,_) >> extractor
        extractor.extractOnLine(CODIGO_OCORRENCIA, _) >> PAID
        extractor.extractOnLine(IDENTIFICACAO_TITULO, _) >> ticket.number
        numberGeneratorMock.getNumberWithoutLeftPad(_) >> ticket.number

        when:
        service.processTicketReturn(file)
        def result = service.findById(ticket.id)

        then:
        result.occurrenceCode == PAID
    }

    def 'given a contractor payment source when process paid ticket should call order process'(){
        given:
        MockMultipartFile file = createCnabFile()
        Ticket ticket = Fixture.from(Ticket.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("sourceId", order.id)
            add("paymentSource", TicketPaymentSource.CONTRACTOR_CREDIT)
        }})
        def extractor = Mock(RemittanceExtractor)
        extractorSelectorMock.define(batchSegmentT,_) >> extractor
        extractor.extractOnLine(CODIGO_OCORRENCIA, _) >> PAID
        extractor.extractOnLine(IDENTIFICACAO_TITULO, _) >> ticket.number
        numberGeneratorMock.getNumberWithoutLeftPad(_) >> ticket.number

        when:
        service.processTicketReturn(file)

        then:
        1 * orderProcessorMock.processAsPaid(order.id)
        0 * creditServiceMock.processAsPaid(_)
        0 * negotiationBillingServiceMock.processAsPaid(_)
    }

    def 'when process a not paid ticket the occurrence code should not be paid'(){
        given:
        MockMultipartFile file = createCnabFile()
        Ticket ticket = Fixture.from(Ticket.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("sourceId", order.id)
            add("paymentSource", TicketPaymentSource.CONTRACTOR_CREDIT)
        }})
        def extractor = Mock(RemittanceExtractor)
        extractorSelectorMock.define(batchSegmentT,_) >> extractor
        extractor.extractOnLine(CODIGO_OCORRENCIA, _) >> "02"
        extractor.extractOnLine(IDENTIFICACAO_TITULO, _) >> ticket.number
        numberGeneratorMock.getNumberWithoutLeftPad(_) >> ticket.number

        when:
        service.processTicketReturn(file)
        def result = service.findById(ticket.id)

        then:
        result.occurrenceCode == "02"

    }

    def 'given a hirer payment source when process paid ticket should call credit process'(){
        given:
        MockMultipartFile file = createCnabFile()
        Ticket ticket = Fixture.from(Ticket.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("sourceId", credit.id)
            add("paymentSource", TicketPaymentSource.HIRER_CREDIT)
        }})

        def extractor = Mock(RemittanceExtractor)
        extractorSelectorMock.define(batchSegmentT,_) >> extractor
        extractor.extractOnLine(CODIGO_OCORRENCIA, _) >> PAID
        extractor.extractOnLine(IDENTIFICACAO_TITULO, _) >> ticket.number
        numberGeneratorMock.getNumberWithoutLeftPad(_) >> ticket.number

        when:
        service.processTicketReturn(file)

        then:
        1 * creditServiceMock.processAsPaid(credit.id)
        0 * orderProcessorMock.processAsPaid(_)
        0 * negotiationBillingServiceMock.processAsPaid(_)
    }

    def 'given a hirer installment payment source when process paid ticket should call installment process'(){
        given:
        MockMultipartFile file = createCnabFile()
        Ticket ticket = Fixture.from(Ticket.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("sourceId", credit.id)
            add("paymentSource", TicketPaymentSource.HIRER_INSTALLMENT)
        }})

        def extractor = Mock(RemittanceExtractor)
        extractorSelectorMock.define(batchSegmentT,_) >> extractor
        extractor.extractOnLine(CODIGO_OCORRENCIA, _) >> PAID
        extractor.extractOnLine(IDENTIFICACAO_TITULO, _) >> ticket.number
        numberGeneratorMock.getNumberWithoutLeftPad(_) >> ticket.number
        negotiationBillingServiceMock.findById(_) >> Fixture.from(NegotiationBilling).gimme("valid")

        when:
        service.processTicketReturn(file)

        then:
        1 * negotiationBillingServiceMock.processAsPaid(_)
        1 * creditServiceMock.processAsPaid(_)
        0 * orderProcessorMock.processAsPaid(_)

    }

    def 'given a processed ticket should not be processed'(){
        given:
        MockMultipartFile file = createCnabFile()
        Ticket ticket = Fixture.from(Ticket.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("sourceId", credit.id)
            add("paymentSource", TicketPaymentSource.HIRER_CREDIT)
            add("processedAt", instant("1 second ago"))
        }})

        def extractor = Mock(RemittanceExtractor)
        extractorSelectorMock.define(batchSegmentT,_) >> extractor
        extractor.extractOnLine(CODIGO_OCORRENCIA, _) >> PAID
        extractor.extractOnLine(IDENTIFICACAO_TITULO, _) >> ticket.number

        when:
        service.processTicketReturn(file)

        then:
        0 * creditServiceMock.processAsPaid(_)
        0 * orderProcessorMock.processAsPaid(_)
        0 * negotiationBillingServiceMock.processAsPaid(_)
    }

    def 'given a processed ticket for issuer should not be processed'(){
        given:
        def issuer = fixtureCreator.createIssuer()
        MockMultipartFile file = createCnabFile()
        Ticket ticket = Fixture.from(Ticket.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("sourceId", credit.id)
            add("paymentSource", TicketPaymentSource.HIRER_CREDIT)
            add("processedAt", instant("1 second ago"))
            add("issuerDocument", issuer.documentNumber())
        }})

        def extractor = Mock(RemittanceExtractor)
        extractorSelectorMock.define(batchSegmentT,_) >> extractor
        extractor.extractOnLine(CODIGO_OCORRENCIA, _) >> PAID
        extractor.extractOnLine(IDENTIFICACAO_TITULO, _) >> ticket.number

        when:
        service.processTicketReturnForIssuer(issuer, file)

        then:
        0 * creditServiceMock.processAsPaid(_)
        0 * orderProcessorMock.processAsPaid(_)
        0 * negotiationBillingServiceMock.processAsPaid(_)
    }

    def 'given a valid ticket should be created'(){
        given:
        Ticket ticket = Fixture.from(Ticket.class).gimme("valid")

        when:
        Ticket created = service.save(ticket)
        Ticket result = service.findById(created.id)

        then:
        result != null
    }

    def 'should send email when a new ticket is created'(){
        when:
        service.createForOrder(order.id)

        then:
        1 * notificationServiceMock.sendBoletoIssued(order,_)
    }

    def 'should create ticket from known order'(){
        when:
        Ticket created = service.createForOrder(order.id)

        then:
        created.issuerDocument == order.product.issuer.documentNumber()
        created.payerDocument == order.person.documentNumber()
        created.value == order.value
        created.sourceId == order.id
    }

    def 'when process cnab paid ticket the occurrence code for issuer should be paid'(){
        given:
        def issuer = fixtureCreator.createIssuer()
        MockMultipartFile file = createCnabFile()
        Ticket ticket = Fixture.from(Ticket.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("sourceId", order.id)
            add("issuerDocument", issuer.documentNumber())
            add("paymentSource", TicketPaymentSource.HIRER_CREDIT)
        }})
        def extractor = Mock(RemittanceExtractor)
        extractorSelectorMock.define(batchSegmentT,_) >> extractor
        extractor.extractOnLine(CODIGO_OCORRENCIA, _) >> PAID
        extractor.extractOnLine(IDENTIFICACAO_TITULO, _) >> ticket.number
        numberGeneratorMock.getNumberWithoutLeftPad(_) >> ticket.number

        when:
        service.processTicketReturnForIssuer(issuer, file)
        def result = service.findById(ticket.id)

        then:
        result.occurrenceCode == PAID
    }

    def 'given a contractor payment source when process paid ticket  for issuer should call order process'(){
        given:
        def issuer = fixtureCreator.createIssuer()
        MockMultipartFile file = createCnabFile()
        Ticket ticket = Fixture.from(Ticket.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("sourceId", order.id)
            add("paymentSource", TicketPaymentSource.CONTRACTOR_CREDIT)
            add("issuerDocument", issuer.documentNumber())
        }})
        def extractor = Mock(RemittanceExtractor)
        extractorSelectorMock.define(batchSegmentT,_) >> extractor
        extractor.extractOnLine(CODIGO_OCORRENCIA, _) >> PAID
        extractor.extractOnLine(IDENTIFICACAO_TITULO, _) >> ticket.number
        numberGeneratorMock.getNumberWithoutLeftPad(_) >> ticket.number

        when:
        service.processTicketReturnForIssuer(issuer, file)

        then:
        1 * orderProcessorMock.processAsPaid(order.id)
        0 * creditServiceMock.processAsPaid(_)
    }

    def 'when process a not paid ticket the occurrence code  for issuer should not be paid'(){
        given:
        def issuer = fixtureCreator.createIssuer()
        MockMultipartFile file = createCnabFile()
        Ticket ticket = Fixture.from(Ticket.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("sourceId", order.id)
            add("issuerDocument", issuer.documentNumber())
        }})
        def extractor = Mock(RemittanceExtractor)
        extractorSelectorMock.define(batchSegmentT,_) >> extractor
        extractor.extractOnLine(CODIGO_OCORRENCIA, _) >> "02"
        extractor.extractOnLine(IDENTIFICACAO_TITULO, _) >> ticket.number
        numberGeneratorMock.getNumberWithoutLeftPad(_) >> ticket.number

        when:
        service.processTicketReturnForIssuer(issuer, file)
        def result = service.findById(ticket.id)

        then:
        result.occurrenceCode == "02"

    }

    def 'given a hirer payment source when process paid ticket  for issuer should call credit process'(){
        given:
        def issuer = fixtureCreator.createIssuer()
        MockMultipartFile file = createCnabFile()
        Ticket ticket = Fixture.from(Ticket.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("sourceId", credit.id)
            add("paymentSource", TicketPaymentSource.HIRER_CREDIT)
            add("issuerDocument", issuer.documentNumber())
        }})
        def extractor = Mock(RemittanceExtractor)
        extractorSelectorMock.define(batchSegmentT,_) >> extractor
        extractor.extractOnLine(CODIGO_OCORRENCIA, _) >> PAID
        extractor.extractOnLine(IDENTIFICACAO_TITULO, _) >> ticket.number
        numberGeneratorMock.getNumberWithoutLeftPad(_) >> ticket.number

        when:
        service.processTicketReturnForIssuer(issuer, file)

        then:
        1 * creditServiceMock.processAsPaid(credit.id)
        0 * orderProcessorMock.processAsPaid(_)
    }


    def 'when create ticket should be found'(){
        when:
        Ticket created = service.createForOrder(order.id)
        Ticket result = service.findById(created.id)

        then:
        result != null
    }

    def 'when create ticket should create with meta information'(){
        when:
        Ticket created = service.createForOrder(order.id)

        then:
        created.uri
        created.typingCode
        created.value
        created.createDateTime
        created.expirationDateTime
    }

    def 'when create ticket should increment number'(){
        when:
        Ticket result = service.createForOrder(order.id)

        then:
        result.number
    }

    def 'when create ticket should upload file'(){
        when:
        service.createForOrder(order.id)

        then:
        1 * uploaderServiceMock.uploadBytes(_, _) >> path
    }

    def 'when create with known number should return error'(){
        given:
        service.createForOrder(order.id)
        numberGeneratorMock.createNumber(_) >>> ['1234', '1234']

        when:
        service.createForOrder(order.id)

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'TICKET_NUMBER_ALREADY_EXISTS'
    }

    def 'when create with unknown order should return error'(){
        when:
        service.createForOrder('')

        then:
        orderServiceMock.findById('') >> { throw UnovationExceptions.notFound().withErrors(Errors.ORDER_NOT_FOUND) }
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ORDER_NOT_FOUND'
    }

    def 'should send email when a new ticket is created for credit'(){
        when:
        service.createForCredit(credit)

        then:
        1 * notificationServiceMock.sendBoletoIssued(_,_)
    }

    def 'should create ticket from known credit'(){
        when:
        Ticket created = service.createForCredit(credit)

        then:
        created.issuerDocument == credit.issuer.documentNumber()
        created.payerDocument == credit.hirer.documentNumber
        created.value == credit.value
        created.sourceId == credit.id
    }

    def 'when create ticket for credit should be found'(){
        when:
        Ticket created = service.createForCredit(credit)
        Ticket result = service.findById(created.id)

        then:
        result != null
    }

    def 'when create ticket for credit should create with meta information'(){
        when:
        Ticket created = service.createForCredit(credit)

        then:
        created.uri
        created.typingCode
        created.value
        created.createDateTime
        created.expirationDateTime
    }

    def 'when create ticket for credit should increment number'(){
        when:
        Ticket result = service.createForCredit(credit)

        then:
        result.number
    }

    def 'when create ticket for credit should upload file'(){
        when:
        service.createForCredit(credit)

        then:
        1 * uploaderServiceMock.uploadBytes(_, _) >> path
    }

    def 'when create with known number for credit should return error'(){
        given:
        service.createForCredit(credit)
        numberGeneratorMock.createNumber(_) >>> ['1234', '1234']

        when:
        service.createForCredit(credit)

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'TICKET_NUMBER_ALREADY_EXISTS'
    }

    def 'when create with unknown credit should return error'(){
        when:
        service.createForCredit(new Credit())

        then:
        creditServiceMock.findById(null) >> {
            throw UnovationExceptions.notFound().withErrors(Errors.HIRER_CREDIT_NOT_FOUND)
        }
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'HIRER_CREDIT_NOT_FOUND'
    }

    private MockMultipartFile createCnabFile() {
        String cnab240 = """03300000        2015200249000118112740130011355     009195386                       229012018      000012040                                                                          
03303881T01  040 2015200249000118009195386           112740130011355                                                                                   0000001229012018                                         
0330388300001T 02112740130011355        000000000020326715           0102201800000000000079903303190                         00100001234567895601300113550000000000000000000000000                      
0330388300002U 020000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002901201829012018000000000000000000000000000                              000                           
03303885         0000040000070000000000000559300000000000000000000000000000000000000000000000000000000000000000000000000006                                                                                                                     
03303889         000001000008                                                                                                                                                                                                                   
"""
        new MockMultipartFile('file', cnab240.getBytes())
    }
}
