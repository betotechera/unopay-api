package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class IssuerControllerTest extends AuthServerApplicationTests {


    void 'valid issuer should be created'() {
        given:
        String accessToken = getUserAccessToken()
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        def result = this.mvc.perform(post('/issuers?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(issuer)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known issuer should be updated'() {
        given:
        String accessToken = getUserAccessToken()
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        def mvcResult = this.mvc.perform(post('/issuers?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(issuer))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put('/issuers/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(issuer.with { id= extractId(location);  fee = 0.3d ; person.id = '1'; paymentAccount.id = '1'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known issuer should be deleted'() {
        given:
        String accessToken = getUserAccessToken()
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        def mvcResult = this.mvc.perform(post('/issuers?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(issuer))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete('/issuers/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known issuers should be found'() {
        given:
        String accessToken = getUserAccessToken()
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        def mvcResult = this.mvc.perform(post('/issuers?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(issuer)))
                .andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(get('/issuers/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.fee', is(notNullValue())))
    }

    void 'all issuers should be found'() {
        given:
        String accessToken = getUserAccessToken()
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        this.mvc.perform(post('/issuers?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(issuer)))

        when:
        def result = this.mvc.perform(get('/issuers?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].id', is(notNullValue())))
    }


    private String extractId(String location) {
        location.replaceAll('/issuers/', "")
    }

}
