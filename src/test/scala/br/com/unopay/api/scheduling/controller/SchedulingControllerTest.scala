package br.com.unopay.api.scheduling.controller

import java.util.UUID

import br.com.unopay.api.ControllerTest
import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.service.SchedulingService
import org.hamcrest.Matchers
import org.junit.runner.RunWith
import org.mockito
import org.mockito.Mockito
import org.mockito.Mockito._
import org.mockito.Mockito.when
import org.scalatest.Suite
import org.scalatest.junit.JUnitRunner
import org.scalatest.mockito._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders._
import org.springframework.test.web.servlet.result.MockMvcResultMatchers._

@WebMvcTest(value = Array(classOf[SchedulingController]))
class SchedulingControllerTest extends ControllerTest { this: Suite =>

    @MockBean
    var mockSchedulingService: SchedulingService = _

    it should "create a Scheduling" in {
        val scheduling = new Scheduling
        scheduling.id = UUID.randomUUID().toString

        when(mockSchedulingService.create(mockito.Matchers.any())).thenReturn(scheduling)

        val result = this.mockMvc.perform(post("/schedules")
            .`with`(SecurityMockMvcRequestPostProcessors.user("user").roles("USER", "MANAGE_SCHEDULES"))
            .contentType(MediaType.APPLICATION_JSON).content(toJson(scheduling)))

        result
            .andExpect(status()
                .isCreated)
            .andExpect(header()
                .string("Location", Matchers.containsString("/schedules/" + scheduling.id)))

    }
}