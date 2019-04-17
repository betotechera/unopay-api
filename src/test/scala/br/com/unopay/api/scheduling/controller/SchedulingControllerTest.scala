package br.com.unopay.api.scheduling.controller

import java.util.UUID

import br.com.unopay.api.ControllerTest
import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.service.SchedulingService
import org.hamcrest.Matchers
import org.mockito
import org.mockito.Mockito.when
import org.scalatest.Suite
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders._
import org.springframework.test.web.servlet.result.MockMvcResultMatchers._

@WebMvcTest(value = Array(classOf[SchedulingController]))
class SchedulingControllerTest extends ControllerTest { this: Suite =>

    @MockBean
    var mockSchedulingService: SchedulingService = _

    private val SCHEDULING_URI = "/schedules"

    private val ROLE_DEFAULT_REQUIRED = "USER"

    it should "create a Scheduling" in {
        val scheduling = new Scheduling
        scheduling.id = UUID.randomUUID().toString

        when(mockSchedulingService.create(mockito.Matchers.any())).thenReturn(scheduling)

        val result = this.mockMvc.perform(post(SCHEDULING_URI)
            .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED, "MANAGE_SCHEDULING"))
            .contentType(MediaType.APPLICATION_JSON).content(toJson(scheduling)))

        result
            .andExpect(status()
                .isCreated)
            .andExpect(header()
                .string("Location", Matchers.containsString(SCHEDULING_URI + "/" + scheduling.id)))

    }

    it should "not authorize create a Scheduling without role" in {
        val scheduling = new Scheduling

        val result = this.mockMvc.perform(post(SCHEDULING_URI)
                .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED))
                .contentType(MediaType.APPLICATION_JSON).content(toJson(scheduling)))

        result.andExpect(status().isForbidden)

    }
}