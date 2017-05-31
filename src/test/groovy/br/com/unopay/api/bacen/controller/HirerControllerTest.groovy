package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.repository.PaymentRuleGroupRepository
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import org.springframework.beans.factory.annotation.Autowired
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

class HirerControllerTest extends AuthServerApplicationTests {
    private static final String HIRER_ENDPOINT = '/hirers?access_token={access_token}'
    private static final String HIRER_ID_ENDPOINT = '/hirers/{id}?access_token={access_token}'

    @Autowired
    private PaymentRuleGroupRepository repository

    
    void 'should create hirer'() {
        given:
            String accessToken = getClientAccessToken()
        when:
            def result = this.mvc.perform(postHirer(accessToken, getHirer()))
        then:
            result.andExpect(status().isCreated()).andExpect(header().string("Location", is(notNullValue())))
    }

    MockHttpServletRequestBuilder postHirer(String accessToken, Hirer hirer) {
        post(HIRER_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON).content(toJson(hirer))
    }

    void 'known hirer should be deleted'() {
        given:
        String accessToken = getClientAccessToken()
        def mvcResult = this.mvc.perform(postHirer(accessToken, getHirer())).andReturn()
        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete(HIRER_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known hirer should be updated'() {
        given:
        String accessToken = getClientAccessToken()
        def mvcResult = this.mvc.perform(postHirer(accessToken, getHirer())).andReturn()
        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put(HIRER_ID_ENDPOINT,id, accessToken)
                .content(toJson(hirer.with { id= extractId(location);person.name = 'updated';it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    String extractId(String location) {
        location.replaceAll('/hirers/', "")
    }

    String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }

    void 'known hirer should be found'() {
        given:
            String accessToken = getClientAccessToken()
            Hirer hirer = getHirer()
            def mvcResult = this.mvc.perform(postHirer(accessToken, hirer)).andReturn()
            def location = getLocationHeader(mvcResult)
            def id = extractId(location)
        when:
            def result = this.mvc.perform(get(HIRER_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.name', is(equalTo(hirer.person.name))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.document.number', is(equalTo(hirer.person.document.number))))
    }

    void 'known hirer should be found when find all'() {
        given:
            String accessToken = getClientAccessToken()
            this.mvc.perform(postHirer(accessToken, getHirer()))

            this.mvc.perform(post(HIRER_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(hirer.with { person.id = null; person.name = 'temp';person.document.number = '1234576777';it })))
        when:
            def result = this.mvc.perform(get("$HIRER_ENDPOINT",accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(greaterThan(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].person', is(notNullValue())))
    }

    Hirer getHirer() {
        Fixture.from(Hirer.class).gimme("valid")
    }
}
