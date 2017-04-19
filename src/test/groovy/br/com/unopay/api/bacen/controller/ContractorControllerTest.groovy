package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.repository.PaymentRuleGroupRepository
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.beans.factory.annotation.Autowired
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

class ContractorControllerTest extends AuthServerApplicationTests {
    private static final String CONTRACTOR_ENDPOINT = '/contractors?access_token={access_token}'
    private static final String CONTRACTOR_ID_ENDPOINT = '/contractors/{id}?access_token={access_token}'

    @Autowired
    private PaymentRuleGroupRepository repository

    
    void 'should create contractor'() {
        given:
            String accessToken = getClientAccessToken()
        when:
            def result = this.mvc.perform(postHired(accessToken, getContractor()))
        then:
            result.andExpect(status().isCreated()).andExpect(header().string("Location", is(notNullValue())))
    }

    MockHttpServletRequestBuilder postHired(String accessToken, Contractor contractor) {
        post(CONTRACTOR_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON).content(toJson(contractor))
    }

    void 'known contractor should be deleted'() {
        given:
        String accessToken = getClientAccessToken()
        def mvcResult = this.mvc.perform(postHired(accessToken, getContractor())).andReturn()
        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete(CONTRACTOR_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known contractor should be updated'() {
        given:
        String accessToken = getClientAccessToken()
        def mvcResult = this.mvc.perform(postHired(accessToken, getContractor())).andReturn()
        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put(CONTRACTOR_ID_ENDPOINT,id, accessToken)
                .content(toJson(contractor.with { id= extractId(location);person.name = 'updated';it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    String extractId(String location) {
        location.replaceAll('/contractors/', "")
    }

    String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }

    void 'known contractor should be found'() {
        given:
            String accessToken = getClientAccessToken()
            Contractor contractor = getContractor()
            def mvcResult = this.mvc.perform(postHired(accessToken, contractor)).andReturn()
            def location = getLocationHeader(mvcResult)
            def id = extractId(location)
        when:
            def result = this.mvc.perform(get(CONTRACTOR_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.name', is(equalTo(contractor.person.name))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.document.number', is(equalTo(contractor.person.document.number))))
    }

    void 'known contractor should be found when find all'() {
        given:
            String accessToken = getClientAccessToken()
            this.mvc.perform(postHired(accessToken, getContractor()))

            this.mvc.perform(post(CONTRACTOR_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(contractor.with { person.id = null; person.name = 'temp';person.document.number = '1234576777';it })))
        when:
            def result = this.mvc.perform(get("$CONTRACTOR_ENDPOINT",accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(greaterThan(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].person', is(notNullValue())))
    }

    Contractor getContractor() {
        Fixture.from(Contractor.class).gimme("valid")
    }
}
