package br.com.unopay.api;


import br.com.unopay.api.uaa.model.UserDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class AuthServerApplicationTests  extends  UnopayApiApplicationTests{

    protected ResultActions clientCredentials() throws Exception {
        return this.mvc.perform(post("/oauth/token")
                .contentType(
                        MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "client_credentials")
                .param("client_id", "client")
                .param("client_secret", "secret"));
    }


    protected ResultActions passwordFlow(String username, String password) throws Exception {
        return this.mvc.perform(post("/oauth/token")
                .contentType(
                        MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "password")
                .param("client_id", "client")
                .param("client_secret", "secret")
                .param("username", username)
                .param("password", password));
    }


    protected ResultActions uaaManagerClientCredentials() throws Exception {
        return this.mvc.perform(post("/oauth/token")
                .contentType(
                        MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "client_credentials")
                .param("client_id", "manager")
                .param("client_secret", "secret"));
    }

    protected ResultActions wrongClientCredentials() throws Exception {
        return this.mvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "client_credentials")
                .param("client_id", "wrong")
                .param("client_secret", "wrong"));
    }

    protected String getAuthorizationHeader(String clientId, String clientSecret) {
        String creds = String.format("%s:%s", clientId, clientSecret);
        try {
            return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not convert String");
        }
    }

    protected String toJson(UserDetail user) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            Throwables.propagate(e);
            return null;
        }
    }

    protected String clientCredentialsAccessToken() throws Exception {
        MvcResult result = clientCredentials()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andReturn();

        return JsonPath.read(
                result.getResponse().getContentAsString(),
                "$.access_token");
    }

}
