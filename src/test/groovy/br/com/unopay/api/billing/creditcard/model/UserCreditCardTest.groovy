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

    def 'when creating UserCreditCard without an int month value should return error'(){

        given:
        def invalidValue = value
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationMonth", invalidValue)
        }})

        when:
        userCreditCard.validateMonth()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_MONTH'

        where:
        _ | value
        _ | "a"
        _ | "1.0"
        _ | "1,1"
    }

    @Unroll
    def 'when creating UserCreditCard with month value #value should return error'(){

        given:
        def invalidValue = value
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationMonth", invalidValue)
        }})

        when:
        userCreditCard.validateMonth()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_MONTH'

        where:
        _ | value
        _ | null
        _ | ""
    }

    def 'when creating UserCreditCard with month value smaller than 1 should return error'(){

        given:
        String expirationMonth = '0'
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationMonth", expirationMonth)
        }})

        when:
        userCreditCard.validateMonth()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_MONTH'
    }

    def 'when creating UserCreditCard with month value greater than 12 should return error'(){

        given:
        String expirationMonth = '13'
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationMonth", expirationMonth)
        }})

        when:
        userCreditCard.validateMonth()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_MONTH'
    }

    def 'when creating UserCreditCard without an int year value should return error'(){

        given:
        def invalidValue = value
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationYear", invalidValue)
        }})

        when:
        userCreditCard.validateYear()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_YEAR'

        where:
        _ | value
        _ | "a"
        _ | "1.0"
        _ | "1,1"
    }

    @Unroll
    def 'when creating UserCreditCard with year value #value should return error'(){

        given:
        def invalidValue = value
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationYear", invalidValue)
        }})

        when:
        userCreditCard.validateYear()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_YEAR'

        where:
        _ | value
        _ | null
        _ | ""
    }

    def 'when creating UserCreditCard with year value smaller than 1000 should return error'(){

        given:
        String expirationYear = '999'
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationYear", expirationYear)
        }})

        when:
        userCreditCard.validateYear()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_YEAR'
    }

    def 'when creating UserCreditCard with year value greater than 9999 should return error'(){

        given:
        String expirationYear = '10000'
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationYear", expirationYear)
        }})

        when:
        userCreditCard.validateYear()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_YEAR'
    }

    def 'when calling validateMe with month value smaller than 1 should return error'(){

        given:
        String expirationMoth = '0'
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationMonth", expirationMoth)
        }})

        when:
        userCreditCard.validateMe()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_MONTH'
    }

    def 'when calling validateMe with year value smaller than 1000 should return error'(){

        given:
        String expirationYear = '999'
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("expirationYear", expirationYear)
        }})

        when:
        userCreditCard.validateMe()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_YEAR'
    }
}
