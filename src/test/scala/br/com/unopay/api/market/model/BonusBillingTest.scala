package br.com.unopay.api.market.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.ScalaApplicationTest

class BonusBillingTest extends ScalaApplicationTest {

    it should "be equal" in {
        val a: BonusBilling = Fixture.from(classOf[BonusBilling]).gimme("valid")
        a == a
    }

    it should "not be equal" in {
        val a: BonusBilling = Fixture.from(classOf[BonusBilling]).gimme("valid")
        val b: BonusBilling = Fixture.from(classOf[BonusBilling]).gimme("valid")
        a != b
    }
}
