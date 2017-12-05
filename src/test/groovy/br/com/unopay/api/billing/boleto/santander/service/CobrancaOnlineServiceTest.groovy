package br.com.unopay.api.billing.boleto.santander.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.TituloDto
import org.springframework.beans.factory.annotation.Autowired

class CobrancaOnlineServiceTest extends SpockApplicationTests{

    @Autowired
    CobrancaOnlineService service

    @Autowired
    FixtureCreator fixtureCreator

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
