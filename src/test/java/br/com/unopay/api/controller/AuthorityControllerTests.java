package br.com.unopay.api.controller;

import br.com.unopay.api.AuthServerApplicationTests;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthorityControllerTests extends AuthServerApplicationTests {

    @Test
    public void testGetAuthorities() throws Exception {
        String accessToken = clientCredentialsAccessToken();
        this.mvc.perform(
                get("/authorities?access_token={access_token}", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..[0].name", is(notNullValue())))
                .andExpect(jsonPath("$..[0].description", is(notNullValue())));
    }


}