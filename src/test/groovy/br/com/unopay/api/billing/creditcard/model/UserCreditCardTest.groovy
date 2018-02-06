package br.com.unopay.api.billing.creditcard.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.joda.time.DateTime
import spock.lang.Unroll

class UserCreditCardTest extends FixtureApplicationTest {

    def 'when creating UserCreditCard, expiration date should be set at 0 hour of 1st day of given expiration month and given expiration year'(){

        given:
        String expirationYear = '2118'
        String expirationMonth = '10'
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationYear", expirationYear)
            add("expirationMonth", expirationMonth)
        }})

        when:
        userCreditCard.defineExpirationDate()

        then:
        def difference = timeComparator.compare(
                userCreditCard.getExpirationDate(),
                DateTime.now()
                        .withYear(Integer.parseInt(expirationYear))
                        .withMonthOfYear(Integer.parseInt(expirationMonth))
                        .withDayOfMonth(1)
                        .withTime(0, 0, 0, 0).toDate())
        !difference
    }

    def 'when calling setupMyCreate, UserCreditCard expiration date should be set at 0 hour of 1st day of given expiration month and given expiration year'(){

        given:
        String expirationYear = '2118'
        String expirationMonth = '10'
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationYear", expirationYear)
            add("expirationMonth", expirationMonth)
        }})

        when:
        userCreditCard.setupMyCreate()

        then:
        def difference = timeComparator.compare(
                userCreditCard.getExpirationDate(),
                DateTime.now()
                        .withYear(Integer.parseInt(expirationYear))
                        .withMonthOfYear(Integer.parseInt(expirationMonth))
                        .withDayOfMonth(1)
                        .withTime(0, 0, 0, 0).toDate())
        !difference
    }

    @Unroll
    def 'when creating UserCreditCard with month value "#notAnIntValue" should return error'(){

        given:
        def invalidValue = notAnIntValue
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationMonth", invalidValue)
        }})

        when:
        userCreditCard.validateMonth()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_MONTH'

        where:
        _ | notAnIntValue
        _ | null
        _ | ""
        _ | "a"
        _ | "1.0"
        _ | "1,1"
    }

    def 'when creating UserCreditCard with month value before January should return error'(){

        given:
        String expirationMonth = value
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationMonth", expirationMonth)
        }})

        when:
        userCreditCard.validateMonth()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_MONTH'

        where:
        _ | value
        _ | '-1'
        _ | '0'
        _ | '-83183'
    }

    def 'when creating UserCreditCard with month value after December should return error'(){

        given:
        String expirationMonth = value
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationMonth", expirationMonth)
        }})

        when:
        userCreditCard.validateMonth()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_MONTH'

        where:
        _ | value
        _ | '13'
        _ | '20'
        _ | '9812389'
    }

    @Unroll
    def 'when creating UserCreditCard with year value "#notAnIntValue" should return error'(){

        given:
        def invalidValue = notAnIntValue
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationYear", invalidValue)
        }})

        when:
        userCreditCard.validateYear()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_YEAR'

        where:
        _ | notAnIntValue
        _ | null
        _ | ""
        _ | "a"
        _ | "1.0"
        _ | "1,1"
        _ | "2018 a"
        _ | "2018" + "a"
        _ | "a2018"
    }

    def 'when creating UserCreditCard with year value smaller than 1000 should return error'(){

        given:
        String expirationYear = value
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationYear", expirationYear)
        }})

        when:
        userCreditCard.validateYear()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_YEAR'

        where:
        _ | value
        _ | '999'
        _ | '10'
        _ | '0'
        _ | '-1'
    }

    def 'when creating UserCreditCard with year value greater than 9999 should return error'(){

        given:
        String expirationYear = value
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationYear", expirationYear)
        }})

        when:
        userCreditCard.validateYear()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_YEAR'

        where:
        _ | value
        _ | '10000'
        _ | '123123213'
    }

    def 'when calling validateMe with month value before January should return error'(){

        given:
        String expirationMoth = value
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationMonth", expirationMoth)
        }})

        when:
        userCreditCard.validateMe()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_MONTH'

        where:
        _ | value
        _ | '0'
        _ | '-1'
        _ | '-13788731'
    }

    def 'when calling validateMe with year value smaller than 1000 should return error'(){

        given:
        String expirationYear = value
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationYear", expirationYear)
        }})

        when:
        userCreditCard.validateMe()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_YEAR'

        where:
        _ | value
        _ | '999'
        _ | '0'
        _ | '-1'
        _ | '-18299898'
    }
}
