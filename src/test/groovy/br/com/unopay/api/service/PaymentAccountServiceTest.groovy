package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.PaymentAccount
import groovy.time.TimeCategory
import org.springframework.beans.factory.annotation.Autowired

class PaymentAccountServiceTest extends SpockApplicationTests {

    @Autowired
    PaymentAccountService service

    @Autowired
    SetupCreator setupCreator

    void setup(){
        Integer.mixin(TimeCategory)
    }

    void 'new payment account should be created'(){
        given:
        PaymentAccount paymentAccount = Fixture.from(PaymentAccount.class).gimme("valid")
                .with { product = setupCreator.createProduct()
                        issuer = setupCreator.createIssuer()
                        paymentRuleGroup = setupCreator.createPaymentRuleGroup()
                        paymentBankAccount = setupCreator.createPaymentBankAccount()
                    it }

        when:
        def created  = service.save(paymentAccount)
        def result = service.findById(created.id)

        then:
        assert result.id != null
    }

    void 'payment account should be created with date time now'(){
        given:
        PaymentAccount paymentAccount = Fixture.from(PaymentAccount.class).gimme("valid")
                .with { product = setupCreator.createProduct()
            issuer = setupCreator.createIssuer()
            paymentRuleGroup = setupCreator.createPaymentRuleGroup()
            paymentBankAccount = setupCreator.createPaymentBankAccount()
            it }

        when:
        def created  = service.save(paymentAccount)
        def result = service.findById(created.id)

        then:
        result.insertionCreatedDateTime > 1.second.ago
        result.insertionCreatedDateTime < 1.second.from.now
    }
}
