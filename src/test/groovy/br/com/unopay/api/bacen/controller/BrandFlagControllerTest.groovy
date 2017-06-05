package br.com.unopay.api.bacen.controller

import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class BrandFlagControllerTest extends AuthServerApplicationTests {

    void 'known brand flags should be returned'() {
        given:
        String accessToken = getClientAccessToken()

        when:
        def result = this.mvc.perform(get('/brand-flags?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].name', is(notNullValue())))
    }
}
