package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Provider
import org.springframework.beans.factory.annotation.Autowired

class ProviderServiceTest extends SpockApplicationTests {

    @Autowired
    ProviderService service

    def 'a valid service should be created'(){
        given:
        Provider provider = Fixture.from(Provider.class).gimme("valid")

        when:
        Provider created = service.create(provider)

        then:
        created != null

    }
}
