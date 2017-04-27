package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.Credit
import org.springframework.beans.factory.annotation.Autowired

class CreditServiceTest extends SpockApplicationTests {

    @Autowired
    CreditService service

    @Autowired
    SetupCreator setupCreator

    void 'new credit should be created'(){
        given:
        Credit credit = Fixture.from(Credit.class).gimme("valid")
                .with { paymentRuleGroup = setupCreator.createPaymentRuleGroup()
                        product = setupCreator.createProduct()
                    it }

        when:
        def created  = service.save(credit)
        def result = service.findById(created.id)

        then:
        assert result.id != null
    }
}
