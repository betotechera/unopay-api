package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.InstrumentNumberGenerator
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.filter.PaymentInstrumentFilter
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.data.domain.Page

import static org.hamcrest.Matchers.hasSize
import org.springframework.beans.factory.annotation.Autowired
import static spock.util.matcher.HamcrestSupport.that

class PaymentInstrumentServiceTest extends SpockApplicationTests {

    @Autowired
    PaymentInstrumentService service

    @Autowired
    FixtureCreator fixtureCreator

    @Autowired
    InstrumentNumberGenerator generator


    def 'should check Instrument Number generated'(){
        given:
        PaymentInstrument instrument1 = fixtureCreator.createPaymentInstrument("valid")
        PaymentInstrument instrument2 = fixtureCreator.createPaymentInstrument("valid")

        def generatorMock = Mock(InstrumentNumberGenerator)
        service.instrumentNumberGenerator = generatorMock
        when:
        service.save(instrument1)
        PaymentInstrument created = service.save(instrument2)
        PaymentInstrument result = service.findById(created.id)

        then:
        3 * generatorMock.generate(_) >>> ['1000000000000000','1000000000000000','1000000000000001']
        result != null
    }

    def 'a new Instrument should be created'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        service.instrumentNumberGenerator = generator
        when:
        PaymentInstrument created = service.save(instrument)
        PaymentInstrument result = service.findById(created.id)

