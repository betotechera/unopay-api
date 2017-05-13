package br.com.unopay.api.controller

import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.ContractorInstrumentCredit
import br.com.unopay.api.model.Credit
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ContractorInstrumentCreditControllerTest extends AuthServerApplicationTests {

    @Autowired
    SetupCreator setupCreator

    void 'valid payment instrument credit should be created'() {
        given:
        String accessToken = getUserAccessToken()

        ContractorInstrumentCredit credit = setupCreator.createContractorInstrumentCredit()
        String instrumentId = credit?.paymentInstrument?.id
        String contractorId = credit?.paymentInstrument?.contractor?.id

        when:
        def result = this.mvc.perform(
                            post('/contractors/{contractorId}/payment-instruments/{instrumentId}/credits?access_token={access_token}',
                                    contractorId, instrumentId, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonWithoutNetworkPaymentRuleGroups(credit)))
        then:
        result.andExpect(status().isCreated())
    }
}
