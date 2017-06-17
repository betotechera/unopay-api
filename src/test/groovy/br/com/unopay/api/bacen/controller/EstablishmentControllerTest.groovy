package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class EstablishmentControllerTest extends AuthServerApplicationTests {

    @Autowired
    SetupCreator setupCreator

    AccreditedNetwork networkUnderTest

    void setup(){
        flyway.clean()
        flyway.migrate()
        networkUnderTest = setupCreator.createNetwork()
    }

    void 'valid establishment should be created'() {
        given:
        String accessToken = getClientAccessToken()
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid").with { network = networkUnderTest; it }

        when:
        def result = this.mvc.perform(post('/establishments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(establishment)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known establishment should be updated'() {
        given:
        String accessToken = getClientAccessToken()
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid").with { network = networkUnderTest; it }
        def mvcResult = this.mvc.perform(post('/establishments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(establishment))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put('/establishments/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(establishment.with { id= extractId(location);  fee = 0.3d ; person.id = '1'; person.document.number = '11114444555786'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known establishment should be deleted'() {
        given:
        String accessToken = getClientAccessToken()
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid").with { network = networkUnderTest; it }
        def mvcResult = this.mvc.perform(post('/establishments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(establishment))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete('/establishments/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known establishments should be found'() {
        given:
        String accessToken = getClientAccessToken()
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid").with { network = networkUnderTest; it }
        def mvcResult = this.mvc.perform(post('/establishments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(establishment)))
                .andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(get('/establishments/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.fee', is(notNullValue())))
    }

    void 'all establishments should be found'() {
        given:
        String accessToken = getClientAccessToken()
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid").with { network = networkUnderTest; it }
        this.mvc.perform(post('/establishments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(establishment)))

        when:
        def result = this.mvc.perform(get('/establishments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].fee', is(notNullValue())))
    }


    private String extractId(String location) {
        location.replaceAll('/establishments/', "")
    }

    private String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }
}
