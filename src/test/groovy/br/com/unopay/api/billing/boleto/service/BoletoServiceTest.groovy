package br.com.unopay.api.billing.boleto.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.billing.boleto.model.Boleto
import br.com.unopay.api.fileuploader.service.FileUploaderService
import br.com.unopay.api.order.model.Order
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class BoletoServiceTest extends SpockApplicationTests{

    @Autowired
    BoletoService service
    String path
    Order order
    FileUploaderService uploaderServiceMock = Mock(FileUploaderService)

    def setup(){
        order = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid")
        path = "${order.person.documentNumber()}.pdf"
        uploaderServiceMock.uploadBytes(_,_) >> path
        service.fileUploaderService = uploaderServiceMock
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

    def 'should create boleto from known order'(){
        when:
        Boleto created = service.create(order.id)

        then:
        created.issuerDocument == order.product.issuer.documentNumber()
        created.clientDocument == order.person.documentNumber()
        created.value == order.value
        created.orderId == order.id
    }

    def 'when create boleto should be found'(){
        when:
        Boleto created = service.create(order.id)
        Boleto result = service.findById(created.id)

        then:
        result != null
    }

    def 'when create boleto should create with meta information'(){
        when:
        Boleto created = service.create(order.id)

        then:
        created.uri
        created.typingCode
        created.value
        created.createDateTime
        created.expirationDateTime
    }

    def 'when create boleto should increment number'(){
        when:
        Boleto first = service.create(order.id)
        Boleto second = service.create(order.id)

        then:
        first.number < second.number
    }

    def 'when create boleto should upload file'(){
        when:
        service.create(order.id)

        then:
        1 * uploaderServiceMock.uploadBytes(_, _) >> path
    }

    def 'when create with unkown order should error'(){
        when:
        service.create('')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ORDER_NOT_FOUND'
    }
}
