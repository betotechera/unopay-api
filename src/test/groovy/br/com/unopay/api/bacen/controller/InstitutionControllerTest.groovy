package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.Institution
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class InstitutionControllerTest extends AuthServerApplicationTests {
    private static final String INSTITUTION_ENDPOINT = '/institutions?access_token={access_token}'
    private static final String INSTITUTION_ID_ENDPOINT = '/institutions/{id}?access_token={access_token}'


    void 'should create institution'() {
        given:
            String accessToken = getClientAccessToken()
        when:
            def result = this.mvc.perform(postInstitution(accessToken, getInstitution()))
        then:
            result.andExpect(status().isCreated()).andExpect(header().string("Location", is(notNullValue())))
    }

    MockHttpServletRequestBuilder postInstitution(String accessToken, Institution institution) {
        post(INSTITUTION_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON).content(toJson(institution))
    }

    void 'known institution should be deleted'() {
        given:
        String accessToken = getClientAccessToken()
        def mvcResult = this.mvc.perform(postInstitution(accessToken, getInstitution())).andReturn()
        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc
                .perform(delete(INSTITUTION_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known institution should be updated'() {
        given:
        String accessToken = getClientAccessToken()
        def mvcResult = this.mvc.perform(postInstitution(accessToken, getInstitution())).andReturn()
        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put(INSTITUTION_ID_ENDPOINT,id, accessToken)
                .content(toJson(institution.with { id= extractId(location);person.name = 'updated';it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    String extractId(String location) {
        location.replaceAll('/institutions/', "")
    }

    String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }

    void 'known institution should be found'() {
        given:
            String accessToken = getClientAccessToken()
            Institution institution = getInstitution()
            def mvcResult = this.mvc.perform(postInstitution(accessToken, institution)).andReturn()
            def location = getLocationHeader(mvcResult)
            def id = extractId(location)
        when:
            def result = this.mvc
                    .perform(get(INSTITUTION_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                    .jsonPath('$.person.name', is(equalTo(institution.person.name))))
                .andExpect(MockMvcResultMatchers
                    .jsonPath('$.person.document.number', is(equalTo(institution.person.document.number))))
    }

    void 'known institution should be found when find all'() {
        given:
            String accessToken = getClientAccessToken()
            this.mvc.perform(postInstitution(accessToken, getInstitution()))

            this.mvc.perform(post(INSTITUTION_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(institution.with {
                person.id = null; person.name = 'temp';person.document.number = '1234576777';it
            })))
        when:
            def result = this.mvc
                    .perform(get("$INSTITUTION_ENDPOINT",accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(greaterThan(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].person', is(notNullValue())))
    }

    Institution getInstitution() {
        Fixture.from(Institution.class).gimme("valid")
    }
}
