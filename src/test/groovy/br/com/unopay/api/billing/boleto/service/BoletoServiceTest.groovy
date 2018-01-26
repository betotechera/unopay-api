package br.com.unopay.api.billing.boleto.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.billing.boleto.model.Boleto
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.TituloDto
import br.com.unopay.api.billing.boleto.santander.service.CobrancaOnlineService
import br.com.unopay.api.credit.model.Credit
import br.com.unopay.api.fileuploader.service.FileUploaderService
import br.com.unopay.api.notification.service.NotificationService
import br.com.unopay.api.order.model.Order
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class BoletoServiceTest extends SpockApplicationTests{

    @Autowired
    BoletoService service
    String path
    Order order
    Credit credit
    FileUploaderService uploaderServiceMock = Mock(FileUploaderService)
    CobrancaOnlineService cobrancaOnlineServiceMock = Mock(CobrancaOnlineService)
    NotificationService notificationServiceMock = Mock(NotificationService)

    def setup(){
        credit = Fixture.from(Credit.class).uses(jpaProcessor).gimme("allFields")
        order = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid")
        path = "${order.person.documentNumber()}.pdf"
        uploaderServiceMock.uploadBytes(_,_) >> path
        service.fileUploaderService = uploaderServiceMock
        cobrancaOnlineServiceMock.getTicket(_,_) >> new TituloDto().with { nossoNumero = "1234"; it }
        service.cobrancaOnlineService = cobrancaOnlineServiceMock
        service.notificationService = notificationServiceMock
    }

    def 'given a valid boleto should be created'(){
        given:
        Boleto boleto = Fixture.from(Boleto.class).gimme("valid")

        when:
        Boleto created = service.save(boleto)
        Boleto result = service.findById(created.id)

        then:
        result != null
    }

    def 'should send email when a new boleto is created'(){
        when:
        service.createForOrder(order.id)

        then:
        1 * notificationServiceMock.sendBoletoIssued(order,_)
    }

    def 'should create boleto from known order'(){
        when:
        Boleto created = service.createForOrder(order.id)

        then:
        created.issuerDocument == order.product.issuer.documentNumber()
        created.clientDocument == order.person.documentNumber()
        created.value == order.value
        created.orderId == order.id
    }

    def 'when create boleto should be found'(){
        when:
        Boleto created = service.createForOrder(order.id)
        Boleto result = service.findById(created.id)

        then:
        result != null
    }

    def 'when create boleto should create with meta information'(){
        when:
        Boleto created = service.createForOrder(order.id)

        then:
        created.uri
        created.typingCode
        created.value
        created.createDateTime
        created.expirationDateTime
    }

    def 'when create boleto should increment number'(){
        when:
        Boleto result = service.createForOrder(order.id)

        then:
        result.number
    }

    def 'when create boleto should upload file'(){
        when:
        service.createForOrder(order.id)

        then:
        1 * uploaderServiceMock.uploadBytes(_, _) >> path
    }

    def 'when create with unkown order should error'(){
        when:
        service.createForOrder('')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ORDER_NOT_FOUND'
    }

    def 'should send email when a new boleto is created for credit'(){
        when:
        service.createForCredit(credit)

        then:
        1 * notificationServiceMock.sendBoletoIssued(_,_)
    }

    def 'should create boleto from known credit'(){
        when:
        Boleto created = service.createForCredit(credit)

        then:
        created.issuerDocument == credit.issuer.documentNumber()
        created.clientDocument == credit.hirer.documentNumber
        created.value == credit.value
        created.orderId == credit.id
    }

    def 'when create boleto for credit should be found'(){
        when:
        Boleto created = service.createForCredit(credit)
        Boleto result = service.findById(created.id)

        then:
        result != null
    }

    def 'when create boleto for credit should create with meta information'(){
        when:
        Boleto created = service.createForCredit(credit)

        then:
        created.uri
        created.typingCode
        created.value
        created.createDateTime
        created.expirationDateTime
    }

    def 'when create boleto for credit should increment number'(){
        when:
        Boleto result = service.createForCredit(credit)

        then:
        result.number
    }

    def 'when create boleto for credit should upload file'(){
        when:
        service.createForCredit(credit)

        then:
        1 * uploaderServiceMock.uploadBytes(_, _) >> path
    }

    def 'when create with unkown credit should error'(){
        when:
        service.createForCredit(new Credit())

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'HIRER_CREDIT_NOT_FOUND'
    }
}
