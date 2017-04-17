package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests

class PhysicalPersonDetailTest extends SpockApplicationTests {

    def 'should be equals'() {
        given:
        PhysicalPersonDetail a = Fixture.from(PhysicalPersonDetail.class).gimme("physical-person")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'() {
        List list = Fixture.from(PhysicalPersonDetail.class).gimme(2, "physical-person")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals

    }
}
