package br.com.unopay.api.uaa

import com.jayway.jsonpath.JsonPath
import org.springframework.test.web.servlet.MvcResult

import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsEqual.equalTo
import static org.hamcrest.core.IsNull.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CheckTokenTests extends AuthServerApplicationTests {

    void should_return_success_for_valid_access_token() throws Exception {

        when:
        MvcResult token = clientCredentials()
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))
                .andReturn()

        String accessToken = JsonPath.read(
                token.getResponse().getContentAsString(),
                '$.access_token')

        def result = this.mvc.perform(
                get("/oauth/check_token?token={access_token}", accessToken)
                        .header("Authorization", getAuthorizationHeader("client", "secret")))
        then:
                result.andExpect(status().is2xxSuccessful())
                        .andExpect(jsonPath('$.aud', is(notNullValue())))
                        .andExpect(jsonPath('$.authorities', is(notNullValue())))
                        .andExpect(jsonPath('$.client_id', is(equalTo("client"))))
                        .andExpect(jsonPath('$.organization', is(equalTo("1"))))
                        .andReturn()
    }



}