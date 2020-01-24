package br.com.unopay.api.scheduling.controller

import java.util.Arrays.asList
import java.util.UUID

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.ControllerTest
import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.model.filter.SchedulingFilter
import br.com.unopay.api.scheduling.service.SchedulingService
import br.com.unopay.api.util.TokenFactory
import br.com.unopay.bootcommons.exception.UnovationExceptions
import org.hamcrest.Matchers
import org.hamcrest.Matchers.notNullValue
import org.mockito
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import org.scalatest.Suite
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
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

    override def beforeEach(): Unit = {
        super.beforeEach()
        reset(mockSchedulingService)
    }

    it should "create a Scheduling" in {
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid")
        scheduling.id = UUID.randomUUID().toString

        when(mockSchedulingService.create(any())).thenReturn(scheduling)

        val result = this.mockMvc.perform(post(SCHEDULING_URI)
                .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED, "MANAGE_SCHEDULING"))
                .contentType(MediaType.APPLICATION_JSON).content(toJson(scheduling)))

        result
                .andExpect(status()
                .isCreated)
                .andExpect(header()
                .string("Location", Matchers.containsString(SCHEDULING_URI + "/" + scheduling.id)))

        val captor = ArgumentCaptor.forClass(classOf[Scheduling])
        verify(mockSchedulingService).create(captor.capture())
        assert(scheduling.token.equals(captor.getValue.token))
    }

    it should "don`t create a Scheduling invalid" in {
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("invalid")

        val result = this.mockMvc.perform(post(SCHEDULING_URI)
                .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED, "MANAGE_SCHEDULING"))
                .contentType(MediaType.APPLICATION_JSON).content(toJson(scheduling)))

        result.andExpect(status().isUnprocessableEntity)
    }

    it should "not authorize create a Scheduling without role" in {
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid")

        val result = this.mockMvc.perform(post(SCHEDULING_URI)
                .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED))
                .contentType(MediaType.APPLICATION_JSON).content(toJson(scheduling)))


        result.andExpect(status().isForbidden)

        verify(mockSchedulingService, never()).create(any())

    }

    it should "update a Scheduling" in {
        val id = UUID.randomUUID().toString
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid")

        val result = this.mockMvc.perform(put(SCHEDULING_URI + "/{id}", id)
                .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED, "MANAGE_SCHEDULING"))
                .contentType(MediaType.APPLICATION_JSON).content(toJson(scheduling)))

        val captor = ArgumentCaptor.forClass(classOf[Scheduling])

        verify(mockSchedulingService).update(mockito.Matchers.eq(id), captor.capture())

        result.andExpect(status().isNoContent)
        assert(captor.getValue.token.equals(scheduling.token))
    }

    it should "don`t update a Scheduling invalid" in {
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("invalid")
        scheduling.id = UUID.randomUUID().toString

        val result = this.mockMvc.perform(put(SCHEDULING_URI + "/{id}", scheduling.id)
                .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED, "MANAGE_SCHEDULING"))
                .contentType(MediaType.APPLICATION_JSON).content(toJson(scheduling)))

        result.andExpect(status().isUnprocessableEntity)
    }

    it should "not authorize update a Scheduling without role" in {
        val id = UUID.randomUUID().toString
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid")

        val result = this.mockMvc.perform(put(SCHEDULING_URI + "/{id}", id)
                .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED))
                .contentType(MediaType.APPLICATION_JSON).content(toJson(scheduling)))

        verify(mockSchedulingService, never()).update(any(), any())

        result.andExpect(status().isForbidden)
    }

    it should "filter Schedules" in {
        val token = TokenFactory.generateToken()
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid")

        when(mockSchedulingService.findAll(any(), any())).thenReturn(new PageImpl[Scheduling](asList(scheduling)))

        val result = this.mockMvc.perform(get(SCHEDULING_URI+ "?token={token}", token)
                .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED, "LIST_SCHEDULING")))

        val captor = ArgumentCaptor.forClass(classOf[SchedulingFilter])
        verify(mockSchedulingService).findAll(captor.capture(), any())

        assert(captor.getValue.token == token)
        result.andExpect(status().isOk)
                .andExpect(jsonPath("$.items[0].token", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
    }

    it should "not authorize filter Schedules" in {
        val token = TokenFactory.generateToken()

        val result = this.mockMvc.perform(get(SCHEDULING_URI+ "?token={token}", token)
                .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED)))

        result.andExpect(status().isForbidden)

        verify(mockSchedulingService, never()).findAll(any(), any())
    }

    it should "find a Scheduling by id" in {
        val id = UUID.randomUUID().toString
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid")
        scheduling.id = id

        when(mockSchedulingService.findById(id)).thenReturn(scheduling)

        val result = this.mockMvc.perform(get(SCHEDULING_URI+ "/{id}", id)
                .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED, "LIST_SCHEDULING")))


        result.andExpect(status().isOk)
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.id", notNullValue()))

        verify(mockSchedulingService).findById(id)
    }

    it should "not authorize find a Scheduling by id" in {
        val id = UUID.randomUUID().toString

        val result = this.mockMvc.perform(get(SCHEDULING_URI+ "/{id}", id)
                .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED)))

        result.andExpect(status().isForbidden)
        verify(mockSchedulingService, never()).findById(any())
    }

    it should "not found when Scheduling not exists" in {
        val id = UUID.randomUUID().toString

        when(mockSchedulingService.findById(id)).thenThrow(UnovationExceptions.notFound())

        val result = this.mockMvc.perform(get(SCHEDULING_URI+ "/{id}", id)
                .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED, "LIST_SCHEDULING")))

        result.andExpect(status().isNotFound)

        verify(mockSchedulingService).findById(id)
    }

    it should "delete a Schedule" in {
        val id = UUID.randomUUID().toString

        val result = this.mockMvc.perform(delete(SCHEDULING_URI+ "/{id}", id)
                .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED, "MANAGE_SCHEDULING")))

        result.andExpect(status().isNoContent)

        verify(mockSchedulingService).deleteById(id)
    }

    it should "not authorize delete a Scheduling by id" in {
        val id = UUID.randomUUID().toString

        val result = this.mockMvc.perform(delete(SCHEDULING_URI+ "/{id}", id)
                .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED)))

        result.andExpect(status().isForbidden)
        verify(mockSchedulingService, never()).deleteById(any())
    }

    it should "cancel a Schedule" in {
        val id = UUID.randomUUID().toString

        val result = this.mockMvc.perform(delete(SCHEDULING_URI+ "/{id}/cancel", id)
          .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED, "LIST_SCHEDULING")))

        result.andExpect(status().isNoContent)

        verify(mockSchedulingService).cancelById(id)
    }

    it should "not authorize cancel a Scheduling by id" in {
        val id = UUID.randomUUID().toString

        val result = this.mockMvc.perform(delete(SCHEDULING_URI+ "/{id}/cancel", id)
          .`with`(user("user").roles(ROLE_DEFAULT_REQUIRED)))

        result.andExpect(status().isForbidden)
        verify(mockSchedulingService, never()).cancelById(any())
    }
}