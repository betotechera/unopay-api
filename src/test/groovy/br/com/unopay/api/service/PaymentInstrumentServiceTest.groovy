package br.com.unopay.api.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.bootcommons.exception.ConflictException
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

    def 'a Instrument with unknown product id should be created'(){
        given:
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")
        instrument.getProduct().setId('')

        when:
        service.save(instrument)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }

    def 'a Instrument with unknown contractor id should be created'(){
        given:
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")
        instrument.getContractor().setId('')

        when:
        service.save(instrument)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACTOR_NOT_FOUND'
    }

    def 'a Instrument with same external id should not be created'(){
        given:
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")

        when:
        service.save(instrument.with { externalNumberId = 'sameNumber' ; it })
        service.save(instrument.with {  externalNumberId = 'sameNumber' ; id = null; it })

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'EXTERNAL_ID_OF_PAYMENT_INSTRUMENT_ALREADY_EXISTS'
    }

    def 'a Instrument with same external id should not be updated'(){
        given:
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")
        def externalId = 'sameExternalId'
        service.save(instrument.with { externalNumberId = externalId; it })
        PaymentInstrument created = service.save(instrument.with { id = null; externalNumberId = 'id'; it })

        when:
        service.update(created.id, instrument.with { externalNumberId = externalId; it })

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'EXTERNAL_ID_OF_PAYMENT_INSTRUMENT_ALREADY_EXISTS'
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

    def 'a known Instrument with password when reset password should reset password'(){
        given:
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument.with { password = "12345"; it })

        when:
        service.update(created.id, instrument.with { resetPassword = true; it })
        PaymentInstrument result = service.findById(created.id)

        then:
        result.password == null
    }

    def 'a known Instrument with password when not reset password should not reset password'(){
        given:
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument.with { password = "12345"; it })

        when:
        service.update(created.id, instrument.with { resetPassword = false; it })
        PaymentInstrument result = service.findById(created.id)

        then:
        result.password != null
    }

    def 'a known Instrument when update should not update password'(){
        given:
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument.with { password = null; it })

        when:
        service.update(created.id, instrument.with { password = "12345"; it })
        PaymentInstrument result = service.findById(created.id)

        then:
        result.password == null
    }

    def 'a known Instrument with unknown product id should be updated'(){
        given:
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument)
        instrument.getProduct().setId('')

        when:
        service.update(created.id, instrument)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }

    def 'a known Instrument with unknown contractor id should be updated'(){
        given:
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument)
        instrument.getContractor().setId('')

        when:
        service.update(created.id, instrument)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACTOR_NOT_FOUND'
    }

    def 'a unknown Instrument should not be updated'(){
        given:
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")

        when:
        service.update('', instrument)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_INSTRUMENT_NOT_FOUND'
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

    def 'a unknown Instrument should not be deleted'(){
        when:
        service.delete('')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_INSTRUMENT_NOT_FOUND'
    }
}
