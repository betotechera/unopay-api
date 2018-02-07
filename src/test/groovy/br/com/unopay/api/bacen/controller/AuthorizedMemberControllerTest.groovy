package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.AuthorizedMember
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
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
}
