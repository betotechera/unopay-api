package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.PaymentInstrument
import org.springframework.beans.factory.annotation.Autowired

class PaymentInstrumentServiceTest extends SpockApplicationTests {

    @Autowired
    PaymentInstrumentService service

    @Autowired
    SetupCreator setupCreator

    def productUnderTest
    def contractorUnderTest

    void setup(){
        productUnderTest = setupCreator.createProduct()
        contractorUnderTest = setupCreator.createContractor()
    }

    def 'a new Instrument should be created'(){
        given:
        PaymentInstrument instrument = Fixture.from(PaymentInstrument.class).gimme("valid")
                                                .with { product = productUnderTest
                                                        contractor = contractorUnderTest
                                                    it }

        when:
        PaymentInstrument created = service.save(instrument)
        PaymentInstrument result = service.findById(created.id)

        then:
        result != null
    }
}
