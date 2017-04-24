package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class ContractTest extends FixtureApplicationTest {

    def 'should update me'(){
        given:
        Contract a = Fixture.from(Contract.class).gimme("valid")
        Contract b = Fixture.from(Contract.class).gimme("valid")
        b.product.id = '65545'
        b.hirer.id = '65545'
        b.contractor.id = '65545'
        when:
         a.updateMe(b)

        then:
        a.code == b.code
        a.name == b.name
    }

    def 'should be equals'(){
        given:
        Contract a = Fixture.from(Contract.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(Contract.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}