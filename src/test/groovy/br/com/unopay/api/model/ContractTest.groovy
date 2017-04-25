package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

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
        a.name == b.name
        a.rntrc == b.rntrc
        a.situation == b.situation
        a.issueInvoice == b.issueInvoice
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

    void 'given contract with begin date after end date it should throw error'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            begin = end + 1
            it }

        when:
        contract.validate()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CONTRACT_END_IS_BEFORE_BEGIN'
    }
}