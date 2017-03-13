package br.com.unopay.api.uaa

import org.springframework.http.MediaType

import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsEqual.equalTo
import static org.hamcrest.core.IsNull.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PasswordGrantTypeTests extends AuthServerApplicationTests {
    void should_return_access_token() throws Exception {
        when:
        def result = this.mvc.perform(post("/oauth/token")
                .contentType(
                        MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "password")
                .param("client_id", "client")
                .param("client_secret", "secret")
                .param("username", "test@test.com")
                .param("password", "test"))
        then:
        result .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))
                .andExpect(jsonPath('$.token_type', is(equalTo("bearer"))));

    }

    void should_return_unauthorized_error() throws Exception {
        when:
        def result = this.mvc.perform(post("/oauth/token")
                .contentType(
                        MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "password")
                .param("client_id", "client")
                .param("client_secret", "secret")
                .param("username", "wrong")
                .param("password", "wrong"))
        then:
                result.andExpect(status().isBadRequest())
                        .andExpect(jsonPath('$.error_description', is(equalTo("Bad credentials"))))
    }

}

