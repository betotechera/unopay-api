package br.com.unopay.api.scheduling.controller

import br.com.unopay.api.AuthServerApplicationTests
import org.hamcrest.Matchers.containsString
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
}