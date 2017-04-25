package br.com.unopay.api.controller

import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PaymentInstrumentControllerTest extends AuthServerApplicationTests {

    @Autowired
    SetupCreator setupCreator


    void 'valid instrument should be created'() {
        given:
        String accessToken = getClientAccessToken()
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")
        instrument.getProduct().getAccreditedNetwork().setPaymentRuleGroups(null)

        when:
        def result = this.mvc.perform(post('/payment-instruments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(instrument)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known instrument should be updated'() {
        given:
        String accessToken = getClientAccessToken()
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")
        instrument.getProduct().getAccreditedNetwork().setPaymentRuleGroups(null)

        def mvcResult = this.mvc.perform(post('/payment-instruments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(instrument))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put('/payment-instruments/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(instrument.with { id = extractId(location);  number = '56456'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known instrument should be deleted'() {
        given:
        String accessToken = getClientAccessToken()
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")
        instrument.getProduct().getAccreditedNetwork().setPaymentRuleGroups(null)

        def mvcResult = this.mvc.perform(post('/payment-instruments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(instrument))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete('/payment-instruments/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known payment-instruments should be found'() {
        given:
        String accessToken = getClientAccessToken()
        PaymentInstrument instrument = setupCreator.createPaymentInstrument("valid")
        instrument.getProduct().getAccreditedNetwork().setPaymentRuleGroups(null)

        def mvcResult = this.mvc.perform(post('/payment-instruments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(instrument)))
                .andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(get('/payment-instruments/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.number', is(notNullValue())))
    }

    private String extractId(String location) {
        location.replaceAll('/payment-instruments/', "")
    }

    private String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }
}
