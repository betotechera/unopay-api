package br.com.unopay.api.credit.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.credit.model.InstrumentBalance
import org.springframework.beans.factory.annotation.Autowired

class InstrumentBalanceServiceTest  extends SpockApplicationTests {

    @Autowired
    InstrumentBalanceService service

    @Autowired
    FixtureCreator fixtureCreator

    def 'given valid balance should be create'(){
        given:
        InstrumentBalance balance = Fixture.from(InstrumentBalance.class).gimme("valid", new Rule(){{
            add("paymentInstrument", fixtureCreator.createInstrumentToProduct())
        }})

        when:
        InstrumentBalance created = service.save(balance)
        InstrumentBalance result = service.findBydId(created.id)

        then:
        result != null
    }
}
