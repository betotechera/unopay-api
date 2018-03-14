package br.com.unopay.api

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader
import com.fasterxml.jackson.databind.ObjectMapper
import org.flywaydb.core.Flyway
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.web.FilterChainProxy
import org.springframework.test.context.{ActiveProfiles, ContextConfiguration, TestContextManager}
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.{DefaultMockMvcBuilder, MockMvcBuilders}
import org.springframework.web.context.WebApplicationContext

@RunWith(classOf[JUnitRunner])
abstract class ScalaApplicationTest extends FlatSpec
    with BeforeAndAfterEach with GivenWhenThen with Matchers with SpringTest {

  @Autowired
  var context: WebApplicationContext = _

  @Autowired
  var filterChainProxy: FilterChainProxy = _

  @Autowired
  var flyway: Flyway = _

  @Autowired
  var jpaProcessor: JpaProcessor = _

  var mvc:  MockMvc = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    mvc = MockMvcBuilders.webAppContextSetup(this.context).addFilter[DefaultMockMvcBuilder](filterChainProxy).build()
    flyway.clean()
    flyway.migrate()
  }

  def toJson(obj: Object) : String  = {
    val objectMapper = new ObjectMapper()
    objectMapper.writeValueAsString(obj)
  }
}


@SpringBootTest
@ContextConfiguration(classes = Array(classOf[UnopayApiApplication]))
@WebAppConfiguration
@ActiveProfiles(Array("test"))
trait SpringTest extends BeforeAndAfterEach { this: Suite =>

  override def beforeEach(): Unit = {
    FixtureFactoryLoader.loadTemplates("br.com.unopay.api")
    new TestContextManager(classOf[SpringTest]).prepareTestInstance(this)
    super.beforeEach()
  }
}