        then:
        result != null
    }

    def 'known Instrument should be found by number'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        service.instrumentNumberGenerator = generator
        PaymentInstrument created = service.save(instrument)

        when:
        PaymentInstrument result = service.findByNumber(created.number)

        then:
        result != null
    }

    def 'when find by unknown number should return error'(){
        given:
        def number = "123"
        when:
        PaymentInstrument result = service.findByNumber(number)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_INSTRUMENT_NOT_FOUND'
    }

    def 'a Instrument with unknown product id should be created'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        instrument.getProduct().setId('')
        service.instrumentNumberGenerator = generator
        when:
        service.save(instrument)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }

    def 'a Instrument with unknown contractor id should be created'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        instrument.getContractor().setId('')
        service.instrumentNumberGenerator = generator
        when:
        service.save(instrument)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACTOR_NOT_FOUND'
    }

    def 'a Instrument with same external id should not be created'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        service.instrumentNumberGenerator = generator
        when:
        service.save(instrument.with { externalNumberId = 'sameNumber' ; it })
        service.save(instrument.with {  externalNumberId = 'sameNumber' ; id = null; it })

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'EXTERNAL_ID_OF_PAYMENT_INSTRUMENT_ALREADY_EXISTS'
    }

    def 'a Instrument with same external id should not be updated'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        def externalId = 'sameExternalId'
        service.save(instrument.with { externalNumberId = externalId; it })
        PaymentInstrument created = service.save(instrument.with { id = null; externalNumberId = 'id'; it })
        service.instrumentNumberGenerator = generator
        when:
        service.update(created.id, instrument.with { externalNumberId = externalId; it })

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'EXTERNAL_ID_OF_PAYMENT_INSTRUMENT_ALREADY_EXISTS'
    }

    def 'a known Instrument should be updated'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument)
        service.instrumentNumberGenerator = generator
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
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument.with { password = "12345"; it })
        service.instrumentNumberGenerator = generator
        when:
        service.update(created.id, instrument.with { resetPassword = true; it })
        PaymentInstrument result = service.findById(created.id)

        then:
        result.password == null
    }

    def 'a known Instrument with password when not reset password should not reset password'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument.with { password = "12345"; it })
        service.instrumentNumberGenerator = generator
        when:
        service.update(created.id, instrument.with { resetPassword = false; it })
        PaymentInstrument result = service.findById(created.id)

        then:
        result.password != null
    }

    def 'a known Instrument when update should not update password'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument.with { password = null; it })
        service.instrumentNumberGenerator = generator
        when:
        service.update(created.id, instrument.with { password = "12345"; it })
        PaymentInstrument result = service.findById(created.id)

        then:
        result.password == null
    }

    def 'a known Instrument with unknown product id should be updated'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument)
        instrument.getProduct().setId('')
        service.instrumentNumberGenerator = generator
        when:
        service.update(created.id, instrument)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }

    def 'a known Instrument with unknown contractor id should be updated'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument)
        instrument.getContractor().setId('')
        service.instrumentNumberGenerator = generator
        when:
        service.update(created.id, instrument)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACTOR_NOT_FOUND'
    }

    def 'a unknown Instrument should not be updated'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        service.instrumentNumberGenerator = generator
        when:
        service.update('', instrument)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_INSTRUMENT_NOT_FOUND'
    }

    def 'a known Instrument should be found'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument)
        service.instrumentNumberGenerator = generator
        when:
        PaymentInstrument result = service.findById(created.id)

        then:
        result != null
    }

    def 'given a contractor with payment instruments when cancel everyone with specific product should be canceled'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPersistedInstrument()
        fixtureCreator.createPersistedInstrument(instrument.contractor, instrument.product)
        service.instrumentNumberGenerator = generator
        when:
        service.cancel(instrument.contractor.documentNumber, instrument.product)
        def all = service.findByContractorId(instrument.contractorId())

        then:
        all.findAll { it.isCanceled() }.size() == 2
    }

    def 'given a contractor with payment instruments when cancel should not cancel instruments with different product'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPersistedInstrument()
        fixtureCreator.createPersistedInstrument()
        service.instrumentNumberGenerator = generator
        when:
        service.cancel(instrument.contractor.documentNumber, instrument.product)
        def all = service.findByContractorId(instrument.contractorId())

        then:
        all.findAll { it.isCanceled() }.size() == 1
    }

    def 'a known Instrument should be deleted'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        PaymentInstrument created = service.save(instrument)
        service.instrumentNumberGenerator = generator
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

    def 'given a contractor credit instruments when find by contractor should be found'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        service.save(instrument)
        service.instrumentNumberGenerator = generator

        when:
        List<PaymentInstrument> result = service.findByContractorId(instrument.contractorId())

        then:
        that result, hasSize(1)
    }

    def 'given a contractor credit instruments when find by contractor document should be found'(){
        given:
        PaymentInstrument instrument = fixtureCreator.createPaymentInstrument("valid")
        service.save(instrument)
        service.instrumentNumberGenerator = generator

        when:
        List<PaymentInstrument> result = service.findByContractorDocument(instrument.contractor.getDocumentNumber())

        then:
        that result, hasSize(1)
    }

    void 'given a known contractor document number as filter should return a payment instrument for a logged network'(){
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        PaymentInstrument paymentInstrument = Fixture.from(PaymentInstrument.class)
                                                  .uses(jpaProcessor).gimme("valid", new Rule(){{
            add("contractor", contract.getContractor())
            add("product", contract.getProduct())
        }})
        def network = contract.getProduct().getAccreditedNetwork().getId()
        def contractorDocumentNumber = contract.getContractor().getPerson().getDocument().getNumber()


        PaymentInstrumentFilter filter = new PaymentInstrumentFilter()
        filter.contractorDocumentNumber = contractorDocumentNumber
        filter.accreditedNetwork = network

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<PaymentInstrument> paymentInstruments = service.findByFilter(filter, page)

        then:
        assert paymentInstruments.content.size() > 0
    }

    void 'given a unknown contractor document number as filter should not return a payment instrument for a logged network'(){
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        PaymentInstrument paymentInstrument = Fixture.from(PaymentInstrument.class)
                                                 .uses(jpaProcessor).gimme("valid", new Rule(){{
            add("product", contract.getProduct())
        }})
        def network = contract.getProduct().getAccreditedNetwork().getId()
        def contractorDocumentNumber = '00000'


        PaymentInstrumentFilter filter = new PaymentInstrumentFilter()
        filter.contractorDocumentNumber = contractorDocumentNumber
        filter.accreditedNetwork = network

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<PaymentInstrument> paymentInstruments = service.findByFilter(filter, page)

        then:
        assert paymentInstruments.content.size() == 0
    }

    void 'given a known contractor document number as filter should not return a payment instrument for a unlogged network'(){
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        PaymentInstrument paymentInstrument = Fixture.from(PaymentInstrument.class)
                                                  .uses(jpaProcessor).gimme("valid", new Rule(){{
            add("contractor", contract.getContractor())
        }})
        def network = fixtureCreator.createNetwork()
        def contractorDocumentNumber = contract.contractorDocumentNumber()


        PaymentInstrumentFilter filter = new PaymentInstrumentFilter()
        filter.contractorDocumentNumber = contractorDocumentNumber
        filter.accreditedNetwork = network

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<PaymentInstrument> paymentInstruments = service.findByFilter(filter, page)

        then:
        assert paymentInstruments.content.size() == 0
    }

    void 'given a known payment instrument number as filter should return a payment instrument for a logged network'(){
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        PaymentInstrument paymentInstrument = Fixture.from(PaymentInstrument.class)
                .uses(jpaProcessor).gimme("valid", new Rule(){{
            add("product", contract.getProduct())
        }})
        def network = contract.getProduct().getAccreditedNetwork().getId()
        def paymentInstrumentNumber = paymentInstrument.getNumber()


        PaymentInstrumentFilter filter = new PaymentInstrumentFilter()
        filter.number = paymentInstrumentNumber
        filter.accreditedNetwork = network

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<PaymentInstrument> paymentInstruments = service.findByFilter(filter, page)

        then:
        assert paymentInstruments.content.size() > 0
    }
}
