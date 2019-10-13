package br.com.unopay.api.network.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.repository.PaymentRuleGroupRepository
import br.com.unopay.api.network.model.Partner
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

class PartnerControllerTest extends AuthServerApplicationTests {
    private static final String HIRER_ENDPOINT = '/partners?access_token={access_token}'
    private static final String HIRER_ID_ENDPOINT = '/partners/{id}?access_token={access_token}'

    @Autowired
    private PaymentRuleGroupRepository repository

    
    void 'should create partner'() {
        given:
            String accessToken = getUserAccessToken()
        when:
            def result = this.mvc.perform(postPartner(accessToken, getPartner()))
        then:
            result.andExpect(status().isCreated()).andExpect(header().string("Location", is(notNullValue())))
    }

    MockHttpServletRequestBuilder postPartner(String accessToken, Partner partner) {
        post(HIRER_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON).content(toJson(partner))
    }

    void 'known partner should be deleted'() {
        given:
        String accessToken = getUserAccessToken()
        def mvcResult = this.mvc.perform(postPartner(accessToken, getPartner())).andReturn()
        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete(HIRER_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known partner should be updated'() {
        given:
        String accessToken = getUserAccessToken()
        def mvcResult = this.mvc.perform(postPartner(accessToken, getPartner())).andReturn()
        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put(HIRER_ID_ENDPOINT,id, accessToken)
                .content(toJson(partner.with { id= extractId(location);person.name = 'updated';it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    String extractId(String location) {
        location.replaceAll('/partners/', "")
    }

    String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }

    void 'known partner should be found'() {
        given:
            String accessToken = getUserAccessToken()
            Partner partner = getPartner()
            def mvcResult = this.mvc.perform(postPartner(accessToken, partner)).andReturn()
            def location = getLocationHeader(mvcResult)
            def id = extractId(location)
        when:
            def result = this.mvc.perform(get(HIRER_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.name', is(equalTo(partner.person.name))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.document.number', is(equalTo(partner.person.document.number))))
    }

    void 'known partner should be found when find all'() {
        given:
            String accessToken = getUserAccessToken()
            this.mvc.perform(postPartner(accessToken, getPartner()))

            this.mvc.perform(post(HIRER_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(partner.with { person.id = null; person.name = 'temp';person.document.number = '1234576777';it })))
        when:
            def result = this.mvc.perform(get("$HIRER_ENDPOINT",getClientAccessToken()).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(greaterThan(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].person', is(notNullValue())))
    }

    Partner getPartner() {
        Fixture.from(Partner.class).gimme("valid")
    }
}
