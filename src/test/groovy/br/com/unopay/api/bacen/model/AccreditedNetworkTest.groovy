package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class AccreditedNetworkTest extends FixtureApplicationTest {


    def "should return error if merchantDiscountRate is not in range"() {
        given:
        AccreditedNetwork accreditedNetwork = Fixture.from(AccreditedNetwork.class).gimme("valid")
        accreditedNetwork.merchantDiscountRate = 1.1D

        when:
        accreditedNetwork.validate()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INVALID_MERCHANT_DISCOUNT_RATE_RANGE'
    }

    def "should return error if merchantDiscountRate is not positve"() {
        given:
        AccreditedNetwork accreditedNetwork = Fixture.from(AccreditedNetwork.class).gimme("valid")
        accreditedNetwork.merchantDiscountRate = -0.1D

        when:
        accreditedNetwork.validate()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INVALID_MERCHANT_DISCOUNT_RATE_RANGE'
    }

    def "should return error if minimumDepositValue is negative"() {
        given:
        AccreditedNetwork accreditedNetwork = Fixture.from(AccreditedNetwork.class).gimme("valid")
        accreditedNetwork.checkout.minimumDepositValue = -1000D

        when:
        accreditedNetwork.validate()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INVALID_MINIMUM_DEPOSIT_VALUE'
    }

    def 'should be equals'(){
        given:
        AccreditedNetwork a = Fixture.from(AccreditedNetwork.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        AccreditedNetwork a = Fixture.from(AccreditedNetwork.class).gimme("valid")
        AccreditedNetwork b = Fixture.from(AccreditedNetwork.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals
    }
}
