package br.com.unopay.api.scheduling.controller

import br.com.unopay.api.AuthServerApplicationTests
import br.com.unopay.api.scheduling.model.Scheduling
import org.hamcrest.Matchers.{containsString, notNullValue}
import org.scalatest.Suite
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders._
import org.springframework.test.web.servlet.result.MockMvcResultMatchers._

class SchedulingControllerIT extends AuthServerApplicationTests { this: Suite =>

    private val SCHEDULING_URI = "/schedules"

    it should "create a Scheduling" in {
        val accessToken = getUserAccessToken()

        val scheduling = fixtureCreator.createSchedulingToPersist()

        val result = this.mvc.perform(post(SCHEDULING_URI).param("access_token", accessToken)
                .contentType(MediaType.APPLICATION_JSON).content(toJson(scheduling)))

        result
            .andExpect(status()
                .isCreated)
            .andExpect(header()
                .string("Location", containsString(SCHEDULING_URI)))

    }

    it should "update a Scheduling" in {
        val accessToken = getUserAccessToken()

        val actualScheduling: Scheduling = fixtureCreator.createSchedulingPersisted()

        val otherScheduling = fixtureCreator.createSchedulingToPersist()

        val result = this.mvc.perform(put(SCHEDULING_URI + "/{id}", actualScheduling.id)
                .param("access_token", accessToken)
                .contentType(MediaType.APPLICATION_JSON).content(toJson(otherScheduling)))

        result.andExpect(status().isNoContent)
    }

    it should "filter Schedules" in {
        val accessToken = getUserAccessToken()
        val schedulingPersisted: Scheduling = fixtureCreator.createSchedulingPersisted()

        val result = this.mvc.perform(get(SCHEDULING_URI+ "?token={token}", schedulingPersisted.token)
                .param("access_token", accessToken))

        result.andExpect(status().isOk)
                .andExpect(jsonPath("$.items[0].token", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
    }

    it should "find a Scheduling by id" in {
        val accessToken = getUserAccessToken()
        val schedulingPersisted: Scheduling = fixtureCreator.createSchedulingPersisted()

        val result = this.mvc.perform(get(SCHEDULING_URI+ "/{id}", schedulingPersisted.id)
                .param("access_token", accessToken))

        result.andExpect(status().isOk)
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.id", notNullValue()))

    }

    it should "delete a Schedule" in {
        val accessToken = getUserAccessToken()
        val schedulingPersisted: Scheduling = fixtureCreator.createSchedulingPersisted()

        val result = this.mvc.perform(delete(SCHEDULING_URI+ "/{id}", schedulingPersisted.id)
                .param("access_token", accessToken))

        result.andExpect(status().isNoContent)
    }

}
