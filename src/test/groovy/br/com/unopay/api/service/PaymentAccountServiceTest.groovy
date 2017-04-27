package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.PaymentAccount
import org.springframework.beans.factory.annotation.Autowired

class PaymentAccountServiceTest extends SpockApplicationTests {

    @Autowired
    PaymentAccountService service

    @Autowired
    SetupCreator setupCreator

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
}
