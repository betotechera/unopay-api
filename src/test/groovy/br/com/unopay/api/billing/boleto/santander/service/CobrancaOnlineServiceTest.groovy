package br.com.unopay.api.billing.boleto.santander.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore

class CobrancaOnlineServiceTest extends SpockApplicationTests{

    @Autowired
    CobrancaOnlineService service

    @Autowired
    FixtureCreator fixtureCreator

    @Ignore
    def 'should create ticket'(){
        given:
        def issuer = fixtureCreator.createIssuer()
        issuer.getPaymentAccount().setStation("2XHI")

        when:
        def ticket = service.getTicket(issuer)

        then:
        ticket.cdBarra != null
    }

}
