package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Issuer
import org.springframework.beans.factory.annotation.Autowired

class IssuerServiceTest  extends SpockApplicationTests {

    @Autowired
    IssuerService service

    def 'a valid issuer should be created'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        Issuer created = service.create(issuer)
        Issuer result = service.findById(created.getId())

        then:
        result != null
    }

}
