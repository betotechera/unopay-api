package br.com.unopay.api.market.controller

import br.com.unopay.api.AuthServerApplicationTests
import org.hamcrest.Matchers.notNullValue
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.{delete, get, put}
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.{jsonPath, status}

class BonusBillingControllerTest  extends AuthServerApplicationTests {

    "known bonusBilling" should "be found" in {
        val accessToken = getUserAccessToken()
        val id = fixtureCreator.createPersistedBonusBilling().id
        val result = this.mvc.perform(get("/bonus-billings/{id}?access_token={access_token}", id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        result.andExpect(status().isOk).andExpect(jsonPath("$.total", notNullValue()))
    }

    "all BonusBillings" should "be found" in {
        val accessToken = getUserAccessToken()
        fixtureCreator.createPersistedBonusBilling()
        fixtureCreator.createPersistedBonusBilling()
        val result = this.mvc.perform(get("/bonus-billings?access_token={access_token}", accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        result.andExpect(status().isOk).andExpect(jsonPath("$.items[0].id", notNullValue()))
    }

    "known BonusBillings" should "be found by filter" in {
        val accessToken = getUserAccessToken()
        val document = fixtureCreator.createPersistedBonusBilling().payer.documentNumber()
        val result = this.mvc.perform(get("/bonus-billings?document={document}&access_token={access_token}", document, accessToken)
                .contentType(MediaType.APPLICATION_JSON))


        result.andExpect(status().isOk).andExpect(jsonPath("$.items[0].id", notNullValue()))
    }

    "known BonusBilling" should "be deleted" in {
        val accessToken = getUserAccessToken()
        val id = fixtureCreator.createPersistedBonusBilling().id
        val result = this.mvc.perform(delete("/bonus-billings/{id}?access_token={access_token}", id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        result.andExpect(status().isNoContent)
    }

    it should "process contractor's bonusses" in {
        val accessToken = getUserAccessToken()
        fixtureCreator.createPersistedContractorBonusForContractor()

        val result = this.mvc.perform(put("/bonus-billings?access_token={access_token}", accessToken)
            .contentType(MediaType.APPLICATION_JSON))

        result.andExpect(status().isNoContent)
    }
}