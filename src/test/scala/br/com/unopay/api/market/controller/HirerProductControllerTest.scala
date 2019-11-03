package br.com.unopay.api.market.controller

import br.com.unopay.api.AuthServerApplicationTests
import org.hamcrest.Matchers.notNullValue
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.{delete, get, post}
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.{jsonPath, status}

class HirerProductControllerTest  extends AuthServerApplicationTests {


    it should "be created" in {
        val accessToken = getUserAccessToken()
        val hirerProduct = fixtureCreator.validHirerProduct()
        val result = this.mvc.perform(post("/hirer-products?access_token={access_token}", accessToken)
          .contentType(MediaType.APPLICATION_JSON).content(toJson(hirerProduct)))

        result.andExpect(status().isCreated).andExpect(jsonPath("$.product", notNullValue()))
    }

    "known hirer product" should "be found" in {
        val accessToken = getUserAccessToken()
        val id = fixtureCreator.validHirerProduct(jpaProcessor).id
        val result = this.mvc.perform(get("/hirer-products/{id}?access_token={access_token}", id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        result.andExpect(status().isOk).andExpect(jsonPath("$.product", notNullValue()))
    }

    "all hirer product" should "be found" in {
        val accessToken = getUserAccessToken()
        fixtureCreator.validHirerProduct(jpaProcessor)
        val result = this.mvc.perform(get("/hirer-products?access_token={access_token}", accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        result.andExpect(status().isOk).andExpect(jsonPath("$.items[0].id", notNullValue()))
    }

    "known hirer product" should "be found by filter" in {
        val accessToken = getUserAccessToken()
        val document = fixtureCreator.validHirerProduct(jpaProcessor).hirer.getDocumentNumber
        val result = this.mvc.perform(get("/hirer-products?hirerDocument={document}&access_token={access_token}", document, accessToken)
                .contentType(MediaType.APPLICATION_JSON))


        result.andExpect(status().isOk).andExpect(jsonPath("$.items[0].id", notNullValue()))
    }

    "known hirer product" should "be deleted" in {
        val accessToken = getUserAccessToken()
        val id = fixtureCreator.validHirerProduct(jpaProcessor).id
        val result = this.mvc.perform(delete("/hirer-products/{id}?access_token={access_token}", id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        result.andExpect(status().isOk)
    }

}