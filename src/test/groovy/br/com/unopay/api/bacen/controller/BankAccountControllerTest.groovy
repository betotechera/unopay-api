package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.BankAccount
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class BankAccountControllerTest extends AuthServerApplicationTests {

    void 'valid bank account should be created'() {
        given:
        String accessToken = getUserAccessToken()
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")

        when:
        def result = this.mvc.perform(post('/bankAccounts?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(account)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known bank account should be updated'() {
        given:
        String accessToken = getUserAccessToken()
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

    void 'known bank account should be deleted'() {
        given:
        String accessToken = getUserAccessToken()
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

    void 'known bank accounts should be found'() {
        given:
        String accessToken = getUserAccessToken()
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


}
