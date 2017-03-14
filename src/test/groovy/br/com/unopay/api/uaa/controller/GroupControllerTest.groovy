package br.com.unopay.api.uaa.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.uaa.AuthServerApplicationTests
import br.com.unopay.api.uaa.model.Group
import org.flywaydb.test.annotation.FlywayTest
import org.springframework.http.MediaType
import spock.lang.Ignore

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GroupControllerTest extends AuthServerApplicationTests {

    void 'should create group'() {

        String accessToken = getClientAccessToken()
        when:
        Group group = Fixture.from(Group.class).gimme("valid")

        def result = this.mvc.perform(
                post("/groups?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(group)))
        then:
        result.andExpect(status().isCreated())
                .andExpect(header().string("Location", is(notNullValue())))

    }

    void 'known group should be deleted'() {
        given:
        String accessToken = getClientAccessToken()
        Group group = Fixture.from(Group.class).gimme("valid")
        def mvcResult = this.mvc.perform(
                post("/groups?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(group))).andReturn()
        def location = mvcResult.getResponse().getHeader("Location")
        def id = location.replaceAll('/groups/', "")

        when:
        def result = this.mvc.perform(
                delete("/groups/{id}?access_token={access_token}",id, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())

    }

    void 'known group should be found'() {
        given:
        String accessToken = getClientAccessToken()
        Group group = Fixture.from(Group.class).gimme("valid")
        def mvcResult = this.mvc.perform(
                post("/groups?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(group))).andReturn()
        def location = mvcResult.getResponse().getHeader("Location")
        def id = location.replaceAll('/groups/', "")

        when:
        def result = this.mvc.perform(
                get("/groups/{id}?access_token={access_token}",id, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.name', is(equalTo(group.name))))
                .andExpect(jsonPath('$.description', is(equalTo(group.description))))

    }

    @FlywayTest(invokeCleanDB = true)
    void 'known groups should be found when find all'() {
        given:
        String accessToken = getClientAccessToken()
        Group group = Fixture.from(Group.class).gimme("valid")
        this.mvc.perform(
                post("/groups?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(group)))
        this.mvc.perform(
                post("/groups?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(group)))

        when:
        def result = this.mvc.perform(
                get("/groups?access_token={access_token}&page=1&size=2",accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', hasSize(2)))
                .andExpect(jsonPath('$.total', is(equalTo(2))))
                .andExpect(jsonPath('$.items[0].name', is(notNullValue())))
                .andExpect(jsonPath('$.items[0].description', is(notNullValue())))
    }


    @FlywayTest(invokeCleanDB = true)
    @Ignore
    void 'given known group and user should be add member to group'() {
        given:
        String accessToken = getClientAccessToken()
        String groupId = '1'
        this.mvc.perform(
                put("/groups/{groupId}/members?access_token={access_token}", groupId, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""["1", "2"]"""))

        when:
        def result = this.mvc.perform(get("/groups/{groupId}/members?access_token={access_token}&page=1&size=20", groupId, accessToken))
        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', hasSize(2)))
                .andExpect(jsonPath('$.total', is(equalTo(2))))
                .andExpect(jsonPath('$.items[0].email', is(notNullValue())))
    }
}
