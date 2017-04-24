package br.com.unopay.api.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class PaymentInstrumentServiceTest extends SpockApplicationTests {

    @Autowired
    PaymentInstrumentService service

    @Autowired
    SetupCreator setupCreator

    def 'a new Instrument should be created'(){
        given:
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")

        when:
        PaymentInstrument created = service.save(instrument)
        PaymentInstrument result = service.findById(created.id)

        then:
        result != null
    }

    def 'a known Instrument should be updated'(){
        given:
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument)

        when:
        service.update(created.id, instrument)
        PaymentInstrument result = service.findById(created.id)

        then:
        result.number == instrument.number
        result.createdDate.format('dd/MM/yyyy') == instrument.createdDate.format('dd/MM/yyyy')
        result.expirationDate.format('dd/MM/yyyy') == instrument.expirationDate.format('dd/MM/yyyy')
        result.externalNumberId == instrument.externalNumberId
    }

    def 'a known Instrument should be found'(){
        given:
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument)

        when:
        PaymentInstrument result = service.findById(created.id)

        then:
        result != null
    }

    def 'a known Instrument should be deleted'(){
        given:
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument)

        when:
        service.delete(created.id)
        service.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_INSTRUMENT_NOT_FOUND'
    }
}
