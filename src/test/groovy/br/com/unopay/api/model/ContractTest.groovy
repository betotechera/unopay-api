package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.bacen.model.Contractor
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

    def 'given a contractor with another contract when validate contractor should return error'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")
        contractor.with {
            person.document.number = '55566677788'
        }

        when:
        contract.validateContractor(contractor)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors?.first()?.logref == 'INVALID_CONTRACTOR'
    }

    def 'given a contractor with another contract when verify contractor belongs to contract should not be'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")
        contractor.with {
            person.document.number = '55566677788'
        }

        when:
        def contains = contract.containsContractor(contractor)

        then:
        !contains
    }

    def 'contractor belongs to contract should be'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")

        when:
        def contains = contract.containsContractor(contract.contractor)

        then:
        contains
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