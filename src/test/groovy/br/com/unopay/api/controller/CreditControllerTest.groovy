package br.com.unopay.api.controller

import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.Credit
import br.com.unopay.api.model.validation.group.Views
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CreditControllerTest extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator

    void 'valid credit should be created'() {
        given:
        String accessToken = getUserAccessToken()

        Credit credit = fixtureCreator.createCredit()
        String document = credit.getHirerDocument()

        when:
        def result = this.mvc.perform(
                            post('/hirers/{document}/credits/?access_token={access_token}', document, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonWithoutNetworkPaymentRuleGroups(credit, Views.Credit.Detail.class)))
        then:
        result.andExpect(status().isCreated())
    }
}
