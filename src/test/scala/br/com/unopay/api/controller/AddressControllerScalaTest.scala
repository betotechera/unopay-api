package br.com.unopay.api.controller

import br.com.unopay.api.AuthServerApplicationTests
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.core.Is._
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AddressControllerScalaTest extends AuthServerApplicationTests {

    "known address" should "be returned" in {
        val accessToken = getClientAccessToken()

        val result = this.mvc.perform(get("/addresses?access_token={access_token}&zipCode=05305011", accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        result.andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode", is(notNullValue())))
    }
}
