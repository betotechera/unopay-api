package br.com.unopay.api.billing.remittance.cnab240

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.billing.remittance.model.ItauAccountField
import br.com.unopay.api.billing.remittance.model.RemittancePayee
import static org.apache.commons.lang3.StringUtils.leftPad

class ItauAccountFieldTest extends FixtureApplicationTest{

    def 'given the payee wth the Itau bank code should return the Itau layout'(){
        given:
        def bankCode = bank
        RemittancePayee payee = Fixture.from(RemittancePayee).gimme("valid", new Rule(){{
            add("bankCode", bankCode)
        }})

        when:

        def result = new ItauAccountField(payee).get()

        then:
        result == "0${leftPad(payee.getAgency(), 4, "0")} 000000${leftPad(payee.getAccountNumber(), 6, "0")} ${payee.accountDvLastDigit()}"

        where:
        _ | bank
        _ | 341
        _ | 409
    }

    def 'given the payee wth the Itau bank code should return the Itau layout  the size equals to 20'(){
        given:
        def bankCode = bank
        RemittancePayee payee = Fixture.from(RemittancePayee).gimme("valid", new Rule(){{
            add("bankCode", bankCode)
        }})

        when:

        def result = new ItauAccountField(payee).get()

        then:
        result.size() == 20

        where:
        _ | bank
        _ | 341
        _ | 409
    }

    def 'given the payee wth the other bank code should return the other banks layout'(){
        given:
        def bankCode = bank
        RemittancePayee payee = Fixture.from(RemittancePayee).gimme("valid", new Rule(){{
            add("bankCode", bankCode)
        }})

        when:

        def result = new ItauAccountField(payee).get()

        then:
        result == "${leftPad(payee.getAgency(), 5, "0")} ${leftPad(payee.getAccountNumber(), 12, "0")} ${payee.accountDvLastDigit()}"

        where:
        _ | bank
        _ | 33
        _ | 1
        _ | 237
        _ | 104
    }

    def 'given the payee wth the other bank code should return the other banks layout with the size equals to 20'(){
        given:
        def bankCode = bank
        RemittancePayee payee = Fixture.from(RemittancePayee).gimme("valid", new Rule(){{
            add("bankCode", bankCode)
        }})

        when:

        def result = new ItauAccountField(payee).get()

        then:
        result.size() == 20

        where:
        _ | bank
        _ | 33
        _ | 1
        _ | 237
        _ | 104
    }
}
