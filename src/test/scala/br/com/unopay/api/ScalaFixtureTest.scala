package br.com.unopay.api

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader
import org.scalatest._

trait ScalaFixtureTest extends FlatSpec
with BeforeAndAfterEach with GivenWhenThen with Matchers {  this: Suite =>

  override def beforeEach(): Unit = {
    FixtureFactoryLoader.loadTemplates("br.com.unopay.api")
    super.beforeEach()
  }

}
