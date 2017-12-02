package br.com.unopay.api.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.credit.model.Credit
import br.com.unopay.api.model.validation.group.Views
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CreditControllerTest extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator

    void 'valid credit should be created'() {
        given:
        String accessToken = getUserAccessToken()
        Credit credit = fixtureCreator.createCredit()
        when:
        def result = this.mvc.perform(
                            post('/hirers/credits/?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonFromView(credit, Views.Credit.Detail.class)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known credit should be canceled'() {
        given:
        String accessToken = getUserAccessToken()
        Credit credit = Fixture.from(Credit.class).uses(jpaProcessor).gimme("allFields")
        def id = credit.id

        when:
        def result = this.mvc.perform(
                delete("/hirers/credits/{id}?access_token={access_token}", id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known credit should be found'() {
        given:
        String accessToken = getUserAccessToken()
        Credit credit = Fixture.from(Credit.class).uses(jpaProcessor).gimme("allFields")
        def id = credit.id

        when:
        def result = this.mvc.perform(
                get("/hirers/credits/{id}?access_token={access_token}", id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.value', is(notNullValue())))
    }

    void 'known credit should be found when find all'() {
        given:
        String accessToken = getUserAccessToken()
        Fixture.from(Credit.class).uses(jpaProcessor).gimme("allFields")
        when:
        def result = this.mvc.perform(
                get("/hirers/credits?access_token={access_token}", accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].value', is(notNullValue())))
    }
}
