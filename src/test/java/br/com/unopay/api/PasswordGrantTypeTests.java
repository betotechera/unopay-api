package br.com.unopay.api;


import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PasswordGrantTypeTests extends AuthServerApplicationTests {
    @Test
    @FlywayTest(locationsForMigrate = {"db/migration"})
    public void should_return_access_token() throws Exception {
        this.mvc.perform(post("/oauth/token")
                .contentType(
                        MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "password")
                .param("client_id", "client")
                .param("client_secret", "secret")
                .param("username", "test@test.com")
                .param("password", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andExpect(jsonPath("$.token_type", is(equalTo("bearer"))));

    }

    @Test
    public void should_return_unauthorized_error() throws Exception {
        this.mvc.perform(post("/oauth/token")
                .contentType(
                        MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "password")
                .param("client_id", "client")
                .param("client_secret", "secret")
                .param("username", "wrong")
                .param("password", "wrong"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description", is(equalTo("Bad credentials"))));
    }

}

