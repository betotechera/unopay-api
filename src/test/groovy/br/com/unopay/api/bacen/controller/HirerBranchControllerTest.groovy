package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.HirerBranch
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class HirerBranchControllerTest extends AuthServerApplicationTests {
    private static final String HIRER_ENDPOINT = '/hirer-branches?access_token={access_token}'
    private static final String HIRER_ID_ENDPOINT = '/hirer-branches/{id}?access_token={access_token}'

    void 'should create hirerBranch'() {
        given:
            String accessToken = getClientAccessToken()
        when:
            def result = this.mvc.perform(postHirerBranch(accessToken, getHirerBranch()))
        then:
            result.andExpect(status().isCreated()).andExpect(header().string("Location", is(notNullValue())))
    }

    MockHttpServletRequestBuilder postHirerBranch(String accessToken, HirerBranch hirer) {
        post(HIRER_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON).content(toJson(hirer))
    }

    void 'known hirerBranch should be deleted'() {
        given:
        String accessToken = getClientAccessToken()
        def mvcResult = this.mvc.perform(postHirerBranch(accessToken, getHirerBranch())).andReturn()
        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete(HIRER_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    String extractId(String location) {
        location.replaceAll('/hirer-branches/', "")
    }

    String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }

    void 'known hirerBranch should be found'() {
        given:
            String accessToken = getClientAccessToken()
            HirerBranch hirer = getHirerBranch()
            def mvcResult = this.mvc.perform(postHirerBranch(accessToken, hirer)).andReturn()
            def location = getLocationHeader(mvcResult)
            def id = extractId(location)
        when:
            def result = this.mvc.perform(get(HIRER_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.name', is(equalTo(hirer.person.name))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.document.number', is(equalTo(hirer.person.document.number))))
    }

    void 'known hirerBranch should be found when find all'() {
        given:
            String accessToken = getClientAccessToken()
            def hirerBranch = getHirerBranch()
            this.mvc.perform(postHirerBranch(accessToken, hirerBranch))

            this.mvc.perform(post(HIRER_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(hirerBranch.with { person.id = null; person.name = 'temp';person.document.number = '1234576777';it })))
        when:
            def result = this.mvc.perform(get("$HIRER_ENDPOINT",accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(greaterThan(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].person', is(notNullValue())))
    }

    HirerBranch getHirerBranch() {
        Fixture.from(HirerBranch.class).gimme("valid")
    }
}
