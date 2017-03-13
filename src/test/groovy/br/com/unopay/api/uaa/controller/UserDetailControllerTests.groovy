package br.com.unopay.api.uaa.controller

import br.com.unopay.api.uaa.AuthServerApplicationTests
import br.com.unopay.api.uaa.model.UserDetail
import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MvcResult

import static com.google.common.collect.Sets.newHashSet
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
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

        UserDetail user = user()
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
        user.setAuthorities(newHashSet("ROLE_NEW", "ROLE_CLIENT"))

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

        UserDetail user = user()
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
        user.setAuthorities(newHashSet("ROLE_NEW", "ROLE_CLIENT"))

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

    void should_not_allow_duplicated_users() throws Exception {

        String accessToken = getClientAccessToken()

        UserDetail user = user()
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
        user.setAuthorities(newHashSet(authority))

        this.mvc.perform(
                post("/users?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isCreated())

        this.mvc.perform(
                get("/users/search?authority={authority}&access_token={access_token}", authority, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath('$..[0].email', is(notNullValue())))
    }

    void shoult_not_allow_client_authentication_on_user_me() throws Exception {

        String accessToken = clientCredentialsAccessToken()
        when:
        def result = this.mvc.perform(
                put("/users/me?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user())))

        then:
        result.andExpect(status().isForbidden())
    }


    private UserDetail user() {
        UserDetail user = new UserDetail()
        user.setId(randomAlphabetic(4))
        user.setEmail(String.format("%s@gmail.com", randomAlphabetic(3)))
        user.setPassword(randomAlphabetic(5))
        return user
    }

}
