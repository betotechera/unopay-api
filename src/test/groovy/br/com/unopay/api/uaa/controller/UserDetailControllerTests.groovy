package br.com.unopay.api.uaa.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.uaa.AuthServerApplicationTests
import br.com.unopay.api.uaa.model.UserDetail
import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import org.flywaydb.test.annotation.FlywayTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MvcResult
import spock.lang.Ignore

import static com.google.common.collect.Sets.newHashSet
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class UserDetailControllerTests extends AuthServerApplicationTests {

    void should_create_user() throws Exception {

        String accessToken = getClientAccessToken()
        when:
        UserDetail user = user()
        def result = this.mvc.perform(
                post("/users?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
        then:
        passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))
        result.andExpect(status().isCreated())
                .andExpect(header().string("Location", is(notNullValue())))

    }

    void should_update_user_me() throws Exception {

        String accessToken = getClientAccessToken()

        UserDetail user = Fixture.from(UserDetail.class).gimme("with-group")
        when:
        def result = this.mvc.perform(
                post("/users?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))

        MvcResult mvcResult = passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))
                .andReturn()

        String userAccessToken = getAccessToken(mvcResult)

        user.setPassword("otherpass")
        user.setEmail(randomAlphabetic(7)+"@gmail.com")

        this.mvc.perform(
                put("/users/me?access_token={access_token}", userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isNoContent())

        then:
        passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))
        result.andExpect(status().isCreated())

    }

    void should_update_user() throws Exception {

        String accessToken = getClientAccessToken()

        UserDetail user = Fixture.from(UserDetail.class).gimme("with-group")
        when:
        MockHttpServletResponse result = this.mvc.perform(
                post("/users?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isCreated()).andReturn().getResponse()

        ObjectMapper mapper = new ObjectMapper()
        user.setId(mapper.readValue(result.getContentAsString(), UserDetail.class).getId())
        then:
        passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))

        user.setPassword("otherpass")
        user.setEmail(randomAlphabetic(7)+"@gmail.com")

        String uaaManagerAccessToken = getUAAManagerAccessToken()

        this.mvc.perform(
                put("/users/{id}?access_token={access_token}", user.getId(), uaaManagerAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isNoContent())

        passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))

    }

    void should_not_allow_duplicated_users() {

        String accessToken = getClientAccessToken()

        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        when:
        this.mvc.perform(
                post("/users?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isCreated())
        then:
        this.mvc.perform(
                post("/users?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isConflict())
    }



     void should_get_users_by_authority() throws Exception {

        String accessToken = getClientAccessToken()
        String authority = "ROLE_ADMIN"

        UserDetail user = user()

        this.mvc.perform(
                post("/users?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isCreated())

        this.mvc.perform(
                get("/users?authority={authority}&access_token={access_token}", authority, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath('$..[0].email', is(notNullValue())))
    }

    void should_not_allow_client_authentication_on_user_me() throws Exception {

        String accessToken = clientCredentialsAccessToken()
        when:
        def result = this.mvc.perform(
                put("/users/me?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user())))

        then:
        result.andExpect(status().isForbidden())
    }

    @Ignore
    void 'should return groups and authorities when get profile'() {

        UserDetail user = Fixture.from(UserDetail.class).gimme("with-group")

        MvcResult mvcResult = passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))
                .andReturn()

        String userAccessToken = getAccessToken(mvcResult)
        when:
        def result = this.mvc.perform(
                put("/users/me?access_token={access_token}", userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items[0].groups', hasSize(2)))
                .andExpect(jsonPath('$.items[0].groups[0].authorities', hasSize(1)))
    }





    private UserDetail user() {
        UserDetail user = new UserDetail()
        user.setId(randomAlphabetic(4))
        user.setEmail(String.format("%s@gmail.com", randomAlphabetic(3)))
        user.setPassword(randomAlphabetic(5))
        return user
    }


   
    void 'given known group and user should be associate user with group'() {
        given:
        String accessToken = getClientAccessToken()
        String groupId = '1'
        this.mvc.perform(
                put("/users/{id}/groups?access_token={access_token}", groupId, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""["1", "2"]"""))

        when:
        def result = this.mvc.perform(get("/users/{id}/groups?access_token={access_token}&page=1&size=20", groupId, accessToken))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', hasSize(2)))
                .andExpect(jsonPath('$.items[0].name', is(notNullValue())))

    }

   
    void 'should return error when associate groups without list'() {
        given:
        String accessToken = getClientAccessToken()
        String userId = '1'
        when:
        def result = this.mvc.perform(
                put("/users/{userId}/groups?access_token={access_token}", userId, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""""""))

        then:
        result.andExpect(status().isBadRequest())
    }


}
