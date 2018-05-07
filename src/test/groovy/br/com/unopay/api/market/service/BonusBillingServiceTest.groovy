package br.com.unopay.api.market.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import org.springframework.beans.factory.annotation.Autowired

class BonusBillingServiceTest extends SpockApplicationTests {

    @Autowired
    private BonusBillingService service
    @Autowired
    private FixtureCreator fixtureCreator

    void 'should save valid BonusBilling'(){
        given:
        def bonusBilling = fixtureCreator.createBonusBillingToPersist()
        when:
        def result = service.save(bonusBilling)
        then:
        result
    }
}
