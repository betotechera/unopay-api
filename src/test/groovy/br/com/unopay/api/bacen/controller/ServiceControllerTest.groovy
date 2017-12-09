package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.Service
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ServiceControllerTest extends AuthServerApplicationTests {

    void 'valid service should be created'() {
        given:
        String accessToken = getUserAccessToken()
        Service service = Fixture.from(Service.class).gimme("valid")

        when:
        def result = this.mvc.perform(post('/services?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(service)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known service should be updated'() {
        given:
        String accessToken = getUserAccessToken()
        Service service = Fixture.from(Service.class).gimme("valid")
        def mvcResult = this.mvc.perform(post('/services?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(service))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put('/services/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(service.with { id= extractId(location); name = '56456'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known service should be deleted'() {
        given:
        String accessToken = getUserAccessToken()
        Service service = Fixture.from(Service.class).gimme("valid")
        def mvcResult = this.mvc.perform(post('/services?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(service))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete('/services/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known services should be found'() {
        given:
        String accessToken = getUserAccessToken()
        Service service = Fixture.from(Service.class).gimme("valid")
        def mvcResult = this.mvc.perform(post('/services?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(service)))
                .andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(get('/services/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.name', is(notNullValue())))
    }

    private String extractId(String location) {
        location.replaceAll('/services/', "")
    }

}