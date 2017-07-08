package br.com.unopay.api.payment.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.payment.model.PaymentRemittance
import org.springframework.beans.factory.annotation.Autowired

class PaymentRemittanceServiceTest extends SpockApplicationTests {

    @Autowired
    PaymentRemittanceService service

    @Autowired
    FixtureCreator fixtureCreator

    def 'payment remittance should be created'(){
        given:
        PaymentRemittance paymentRemittance = Fixture.from(PaymentRemittance.class).gimme("valid", new Rule(){{
            add("issuer", fixtureCreator.createIssuer())
        }})

        when:
        PaymentRemittance created = service.create(paymentRemittance)
        def result = service.findById(created.id)

        then:
        result.id != null
    }
}
