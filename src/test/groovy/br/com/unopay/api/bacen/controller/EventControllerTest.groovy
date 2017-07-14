package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.Event
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class EventControllerTest extends AuthServerApplicationTests {

    void 'valid event should be created'() {
        given:
        String accessToken = getUserAccessToken()
        Event event = Fixture.from(Event.class).gimme("valid")

        when:
        def result = this.mvc.perform(post('/events?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(event)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known event should be updated'() {
        given:
        String accessToken = getUserAccessToken()
        Event event = Fixture.from(Event.class).gimme("valid")
        def mvcResult = this.mvc.perform(post('/events?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(event))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put('/events/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(event.with { id= extractId(location);  name = '56456'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known event should be deleted'() {
        given:
        String accessToken = getUserAccessToken()
        Event event = Fixture.from(Event.class).gimme("valid")
        def mvcResult = this.mvc.perform(post('/events?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(event))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete('/events/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known events should be found'() {
        given:
        String accessToken = getUserAccessToken()
        Event event = Fixture.from(Event.class).gimme("valid")
        def mvcResult = this.mvc.perform(post('/events?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(event)))
                .andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(get('/events/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.name', is(notNullValue())))
    }

    private String extractId(String location) {
        location.replaceAll('/events/', "")
    }

}