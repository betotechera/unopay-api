package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Branch
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AccreditedNetworkControllerTest extends AuthServerApplicationTests {
    private static final String ACCREDITED_NETWORK_ENDPOINT = '/accredited-networks?access_token={access_token}'
    private static final String ACCREDITED_NETWORK_ID_ENDPOINT = '/accredited-networks/{id}?access_token={access_token}'

    @Autowired
    FixtureCreator fixtureCreator

    void 'should create accreditedNetwork'() {
        given:
            String accessToken = getUserAccessToken()
        when:
            def result = this.mvc.perform(postAccreditedNetwork(accessToken, getAccreditedNetwork()))
        then:
            result.andExpect(status().isCreated()).andExpect(header().string("Location", is(notNullValue())))
    }

    MockHttpServletRequestBuilder postAccreditedNetwork(String accessToken, AccreditedNetwork accreditedNetwork) {
        post(ACCREDITED_NETWORK_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON)
                .content(toJson(accreditedNetwork))
    }

    void 'known accreditedNetwork should be deleted'() {
        given:
        String accessToken = getUserAccessToken()
        def network = fixtureCreator.createNetwork()
        def id = network.id
        when:
        def result = this.mvc.perform(delete(ACCREDITED_NETWORK_ID_ENDPOINT,id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known accreditedNetwork should be updated'() {
        given:
        String accessToken = getUserAccessToken()
        def network = fixtureCreator.createNetwork()
        def id = network.id
        when:
        def result = this.mvc.perform(put(ACCREDITED_NETWORK_ID_ENDPOINT,id, accessToken)
                .content(toJson(accreditedNetwork.with { person.name = 'updated';it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known accreditedNetwork should be found'() {
        given:
        String accessToken = getUserAccessToken()
        def network = fixtureCreator.createNetwork()
        def id = network.id

        when:
        def result = this.mvc.perform(get(ACCREDITED_NETWORK_ID_ENDPOINT,id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.name', is(equalTo(network.person.name))))
                .andExpect(MockMvcResultMatchers
                .jsonPath('$.person.document.number', is(equalTo(network.person.document.number))))
    }

    void 'known accreditedNetwork should be found when find all'() {
        given:
            String accessToken = getUserAccessToken()
            this.mvc.perform(postAccreditedNetwork(accessToken, getAccreditedNetwork()))

            this.mvc.perform(post(ACCREDITED_NETWORK_ENDPOINT, accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                    toJson(accreditedNetwork.with {
                        person.id = null
                        person.name = 'temp'
                        person.document.number = '1234576777'
                        it })))
        when:
            def result = this.mvc.perform(get("$ACCREDITED_NETWORK_ENDPOINT",getClientAccessToken())
                    .contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(greaterThan(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].person', is(notNullValue())))
    }

    void 'known me accreditedNetwork should be updated'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)

        when:
        def result = this.mvc.perform(put("/accredited-networks/me?access_token={access_token}", accessToken)
                .content(toJson(accreditedNetwork.with { person.name = 'updated';it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known me accreditedNetwork should be found'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)
        def person = accreditedNetworkUser.accreditedNetwork.person

        when:
        def result = this.mvc.perform(get("/accredited-networks/me?access_token={access_token}", accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.name', is(equalTo(person.name))))
                .andExpect(MockMvcResultMatchers
                .jsonPath('$.person.document.number', is(equalTo(person.document.number))))
    }

    void 'known establishments should be found'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)
        Establishment establishment = fixtureCreator.createEstablishment(accreditedNetworkUser.accreditedNetwork)

        def id = establishment.id
        when:
        def result = this.mvc.perform(
                get('/accredited-networks/me/establishments/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.fee', is(notNullValue())))
    }

    void 'all establishments should be found'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)
        fixtureCreator.createEstablishment(accreditedNetworkUser.accreditedNetwork)

        when:
        def result = this.mvc.perform(
                get('/accredited-networks/me/establishments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].id', is(notNullValue())))
    }

    void 'valid branch to a logged network should be created'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)
        Establishment establishment = fixtureCreator.createEstablishment(accreditedNetworkUser.accreditedNetwork)
        Branch branch = Fixture.from(Branch.class).gimme("valid", new Rule(){{
            add("headOffice", establishment)
        }})

        when:
        def result = this.mvc.perform(post('/accredited-networks/me/establishments/branches?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(branch)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known branch to a logged network should be updated'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)
        Establishment establishment = fixtureCreator.createEstablishment(accreditedNetworkUser.accreditedNetwork)
        Branch branch = Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("headOffice", establishment)
        }})
        def id = branch.id

        when:
        def result = this.mvc.perform(put('/accredited-networks/me/establishments/branches/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(branch.with { name = '155564'; fantasyName = '11114444555786'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known branches  to a logged network should be found'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)
        Establishment establishment = fixtureCreator.createEstablishment(accreditedNetworkUser.accreditedNetwork)
        Branch branch = Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("headOffice", establishment)
        }})
        def id = branch.id
        when:
        def result = this.mvc.perform(get('/accredited-networks/me/establishments/branches/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.id', is(notNullValue())))
    }

    void 'all branches  to a logged network should be found'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)
        Establishment establishment = fixtureCreator.createEstablishment(accreditedNetworkUser.accreditedNetwork)
        Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("headOffice", establishment)
        }})

        when:
        def result = this.mvc.perform(get('/accredited-networks/me/establishments/branches?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].id', is(notNullValue())))
    }

    AccreditedNetwork getAccreditedNetwork() {
        Fixture.from(AccreditedNetwork.class).gimme("valid")
    }
}
