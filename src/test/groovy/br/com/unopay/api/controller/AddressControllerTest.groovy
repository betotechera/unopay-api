package br.com.unopay.api.controller

import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AddressControllerTest extends AuthServerApplicationTests {

    void 'known address should be returned'() {
        given:
        String accessToken = getClientAccessToken()

        when:
        def result = this.mvc.perform(get('/addresses?access_token={access_token}&zipCode=05305011', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.zipCode', is(notNullValue())))
    }
}
