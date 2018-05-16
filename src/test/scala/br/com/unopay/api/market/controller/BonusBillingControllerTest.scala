package br.com.unopay.api.market.controller

import br.com.unopay.api.AuthServerApplicationTests
import br.com.unopay.api.util.FixtureCreatorScala
import org.hamcrest.Matchers.notNullValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.{delete, get, post}
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.{jsonPath, status}

class BonusBillingControllerTest  extends AuthServerApplicationTests {

    @Autowired
    var fixtureCreator: FixtureCreatorScala = _

     "given valid bonusBilling" should "create it" in {
         val accessToken = getUserAccessToken()
         val bonusBilling = fixtureCreator.createBonusBillingToPersist()

         val result = this.mvc.perform(post("/bonus-billings?access_token={access_token}", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(bonusBilling)))

        result.andExpect(status().isCreated)
    }

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
}