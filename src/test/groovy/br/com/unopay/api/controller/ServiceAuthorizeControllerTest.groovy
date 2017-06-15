package br.com.unopay.api.controller

import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.ServiceAuthorize
import br.com.unopay.api.service.ServiceAuthorizeService
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ServiceAuthorizeControllerTest extends AuthServerApplicationTests {

    @Autowired
    SetupCreator setupCreator

    @Autowired
    ServiceAuthorizeService service

    void 'valid service authorize credit should be created'() {
        given:
        String accessToken = getUserAccessToken()
        ServiceAuthorize credit = setupCreator.createServiceAuthorize()

        when:
        def result = this.mvc.perform(post('/service-authorizations/?access_token={access_token}',accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonWithoutNetworkPaymentRuleGroups(credit)))
        then:
        result.andExpect(status().isCreated())
    }

}
