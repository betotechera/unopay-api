package br.com.unopay.api.bacen.controller

import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class BankControllerTest extends AuthServerApplicationTests {


    void 'known banks should be returned'() {
        given:
        String accessToken = getClientAccessToken()

        when:
        def result = this.mvc.perform(get('/banks?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].name', is(notNullValue())))
    }
}
