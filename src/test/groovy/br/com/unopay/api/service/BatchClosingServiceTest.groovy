package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.model.BatchClosing
import org.springframework.beans.factory.annotation.Autowired

class BatchClosingServiceTest extends SpockApplicationTests {

    @Autowired
    BatchClosingService service

    def 'should create batch closing'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        Issuer issuer = Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid")
        AccreditedNetwork accreditedNetwork = Fixture.from(AccreditedNetwork.class).uses(jpaProcessor).gimme("valid")
        Hirer hirer = Fixture.from(Hirer.class).uses(jpaProcessor).gimme("valid")

        BatchClosing batchClosing = Fixture.from(BatchClosing.class).gimme("valid", new Rule(){{
            add("establishment",establishment)
            add("issuer",issuer)
            add("accreditedNetwork",accreditedNetwork)
            add("hirer",hirer)
        }})

        when:
        def created = service.save(batchClosing)
        def result = service.findById(created.id)

        then:
        result.id != null
    }
}
