package br.com.unopay.api.payment.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.payment.model.PaymentRemittance
import br.com.unopay.api.payment.model.PaymentRemittanceItem
import br.com.unopay.api.payment.model.RemittanceSituation
import org.springframework.beans.factory.annotation.Autowired

class PaymentRemittanceItemServiceTest extends SpockApplicationTests {

    @Autowired
    PaymentRemittanceItemService service

    @Autowired
    FixtureCreator fixtureCreator

    def 'payment remittance item should be created'(){
        given:
        PaymentRemittance paymentRemittance = Fixture.from(PaymentRemittance.class).uses(jpaProcessor)
                                                                                            .gimme("valid", new Rule(){{
            add("issuer", fixtureCreator.createIssuer())
        }})

        PaymentRemittanceItem paymentRemittanceItem = Fixture.from(PaymentRemittanceItem.class)
                                                                                            .gimme("valid", new Rule(){{
            add("paymentRemittance", paymentRemittance)
            add("establishment", fixtureCreator.createEstablishment())
        }})

        when:
        PaymentRemittanceItem created = service.save(paymentRemittanceItem)
        def result = service.findById(created.id)

        then:
        result.id != null
    }

    def 'given a payment remittance item with processing situation should be found'(){
        given:
        PaymentRemittance paymentRemittance = Fixture.from(PaymentRemittance.class).uses(jpaProcessor)
                                                                                            .gimme("valid", new Rule(){{
            add("issuer", fixtureCreator.createIssuer())
        }})
        def establishment = fixtureCreator.createEstablishment()
        PaymentRemittanceItem item = Fixture.from(PaymentRemittanceItem.class).uses(jpaProcessor)
                                                                                            .gimme("valid", new Rule() {
            {
                add("paymentRemittance", paymentRemittance)
                add("establishment", establishment)
                add("situation", RemittanceSituation.PROCESSING)
            }
        })

        when:
        Optional<PaymentRemittanceItem> result = service.findProcessingByEstablishment(establishment.id)

        then:
        result.get().id == item.id
        result.get().situation == RemittanceSituation.PROCESSING

    }

}
