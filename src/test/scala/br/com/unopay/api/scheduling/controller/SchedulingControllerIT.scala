package br.com.unopay.api.scheduling.controller

import java.util.Arrays.asList
import java.util.UUID

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.AuthServerApplicationTests
import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.model.filter.SchedulingFilter
import org.hamcrest.Matchers.{containsString, notNullValue}
import org.mockito
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.Suite
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
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

        val actualScheduling: Scheduling = fixtureCreator.createScheduling()

        val otherScheduling = fixtureCreator.createSchedulingToPersist()

        val result = this.mvc.perform(put(SCHEDULING_URI + "/{id}", actualScheduling.id)
                .param("access_token", accessToken)
                .contentType(MediaType.APPLICATION_JSON).content(toJson(otherScheduling)))

        result.andExpect(status().isNoContent)
    }

    it should "filter Schedules" in {
        val accessToken = getUserAccessToken()
        val actualScheduling: Scheduling = fixtureCreator.createScheduling()

        val result = this.mvc.perform(get(SCHEDULING_URI+ "?token={token}", actualScheduling.token)
                .param("access_token", accessToken))

        result.andExpect(status().isOk)
                .andExpect(jsonPath("$.items[0].token", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
    }

    it should "find a Scheduling by id" in {
        val accessToken = getUserAccessToken()
        val actualScheduling: Scheduling = fixtureCreator.createScheduling()

        val result = this.mvc.perform(get(SCHEDULING_URI+ "/{id}", actualScheduling.id)
                .param("access_token", accessToken))

        result.andExpect(status().isOk)
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.id", notNullValue()))

    }

}