package br.com.unopay.api.credit.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.util.Rounder
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class InstrumentBalanceTest  extends FixtureApplicationTest {

    def 'given a balance without value when add value should initialize balance with value'(){
        given:
        def balance = new InstrumentBalance()
        BigDecimal value = 50.66
        when:
        balance.add(value)

        then:
        balance.value == value
    }

    def 'given a balance with value when add value should increment balance with value'(){
        given:
        def balance = new InstrumentBalance()
        BigDecimal initialValue = 88.99
        BigDecimal value = 50.66
        balance.value = initialValue
        when:
        balance.add(value)

        then:
        balance.value == Rounder.round(value + initialValue)
    }


    def 'when subtract value less than or equals balance should be subtracted'(){
        given:
        def balance = new InstrumentBalance()
        balance.add(20.0)

        when:
        balance.subtract(value)

        then:
        balance.value == expected

        where:
        value || expected
        20.0  || 0.0
        15.0  || 5.0
        0.1   || 19.9
    }

    def 'given a value greater than  balance should return error when subtract'(){
        given:
        def balance = new InstrumentBalance()
        balance.add(20.0)

        when:
        balance.subtract(20.01)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'BALANCE_LESS_THAN_REQUIRED'
    }

    def 'when try subtract without value should return error'(){
        given:
        def balance = new InstrumentBalance()
        balance.add(20.0)

        when:
        balance.subtract(null)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INVALID_VALUE'
    }

    def 'when try add without value should return error'(){
        given:
        def balance = new InstrumentBalance()

        when:
        balance.add(null)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INVALID_VALUE'
    }

    def 'given balance without value should return error when subtract'(){
        given:
        def balance = new InstrumentBalance()

        when:
        balance.subtract(20.01)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'BALANCE_LESS_THAN_REQUIRED'
    }

    def 'should be equals'(){
        given:
        InstrumentBalance a = Fixture.from(InstrumentBalance.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(InstrumentBalance.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }

}
