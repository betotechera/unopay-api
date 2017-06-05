package br.com.unopay.api.uaa.controller

import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthorityControllerTest extends AuthServerApplicationTests {

    void testGetAuthorities() {
        given:
        String accessToken = clientCredentialsAccessToken()

        when:
        def result = this.mvc.perform(
                get("/authorities?access_token={access_token}", accessToken))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$..[0].name', is(notNullValue())))
                .andExpect(jsonPath('$..[0].description', is(notNullValue())))

    }


}