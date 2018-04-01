package br.com.unopay.api.bacen.controller

import br.com.unopay.api.market.model.AuthorizedMember
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthorizedMemberControllerTest extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator

    def 'given valid AuthorizedMember should create it'() {
        given:
        def accessToken = getUserAccessToken()
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()

        when:
        def result = this.mvc.perform(post('/authorized-members?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(authorizedMember)))

        then:
        result.andExpect(status().isCreated())
    }

    def 'known AuthorizedMember should be found'() {
        given:
        def accessToken = getUserAccessToken()
        AuthorizedMember authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        def id = authorizedMember.id
        when:
        def result = this.mvc.perform(get('/authorized-members/{id}?access_token={access_token}', id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk()).andExpect(jsonPath('$.name', notNullValue()))
    }

    def 'known AuthorizedMember should be updated'() {
        given:
        def accessToken = getUserAccessToken()
        AuthorizedMember authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        authorizedMember.name = "new name"
        def id = authorizedMember.id
        when:
        def result = this.mvc.perform(put('/authorized-members/{id}?access_token={access_token}', id, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(authorizedMember)))

        def found = this.mvc.perform(get('/authorized-members/{id}?access_token={access_token}', id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
        found.andExpect(status().isOk()).andExpect(jsonPath('$.name', equalTo("new name")))
    }

    def 'all AuthorizedMembers should be found'() {
        given:
        def accessToken = getUserAccessToken()
        fixtureCreator.createPersistedAuthorizedMember()
        fixtureCreator.createPersistedAuthorizedMember()
        when:
        def result = this.mvc.perform(get('/authorized-members?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))


        then:
        result.andExpect(status().isOk()).andExpect(jsonPath('$.items[0].name', notNullValue()))
    }

    def 'known AuthorizedMembers should be found by name filter'() {
        given:
        def accessToken = getUserAccessToken()
        def name = fixtureCreator.createPersistedAuthorizedMember().name
        when:
        def result = this.mvc.perform(get('/authorized-members?name={name}&access_token={access_token}', name, accessToken)
                .contentType(MediaType.APPLICATION_JSON))


        then:
        result.andExpect(status().isOk()).andExpect(jsonPath('$.items[0].name', notNullValue()))
    }

    def 'known AuthorizedMember should be deleted'() {
        given:
        def accessToken = getUserAccessToken()
        def id = fixtureCreator.createPersistedAuthorizedMember().id
        when:
        def result = this.mvc.perform(delete('/authorized-members/{id}?access_token={access_token}', id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))


        then:
        result.andExpect(status().isNoContent())
    }
}
