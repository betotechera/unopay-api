package br.com.unopay.api.util

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.ScalaApplicationTest
import br.com.unopay.api.model.Contract
import br.com.unopay.api.repository.ProductRepository
import br.com.unopay.api.uaa.model.UserDetail
import ch.qos.logback.core.net.server.Client
import org.springframework.beans.factory.annotation.Autowired

class FixTureTest extends ScalaApplicationTest {

    @Autowired
    var productRepository: ProductRepository = _

    it should "persist references when load" in  {
        val contract = Fixture.from(classOf[Contract]).uses(jpaProcessor).gimme("valid").asInstanceOf[Contract]
        val result = productRepository.findById(contract.getProduct.getId)
        contract.getProduct.getId should not be null
        contract.getContractor.getId should not be null
        contract.getHirer.getId should not be null
        result.isPresent
    }

    "when load known template" should "be found" in {
        val userDetail = Fixture.from(classOf[UserDetail]).gimme("without-group").asInstanceOf[UserDetail]
        userDetail should not be null
    }

    "when load unknown template" should "not be found" in {
        a [IllegalArgumentException] should be thrownBy {
            Fixture.from(classOf[Client]).gimme("valid")
        }
    }
}
