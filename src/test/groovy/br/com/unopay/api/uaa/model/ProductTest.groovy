package br.com.unopay.api.uaa.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.model.Product

class ProductTest  extends FixtureApplicationTest {

    def 'should be equals'(){
        given:
        Product a = Fixture.from(Product.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(Product.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}