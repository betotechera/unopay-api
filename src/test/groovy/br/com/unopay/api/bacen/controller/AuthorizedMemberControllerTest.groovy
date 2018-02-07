package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.AuthorizedMember
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.http.MediaType

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

class AuthorizedMemberControllerTest extends AuthServerApplicationTests {

    def 'given valid AuthorizedMember should create it'() {
        given:
        def accessToken = getUserAccessToken()
        AuthorizedMember authorizedMember = Fixture.from(AuthorizedMember).gimme("valid")

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
        AuthorizedMember authorizedMember = Fixture.from(AuthorizedMember).uses(jpaProcessor).gimme("valid")
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
        AuthorizedMember authorizedMember = Fixture.from(AuthorizedMember).uses(jpaProcessor).gimme("valid")
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
}
