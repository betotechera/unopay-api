package br.com.unopay.api.billing.boleto.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.billing.boleto.model.Ticket
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class TicketControllerTest extends AuthServerApplicationTests {


    void 'known boletos should be returned'() {
        given:
        String accessToken = getUserAccessToken()
        Fixture.from(Ticket.class).uses(jpaProcessor).gimme("valid")

        when:
        def result = this.mvc.perform(get('/boletos?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].value', is(notNullValue())))
    }
}
