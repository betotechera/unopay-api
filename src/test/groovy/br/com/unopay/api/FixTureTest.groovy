package br.com.unopay.api

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.model.Contract
import br.com.unopay.api.repository.ProductRepository
import br.com.unopay.api.uaa.model.UserDetail
import ch.qos.logback.core.net.server.Client
import org.springframework.beans.factory.annotation.Autowired

class FixTureTest  extends SpockApplicationTests{

    @Autowired
    ProductRepository productRepository

    void 'should persist references when load'(){
        when:
        Contract contract = Fixture.from(Contract.class).uses(jpaProcessor).gimme("valid")
        def result = productRepository.findById(contract.product.id)
        then:
        contract.product.id != null
        contract.contractor.id != null
        contract.hirer.id != null
        result.isPresent()
    }

    void 'when load known template should be found'(){
        when:
        UserDetail userDetail = Fixture.from(UserDetail.class).gimme("without-group")

        then:
        userDetail != null
    }

    void 'when load unknown template should not be found'(){
        when:
        Fixture.from(Client.class).gimme("valid")

        then:
        thrown(IllegalArgumentException)
    }
}
