package br.com.unopay.api.controller

import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.ServiceAuthorize
import br.com.unopay.api.service.ServiceAuthorizeService
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ServiceAuthorizeControllerTest extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator

    @Autowired
    ServiceAuthorizeService service

    void 'valid service authorize credit should be created'() {
        given:
        String accessToken = getUserAccessToken()
        ServiceAuthorize authorize = fixtureCreator.createServiceAuthorize()

        when:
        def result = this.mvc.perform(post('/service-authorizations/?access_token={access_token}',accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(authorize.with { authorizeEvents.find().id = null; it })))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known contracts should be found'() {
        given:
        String accessToken = getUserAccessToken()
        ServiceAuthorize authorize = fixtureCreator.createServiceAuthorize()

        def mvcResult = this.mvc.perform(post('/service-authorizations?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(authorize.with { authorizeEvents.find().id = null; it })))
                .andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(get('/service-authorizations/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.id', is(notNullValue())))
    }

    private String extractId(String location) {
        location.replaceAll('/service-authorizations/', "")
    }

}
