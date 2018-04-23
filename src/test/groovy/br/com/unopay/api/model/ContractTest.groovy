package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import groovy.time.TimeCategory

class ContractTest extends FixtureApplicationTest {


    void setup(){
        Integer.mixin(TimeCategory)
    }

    def 'should create contract from product'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")

        when:
        def contract = new Contract(product)

        then:
        contract.product == product
    }

    def 'when create from product should define credit insertion type'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")

        when:
        def contract = new Contract(product)

        then:
        contract.creditInsertionTypes.contains(product.creditInsertionTypes.find())
    }

    def 'when create from product should define code insertion type'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")

        when:
        def contract = new Contract(product)

        then:
        contract.code != null
    }

    def 'when create from product should define name from product name'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")

        when:
        def contract = new Contract(product)

        then:
        contract.name == product.name
    }

    def 'when create from product should define service types from product'(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")

        when:
        def contract = new Contract(product)

        then:
        contract.serviceTypes.any { it in product.serviceTypes }
    }

    def 'when create from product should define DIGITAL_WALLET payment instrument type '(){
        given:
        Product product = Fixture.from(Product.class).gimme("valid")

        when:
        def contract = new Contract(product)

        then:
        contract.paymentInstrumentType == PaymentInstrumentType.DIGITAL_WALLET
    }


    void 'should be in progress'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid", new Rule(){{
            add("begin", instant("1 day ago"))
            add("end", instant("1 day from now"))
        }})

        when:
        def inProgress = contract.inProgress()

        then:
        inProgress
    }

    void 'contract without period should be in progress'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid", new Rule(){{
            add("begin", null)
            add("end", null)
        }})

        when:
        def inProgress = contract.inProgress()

        then:
        inProgress
    }

    void 'should not be in progress'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid", new Rule(){{
            add("begin", instant("2 days from now"))
            add("end", instant("3 days from now"))
        }})

        when:
        def inProgress = contract.inProgress()

        then:
        !inProgress
    }

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
        a.situation == b.situation
        a.issueInvoice == b.issueInvoice
    }

    def 'given a contractor with another contract when validate contractor should return error'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")
        contractor.with {
            id = '55566677788'
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
            id = '55566677788'
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