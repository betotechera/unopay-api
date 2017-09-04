package br.com.unopay.api.order.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.order.model.CreditOrder
import org.springframework.beans.factory.annotation.Autowired

class CreditCreditOrderServiceTest extends SpockApplicationTests{

    @Autowired
    CreditOrderService service

    @Autowired
    FixtureCreator fixtureCreator

    def 'a with known person order should be created'(){
        given:
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProduct()
        CreditOrder creditOrder = Fixture.from(CreditOrder.class).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
        }})

        when:
        CreditOrder created = service.save(creditOrder)
        CreditOrder result = service.findById(created.id)

        then:
        result != null

    }
}
