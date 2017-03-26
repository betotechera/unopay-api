package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.PaymentRuleGroup
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

class PaymentRuleGroupControllerTest extends AuthServerApplicationTests {
    private static final String PAYMENT_RULE_GROUP_ENDPOINT = '/payment-rule-groups?access_token={access_token}'
    private static final String PAYMENT_RULE_GROUP_ID_ENDPOINT = '/payment-rule-groups/{id}?access_token={access_token}'

    @Autowired
    private PaymentRuleGroupRepository repository

    
    void 'should create paymentRuleGroup'() {
        given:
            String accessToken = getClientAccessToken()
        when:
            def result = this.mvc.perform(postPaymentRuleGroup(accessToken, getPaymentRuleGroup()))
        then:
            result.andExpect(status().isCreated()).andExpect(header().string("Location", is(notNullValue())))
    }

    MockHttpServletRequestBuilder postPaymentRuleGroup(String accessToken, PaymentRuleGroup paymentRuleGroup) {
        post(PAYMENT_RULE_GROUP_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON).content(toJson(paymentRuleGroup))
    }

    void 'known paymentRuleGroup should be deleted'() {
        given:
        String accessToken = getClientAccessToken()
        def mvcResult = this.mvc.perform(postPaymentRuleGroup(accessToken, getPaymentRuleGroup())).andReturn()
        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete(PAYMENT_RULE_GROUP_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known paymentRuleGroup should be updated'() {
        given:
        String accessToken = getClientAccessToken()
        def mvcResult = this.mvc.perform(postPaymentRuleGroup(accessToken, getPaymentRuleGroup())).andReturn()
        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put(PAYMENT_RULE_GROUP_ID_ENDPOINT,id, accessToken)
                .content(toJson(paymentRuleGroup.with { id= extractId(location);name = 'updated'; code = 'updated';it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    String extractId(String location) {
        location.replaceAll('/payment-rule-groups/', "")
    }

    String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }

    void 'known paymentRuleGroup should be found'() {
        given:
            String accessToken = getClientAccessToken()
            PaymentRuleGroup paymentRuleGroup = getPaymentRuleGroup()
            def mvcResult = this.mvc.perform(postPaymentRuleGroup(accessToken, paymentRuleGroup)).andReturn()
            def location = getLocationHeader(mvcResult)
            def id = extractId(location)
        when:
            def result = this.mvc.perform(get(PAYMENT_RULE_GROUP_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.name', is(equalTo(paymentRuleGroup.name))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.code', is(equalTo(paymentRuleGroup.code))))
    }

    void 'known paymentRuleGroups should be found when find all'() {
        given:
            String accessToken = getClientAccessToken()
            this.mvc.perform(postPaymentRuleGroup(accessToken, getPaymentRuleGroup()))

            this.mvc.perform(post(PAYMENT_RULE_GROUP_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(paymentRuleGroup.with { name = 'temp'; code = 'temp';it })))
        when:
            def result = this.mvc.perform(get("$PAYMENT_RULE_GROUP_ENDPOINT",accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(greaterThan(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].name', is(notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].code', is(notNullValue())))
    }

    PaymentRuleGroup getPaymentRuleGroup() {
        Fixture.from(PaymentRuleGroup.class).gimme("valid")
    }
}
