package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.Provider
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ProviderControllerTest  extends AuthServerApplicationTests {

    void 'valid provider should be created'() {
        given:
        String accessToken = getClientAccessToken()
        Provider provider = Fixture.from(Provider.class).gimme("valid")

        when:
        def result = this.mvc.perform(post('/providers?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(provider)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known provider should be updated'() {
        given:
        String accessToken = getClientAccessToken()
        Provider provider = Fixture.from(Provider.class).gimme("valid")
        def mvcResult = this.mvc.perform(post('/providers?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(provider))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put('/providers/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(provider.with { id= extractId(location); name = '56456'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known provider should be deleted'() {
        given:
        String accessToken = getClientAccessToken()
        Provider provider = Fixture.from(Provider.class).gimme("valid")
        def mvcResult = this.mvc.perform(post('/providers?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(provider))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete('/providers/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known providers should be found'() {
        given:
        String accessToken = getClientAccessToken()
        Provider provider = Fixture.from(Provider.class).gimme("valid")
        def mvcResult = this.mvc.perform(post('/providers?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(provider)))
                .andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(get('/providers/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.name', is(notNullValue())))
    }

    private String extractId(String location) {
        location.replaceAll('/providers/', "")
    }

    private String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }
}