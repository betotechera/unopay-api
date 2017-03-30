package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.BankAccount
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class BankAccountControllerTest extends AuthServerApplicationTests {

    void 'valid issuer should be created'() {
        given:
        String accessToken = getClientAccessToken()
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")

        when:
        def result = this.mvc.perform(post('/bankAccounts?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(account)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known issuer should be updated'() {
        given:
        String accessToken = getClientAccessToken()
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        def mvcResult = this.mvc.perform(post('/bankAccounts?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(account))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put('/bankAccounts/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(account.with { id= extractId(location);  agency = '56456'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known issuer should be deleted'() {
        given:
        String accessToken = getClientAccessToken()
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        def mvcResult = this.mvc.perform(post('/bankAccounts?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(account))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete('/bankAccounts/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known issuers should be found'() {
        given:
        String accessToken = getClientAccessToken()
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        def mvcResult = this.mvc.perform(post('/bankAccounts?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(account)))
                .andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(get('/bankAccounts/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.agency', is(notNullValue())))
    }

    private String extractId(String location) {
        location.replaceAll('/bankAccounts/', "")
    }

    private String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }
}
