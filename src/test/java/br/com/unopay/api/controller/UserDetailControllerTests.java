package br.com.unopay.api.controller;


import br.com.unopay.api.AuthServerApplicationTests;
import br.com.unopay.api.uaa.model.UserDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserDetailControllerTests extends AuthServerApplicationTests {

    @Test
    public void should_create_user() throws Exception {

        String accessToken = getClientAccessToken();

        UserDetail user = user();
        this.mvc.perform(
                post("/users?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", is(notNullValue())));

        passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", is(notNullValue())));

    }

    @Test
    public void should_update_user_me() throws Exception {

        String accessToken = getClientAccessToken();

        UserDetail user = user();
        this.mvc.perform(
                post("/users?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isCreated());

        MvcResult mvcResult = passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andReturn();

        String userAccessToken = getAccessToken(mvcResult);

        user.setPassword("otherpass");
        user.setEmail(randomAlphabetic(7)+"@gmail.com");
        user.setAuthorities(newHashSet("ROLE_NEW", "ROLE_CLIENT"));

        this.mvc.perform(
                put("/users/me?access_token={access_token}", userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isNoContent());

        passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", is(notNullValue())));

    }

    @Test
    public void should_update_user() throws Exception {

        String accessToken = getClientAccessToken();

        UserDetail user = user();
        MockHttpServletResponse result = this.mvc.perform(
                post("/users?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isCreated()).andReturn().getResponse();

        ObjectMapper mapper = new ObjectMapper();
        user.setId(mapper.readValue(result.getContentAsString(), UserDetail.class).getId());

        passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", is(notNullValue())));

        user.setPassword("otherpass");
        user.setEmail(randomAlphabetic(7)+"@gmail.com");
        user.setAuthorities(newHashSet("ROLE_NEW", "ROLE_CLIENT"));

        String uaaManagerAccessToken = getUAAManagerAccessToken();

        this.mvc.perform(
                put("/users/{id}?access_token={access_token}", user.getId(), uaaManagerAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isNoContent());

        passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", is(notNullValue())));

    }

    @Test
    @FlywayTest(invokeInitDB = true)
    @Ignore
    public void should_not_allow_duplicated_users() throws Exception {

        String accessToken = getClientAccessToken();

        UserDetail user = user();
        this.mvc.perform(
                post("/users?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isCreated());

        this.mvc.perform(
                post("/users?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isConflict());
    }

    @Test
    public void should_get_users_by_authority() throws Exception {

        String accessToken = getClientAccessToken();
        String authority = "ROLE_ADMIN";

        UserDetail user = user();
        user.setAuthorities(newHashSet(authority));

        this.mvc.perform(
                post("/users?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isCreated());

        this.mvc.perform(
                get("/users/search?authority={authority}&access_token={access_token}", authority, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$..[0].email", is(notNullValue())));
    }

    @Test
    public void shoult_not_allow_client_authentication_on_user_me() throws Exception {

        String accessToken = clientCredentialsAccessToken();

        this.mvc.perform(
                put("/users/me?access_token={access_token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user())))
                .andExpect(status().isForbidden());
    }


    private UserDetail user() {
        UserDetail user = new UserDetail();
        user.setId(randomAlphabetic(4));
        user.setEmail(String.format("%s@gmail.com", randomAlphabetic(3)));
        user.setPassword(randomAlphabetic(5));
        return user;
    }

    private String getClientAccessToken() throws Exception {
        MvcResult result = clientCredentials()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andReturn();
        return getAccessToken(result);
    }

    private String getUAAManagerAccessToken() throws Exception {
        MvcResult result = uaaManagerClientCredentials()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andReturn();

        return getAccessToken(result);
    }


    private String getAccessToken(MvcResult result) throws UnsupportedEncodingException {
        return JsonPath.read(
                result.getResponse().getContentAsString(),
                "$.access_token");
    }


}
