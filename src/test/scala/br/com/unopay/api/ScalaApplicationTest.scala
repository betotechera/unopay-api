package br.com.unopay.api

import br.com.six2six.fixturefactory.function.impl.ChronicFunction
import com.fasterxml.jackson.databind.ObjectMapper
import org.flywaydb.core.Flyway
import org.flywaydb.test.annotation.FlywayTest
import org.flywaydb.test.junit.FlywayTestExecutionListener
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.web.FilterChainProxy
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.context.{ActiveProfiles, ContextConfiguration, TestContextManager, TestExecutionListeners}
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.{DefaultMockMvcBuilder, MockMvcBuilders}
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.context.WebApplicationContext

@RunWith(classOf[JUnitRunner])
abstract class ScalaApplicationTest extends SpringTest {

  @Autowired
  var context: WebApplicationContext = _

  @Autowired
  var filterChainProxy: FilterChainProxy = _

  @Autowired
  var flyway: Flyway = _

  @Autowired
  var jpaProcessor: util.JpaProcessorScala = _

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

  def instant(pattern: String): java.util.Date ={
    new ChronicFunction(pattern).generateValue()
  }
}


@SpringBootTest
@FlywayTest
@EnableTransactionManagement
@ContextConfiguration(classes = Array(classOf[UnopayApiApplication]))
@WebAppConfiguration
@ActiveProfiles(Array("test"))
@TestExecutionListeners(Array(classOf[DependencyInjectionTestExecutionListener], classOf[FlywayTestExecutionListener]))
trait SpringTest extends ScalaFixtureTest { this: Suite =>

  override def beforeEach(): Unit = {
    new TestContextManager(classOf[SpringTest]).prepareTestInstance(this)
    super.beforeEach()
  }
}
