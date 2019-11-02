package br.com.unopay.api.market.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.ScalaFixtureTest

class HirerProductTest extends ScalaFixtureTest {

    it should "be equal" in {
        val a: HirerProduct = Fixture.from(classOf[HirerProduct]).gimme("valid")
        a == a
    }

    it should "not be equal" in {
        val a: HirerProduct = Fixture.from(classOf[HirerProduct]).gimme("valid")
        val b: HirerProduct = Fixture.from(classOf[HirerProduct]).gimme("valid")
        a != b
    }

}
