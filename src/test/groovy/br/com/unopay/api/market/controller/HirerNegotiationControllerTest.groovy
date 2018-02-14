package br.com.unopay.api.market.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.market.model.HirerNegotiation
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class HirerNegotiationControllerTest extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator

    Issuer issuerUnderTest
    AccreditedNetwork networkUnderTest
    PaymentRuleGroup paymentRuleGroupUnderTest

    void setup(){
        issuerUnderTest = fixtureCreator.createIssuer()
        paymentRuleGroupUnderTest = fixtureCreator.createPaymentRuleGroup()
        networkUnderTest = fixtureCreator.createNetwork()
    }

    void 'valid negotiation should be created'() {
        given:
        String accessToken = getUserAccessToken()
        HirerNegotiation negotiation = createNegotiation()

        when:
        def result = this.mvc.perform(post('/hirer-negotiations?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(negotiation)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known negotiation should be updated'() {
        given:
        String accessToken = getUserAccessToken()
        HirerNegotiation negotiation = fixtureCreator.createNegotiation()
        def id = negotiation.id
        when:
        def result = this.mvc.perform(put('/hirer-negotiations/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(negotiation.with {  paymentDay = 5; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }


    void 'known negotiation should be found'() {
        given:
        String accessToken = getUserAccessToken()
        HirerNegotiation negotiation = fixtureCreator.createNegotiation()
        def id = negotiation.id
        when:
        def result = this.mvc.perform(get('/hirer-negotiations/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.paymentDay', is(notNullValue())))
    }

    private HirerNegotiation createNegotiation() {
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule() {
            {
                add("hirer", fixtureCreator.createHirer())
                add("product", fixtureCreator.createProduct())
                add("freeInstallmentQuantity", 0)
            }
        })
        negotiation
    }
}
