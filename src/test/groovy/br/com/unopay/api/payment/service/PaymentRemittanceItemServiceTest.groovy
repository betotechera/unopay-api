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
    PaymentRemittanceService remittanceService

    @Autowired
    FixtureCreator fixtureCreator

    def 'payment remittance item should be created'(){
        given:
        PaymentRemittance paymentRemittance = Fixture.from(PaymentRemittance.class).gimme("valid")
        remittanceService.save(paymentRemittance)

        PaymentRemittanceItem paymentRemittanceItem = Fixture.from(PaymentRemittanceItem.class)
                                                                                            .gimme("valid", new Rule(){{
            add("paymentRemittance", paymentRemittance)
        }})

        when:
        PaymentRemittanceItem created = service.save(paymentRemittanceItem)
        def result = service.findById(created.id)

        then:
        result.id != null
    }

    def 'given a payment remittance item with processing situation should be found'(){

        given:
        PaymentRemittance paymentRemittance = Fixture.from(PaymentRemittance.class).gimme("valid")
        remittanceService.save(paymentRemittance)
        PaymentRemittanceItem item = Fixture.from(PaymentRemittanceItem.class).gimme("valid", new Rule() {
            {
                add("paymentRemittance", paymentRemittance)
                add("situation", RemittanceSituation.PROCESSING)
            }
        })
        service.save(item)

        when:
        Optional<PaymentRemittanceItem> result = service.findProcessingByEstablishment(item.payee.documentNumber)

        then:
        result.get().id == item.id
        result.get().situation == RemittanceSituation.PROCESSING
    }

}
