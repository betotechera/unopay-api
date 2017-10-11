package br.com.unopay.api.billing.boleto.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.billing.boleto.model.Boleto
import org.springframework.beans.factory.annotation.Autowired

class BoletoServiceTest extends SpockApplicationTests{

    @Autowired
    BoletoService service

    def 'given a valid boleto should be created'(){
        given:
        Boleto boleto = Fixture.from(Boleto.class).gimme("valid")

        when:
        Boleto created = service.save(boleto)
        Boleto result = service.findById(created.id)

        then:
        result != null

    }
}
