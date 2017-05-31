package br.com.unopay.api.controller

import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.Credit
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CreditControllerTest extends AuthServerApplicationTests {

    @Autowired
    SetupCreator setupCreator

    void 'valid credit should be created'() {
        given:
        String accessToken = getUserAccessToken()

        Credit credit = setupCreator.createCredit()
        String document = credit.getHirerDocument()

        when:
        def result = this.mvc.perform(
                            post('/hirers/{document}/credits/?access_token={access_token}', document, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonWithoutNetworkPaymentRuleGroups(credit)))
        then:
        result.andExpect(status().isCreated())
    }
}
