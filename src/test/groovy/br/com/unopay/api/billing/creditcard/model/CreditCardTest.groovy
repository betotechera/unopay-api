package br.com.unopay.api.billing.creditcard.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import spock.lang.Unroll

class CreditCardTest extends FixtureApplicationTest {

    @Unroll
    def 'when creating CreditCard with expiryMonth value "#notANumber" should return error'() {

        given:
        def invalidValue = notANumber
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard", new Rule() {{
                add("expiryMonth", invalidValue)
        }})

        when:
        creditCard.checkExpiryMonth()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_MONTH'

        where:
        _ | notANumber
        _ | null
        _ | ""
        _ | "a"
        _ | "1.0"
        _ | "1,1"
        _ | "1 a"

    }

    def 'when creating CreditCard with expiryMonth value before January should return error'() {

        given:
        String expiryMonth = value
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard", new Rule() {{
            add("expiryMonth", expiryMonth)
        }})

        when:
        creditCard.checkExpiryMonth()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_MONTH'

        where:
        _ | value
        _ | '-1'
        _ | '0'
        _ | '-83183'
        _ | '-781238971239897798789879'

    }

    def 'when creating CreditCard with expiryMonth value after December should return error'() {

        given:
        String expiryMonth = value
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard", new Rule() {{
            add("expiryMonth", expiryMonth)
        }})

        when:
        creditCard.checkExpiryMonth()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_MONTH'

        where:
        _ | value
        _ | '13'
        _ | '20'
        _ | '9812389'
        _ | '+192838919823838'

    }

    @Unroll
    def 'when creating CreditCard with expiryYear value "#notANumber" should return error'() {

        given:
        def invalidValue = notANumber
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard", new Rule() {{
            add("expiryYear", invalidValue)
        }})

        when:
        creditCard.checkExpiryYear()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_YEAR'

        where:
        _ | notANumber
        _ | null
        _ | ""
        _ | "a"
        _ | "1.0"
        _ | "1,1"
        _ | "2018 a"
        _ | "2018" + "a"
        _ | "a2018"

    }

    def 'when creating CreditCard with expiryYear value before current year should return error'() {

        given:
        String expiryYear = value
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard", new Rule() {{
            add("expiryYear", expiryYear)
        }})

        when:
        creditCard.checkExpiryYear()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_YEAR'

        where:
        _ | value
        _ | '999'
        _ | '10'
        _ | '0'
        _ | '-1'
        _ | '2017'

    }

    @Unroll
    def 'when creating CreditCard with expiryYear value after current year plus #suplusLimit should return error'() {

        given:
        def limit = surplusLimit
        String expiryYear = value
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard", new Rule() {{
            add("expiryYear", expiryYear)
        }})

        when:
        creditCard.checkExpiryYear()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_YEAR'

        where:
        surplusLimit | value
        100 | '2300'
        100 | '10000'
        100 | '123123213'

    }

    @Unroll
    'when creating CreditCard with number value "#notANumber" should return error'(){

        given:
        String number = notANumber
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard", new Rule(){{
            add("number", number)
        }})

        when:
        creditCard.checkNumber()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_NUMBER'

        where:
        _ | notANumber
        _ | null
        _ | ""
        _ | "kaowkdoaw"

    }

    @Unroll
    def 'when creating CreditCard with number length smaller than #minimumLength should return error'(){

        given:
        String number = smallLength
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard", new Rule(){{
            add("number", number)
        }})

        when:
        creditCard.checkNumber()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_NUMBER'

        where:
        minimumLength | smallLength
        4 | "406"
        4 | "12"
        4 | "7"

    }

    @Unroll
    def 'when creating CreditCard with cardReference value "#blankOrNull" should return error'() {

        given:
        def invalid = blankOrNull
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard", new Rule(){{
            add("cardReference", invalid)
        }})

        when:
        creditCard.checkCardReference()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_CARD_REFERENCE'

        where:
        _ | blankOrNull
        _ | ""
        _ | null

    }

    @Unroll
    def 'when creating CreditCard with holderName value "#blankOrNull" should return error'() {

        given:
        def invalid = blankOrNull
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard", new Rule(){{
            add("holderName", invalid)
        }})

        when:
        creditCard.checkHolderName()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_HOLDER_NAME'

        where:
        _ | blankOrNull
        _ | ""
        _ | null

    }

}
