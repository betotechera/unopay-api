package br.com.unopay.api.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.credit.model.CreditPaymentAccount
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CreditPaymentAccountControllerTest extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator

    void 'known credit payment account should be found'() {
        given:
        String accessToken = getUserAccessToken()
        CreditPaymentAccount credit = Fixture.from(CreditPaymentAccount.class).uses(jpaProcessor).gimme("valid")
        def id = credit.id

        when:
        def result = this.mvc.perform(
                get("/hirers/credit-payment-accounts/{id}?access_token={access_token}", id, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.value', is(notNullValue())))
    }

    void 'known credit payment account should be found when find all'() {
        given:
        String accessToken = getUserAccessToken()
        CreditPaymentAccount credit = Fixture.from(CreditPaymentAccount.class).uses(jpaProcessor).gimme("valid")
        when:
        def result = this.mvc.perform(
                get("/hirers/{document}/credit-payment-accounts?access_token={access_token}",
                        credit.hirerDocument, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].value', is(notNullValue())))
    }
}
