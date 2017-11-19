package br.com.unopay.api.billing.boleto.santander.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import org.springframework.beans.factory.annotation.Autowired

class CobrancaOnlineServiceTest extends SpockApplicationTests{

    @Autowired
    CobrancaOnlineService service

    @Autowired
    FixtureCreator fixtureCreator

}
