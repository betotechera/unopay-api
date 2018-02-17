package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.bacen.model.AuthorizedMember
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.credit.model.Credit
import br.com.unopay.api.credit.model.CreditSituation
import static br.com.unopay.api.function.FixtureFunctions.*
import br.com.unopay.api.function.FixtureFunctions
import br.com.unopay.api.market.model.HirerNegotiation
import br.com.unopay.api.model.validation.group.Views
import br.com.unopay.api.uaa.AuthServerApplicationTests
import br.com.unopay.api.uaa.model.UserDetail
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class HirerControllerTest extends AuthServerApplicationTests {
    private static final String HIRER_ENDPOINT = '/hirers?access_token={access_token}'
    private static final String HIRER_ID_ENDPOINT = '/hirers/{id}?access_token={access_token}'

    @Autowired
    private FixtureCreator fixtureCreator

    void 'should create hirer'() {
        given:
            String accessToken = getUserAccessToken()
        when:
            def result = this.mvc.perform(postHirer(accessToken, getHirer()))
        then:
            result.andExpect(status().isCreated()).andExpect(header().string("Location", is(notNullValue())))
    }

    MockHttpServletRequestBuilder postHirer(String accessToken, Hirer hirer) {
        post(HIRER_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON).content(toJson(hirer))
    }

    void 'known hirer should be deleted'() {
        given:
        String accessToken = getUserAccessToken()
        def mvcResult = this.mvc.perform(postHirer(accessToken, getHirer())).andReturn()
        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete(HIRER_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known hirer should be updated'() {
        given:
        String accessToken = getUserAccessToken()
        def mvcResult = this.mvc.perform(postHirer(accessToken, getHirer())).andReturn()
        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put(HIRER_ID_ENDPOINT,id, accessToken)
                .content(toJson(hirer.with { id= extractId(location);person.name = 'updated';it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    String extractId(String location) {
        location.replaceAll('/hirers/', "")
    }

    String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }

    void 'known hirer should be found'() {
        given:
            String accessToken = getUserAccessToken()
            Hirer hirer = getHirer()
            def mvcResult = this.mvc.perform(postHirer(accessToken, hirer)).andReturn()
            def location = getLocationHeader(mvcResult)
            def id = extractId(location)
        when:
            def result = this.mvc.perform(get(HIRER_ID_ENDPOINT,id, accessToken)
                    .contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.name', is(equalTo(hirer.person.name))))
                .andExpect(MockMvcResultMatchers
                    .jsonPath('$.person.document.number', is(equalTo(hirer.person.document.number))))
    }

    void 'known hirer should be found when find all'() {
        given:
            String accessToken = getUserAccessToken()
            this.mvc.perform(postHirer(accessToken, getHirer()))

            this.mvc.perform(post(HIRER_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON)
                    .content(
                    toJson(hirer.with {
                        person.id = null
                        person.name = 'temp'
                        person.document.number = '1234576777'
                        it
                    })))
        when:
            def result = this.mvc.perform(get("$HIRER_ENDPOINT",accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(greaterThan(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].person', is(notNullValue())))
    }

    void 'known contractor should be updated'() {
        given:
        def hirerUser = fixtureCreator.createHirerUser()
        String accessToken = getUserAccessToken(hirerUser.email, hirerUser.password)
        def contractor = fixtureCreator.createContractor()
        fixtureCreator.createPersistedContract(contractor, fixtureCreator.createProduct(),hirerUser.hirer)
        def id = contractor.id

        when:
        def result = this.mvc.perform(put("/hirers/me/contractors/{id}?access_token={access_token}",id, accessToken)
                .content(toJson(contractor.with { person.name = 'updated';it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known contractor should be found'() {
        given:
        def hirerUser = fixtureCreator.createHirerUser()
        String accessToken = getUserAccessToken(hirerUser.email, hirerUser.password)
        def contractor = fixtureCreator.createContractor()
        fixtureCreator.createPersistedContract(contractor,fixtureCreator.createProduct(),hirerUser.hirer)
        def id = contractor.id

        when:
        def result = this.mvc.perform(get("/hirers/me/contractors/{id}?access_token={access_token}",id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.name', is(equalTo(contractor.person.name))))
                .andExpect(MockMvcResultMatchers
                .jsonPath('$.person.document.number', is(equalTo(contractor.person.document.number))))
    }

    void 'known contractor should be found when find all'() {
        given:
        def hirerUser = fixtureCreator.createHirerUser()
        String accessToken = getUserAccessToken(hirerUser.email, hirerUser.password)
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(),
                fixtureCreator.createProduct(),hirerUser.hirer)
        when:
        def result = this.mvc.perform(get("/hirers/me/contractors?access_token={access_token}",accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].person', is(notNullValue())))
    }

    void 'valid credit should be created'() {
        given:
        def hirerUser = fixtureCreator.createHirerUser()
        String accessToken = getUserAccessToken(hirerUser.email, hirerUser.password)

        Credit credit = fixtureCreator.createCredit()

        when:
        def result = this.mvc.perform(
                post('/hirers/me/credits/?access_token={access_token}', accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonFromView(credit, Views.Credit.Detail.class)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known credit should be canceled'() {
        given:
        def hirerUser = fixtureCreator.createHirerUser()
        String accessToken = getUserAccessToken(hirerUser.email, hirerUser.password)
        Credit credit = Fixture.from(Credit.class).uses(jpaProcessor).gimme("allFields", new Rule() {{
            add("hirer", hirerUser.hirer)
            add("situation", CreditSituation.AVAILABLE)
        }})
        def id = credit.id

        when:
        def result = this.mvc.perform(delete("/hirers/me/credits/{id}?access_token={access_token}",id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known credit should be found'() {
        given:
        def hirerUser = fixtureCreator.createHirerUser()
        String accessToken = getUserAccessToken(hirerUser.email, hirerUser.password)
        Credit credit = Fixture.from(Credit.class).uses(jpaProcessor).gimme("allFields", new Rule() {{
            add("hirer", hirerUser.hirer)
        }})
        def id = credit.id

        when:
        def result = this.mvc.perform(get("/hirers/me/credits/{id}?access_token={access_token}",id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.value', is(notNullValue())))
    }

    void 'known credit should be found when find all'() {
        given:
        def hirerUser = fixtureCreator.createHirerUser()
        String accessToken = getUserAccessToken(hirerUser.email, hirerUser.password)
        Fixture.from(Credit.class).uses(jpaProcessor).gimme("allFields", new Rule() {{
            add("hirer", hirerUser.hirer)
        }})
        when:
        def result = this.mvc.perform(get("/hirers/me/credits?access_token={access_token}",accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].value', is(notNullValue())))
    }

    void 'known me negotiation should be updated'() {
        given:
        UserDetail hirerUser = fixtureCreator.createHirerUser()
        String accessToken = getUserAccessToken(hirerUser.email, hirerUser.password)
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = fixtureCreator.createNegotiation(hirerUser.hirer, product, instant("one day from now"))
        def id = negotiation.id
        when:
        def result = this.mvc.perform(put('/hirers/me/negotiations/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(negotiation.with {  paymentDay = 5; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known me negotiation should be found'() {
        given:
        UserDetail hirerUser = fixtureCreator.createHirerUser()
        String accessToken = getUserAccessToken(hirerUser.email, hirerUser.password)
        HirerNegotiation negotiation = fixtureCreator.createNegotiation(hirerUser.hirer)
        def id = negotiation.id
        when:
        def result = this.mvc.perform(get('/hirers/me/negotiations/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.paymentDay', is(notNullValue())))
    }

    void 'known me authorizedMember should be found'() {
        given:
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        UserDetail hirerUser = fixtureCreator.createHirerUser(authorizedMember.contract.hirer)
        String accessToken = getUserAccessToken(hirerUser.email, hirerUser.password)
        def id = authorizedMember.id
        when:
        def result = this.mvc.perform(get('/hirers/me/authorized-members/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.name', is(notNullValue())))
    }

    void 'all me authorizedMember should be found'() {
        given:
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        UserDetail hirerUser = fixtureCreator.createHirerUser(authorizedMember.contract.hirer)
        String accessToken = getUserAccessToken(hirerUser.email, hirerUser.password)
        when:
        def result = this.mvc.perform(get('/hirers/me/authorized-members?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.content[0].name', is(notNullValue())))
    }

    void 'known me authorizedMember should be updated'() {
        given:
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        UserDetail hirerUser = fixtureCreator.createHirerUser(authorizedMember.contract.hirer)
        String accessToken = getUserAccessToken(hirerUser.email, hirerUser.password)
        def id = authorizedMember.id
        when:
        def result = this.mvc.perform(put('/hirers/me/authorized-members/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(authorizedMember.with {  name = "new name"; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'should delete my authorizedMember'() {
        given:
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        UserDetail hirerUser = fixtureCreator.createHirerUser(authorizedMember.contract.hirer)
        String accessToken = getUserAccessToken(hirerUser.email, hirerUser.password)
        def id = authorizedMember.id
        when:
        def result = this.mvc.perform(delete("/hirers/me/authorized-members/{id}?access_token={access_token}",id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    Hirer getHirer() {
        Fixture.from(Hirer.class).gimme("valid")
    }
}
