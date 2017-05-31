package br.com.unopay.api.uaa.controller

import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserTypeControllerTest extends AuthServerApplicationTests {


    void 'when find all should return'() {
        given:
        String accessToken = getClientAccessToken()

        when:
        def result = this.mvc.perform(
                get("/user-types?access_token={access_token}&page=1&size=2",accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', hasSize(4)))
                .andExpect(jsonPath('$.items[0].name', is(notNullValue())))
                .andExpect(jsonPath('$.items[0].description', is(notNullValue())))
    }
}
