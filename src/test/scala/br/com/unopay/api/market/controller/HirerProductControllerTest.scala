package br.com.unopay.api.market.controller

import br.com.unopay.api.AuthServerApplicationTests
import org.hamcrest.Matchers.notNullValue
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.{delete, get}
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.{jsonPath, status}

class HirerProductControllerTest  extends AuthServerApplicationTests {

    "known hirer product" should "be found" in {
        val accessToken = getUserAccessToken()
        val id = fixtureCreator.validHirerProduct.id
        val result = this.mvc.perform(get("/hirer-products/{id}?access_token={access_token}", id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        result.andExpect(status().isOk).andExpect(jsonPath("$.product", notNullValue()))
    }

    "all hirer product" should "be found" in {
        val accessToken = getUserAccessToken()
        fixtureCreator.validHirerProduct
        val result = this.mvc.perform(get("/hirer-products?access_token={access_token}", accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        result.andExpect(status().isOk).andExpect(jsonPath("$.items[0].id", notNullValue()))
    }

    "known hirer product" should "be found by filter" in {
        val accessToken = getUserAccessToken()
        val document = fixtureCreator.validHirerProduct.product.getIssuer.documentNumber()
        val result = this.mvc.perform(get("/hirer-products?issuerDocument={document}&access_token={access_token}", document, accessToken)
                .contentType(MediaType.APPLICATION_JSON))


        result.andExpect(status().isOk).andExpect(jsonPath("$.items[0].id", notNullValue()))
    }

    "known hirer product" should "be deleted" in {
        val accessToken = getUserAccessToken()
        val id = fixtureCreator.validHirerProduct.id
        val result = this.mvc.perform(delete("/hirer-products/{id}?access_token={access_token}", id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        result.andExpect(status().isNoContent)
    }

}