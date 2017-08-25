package br.com.unopay.api.uaa

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.model.validation.group.Views
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Throwables
import com.jayway.jsonpath.JsonPath
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import org.springframework.http.MediaType
import org.springframework.security.crypto.codec.Base64
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

abstract class AuthServerApplicationTests  extends SpockApplicationTests {

    protected String getClientAccessToken() throws Exception {
        MvcResult result = clientCredentials()
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))
                .andReturn()
        return getAccessToken(result)
    }

    protected String getUserAccessToken(String user='test@test.com',String pwd='test') throws Exception {
        MvcResult result = passwordFlow(user,pwd)
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))
                .andReturn()
        return getAccessToken(result)
    }


    protected ResultActions clientCredentials() throws Exception {
        return this.mvc.perform(post("/oauth/token")
                .contentType(
                        MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "client_credentials")
                .param("client_id", "client")
                .param("client_secret", "secret"))
    }


    protected ResultActions passwordFlow(String username, String password) throws Exception {
        return this.mvc.perform(post("/oauth/token")
                .contentType(
                        MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "password")
                .param("client_id", "client")
                .param("client_secret", "secret")
                .param("username", username)
                .param("password", password))
    }


    protected ResultActions uaaManagerClientCredentials() throws Exception {
        return this.mvc.perform(post("/oauth/token")
                .contentType(
                        MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "client_credentials")
                .param("client_id", "manager")
                .param("client_secret", "secret"))
    }

    protected ResultActions wrongClientCredentials() throws Exception {
        return this.mvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "client_credentials")
                .param("client_id", "wrong")
                .param("client_secret", "wrong"))
    }

    protected String getAuthorizationHeader(String clientId, String clientSecret) {
        String creds = String.format("%s:%s", clientId, clientSecret)
        try {
            return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")))
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not convert String")
        }
    }

    protected String toJson(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper()
            return objectMapper.writeValueAsString(object)
        } catch (JsonProcessingException e) {
            Throwables.propagate(e)
            return null
        }
    }

    protected String toJsonWithoutNetworkPaymentRuleGroups(Object object,Class view) {
        try {
            ObjectMapper objectMapper = new ObjectMapper()
            return objectMapper.writerWithView(view).writeValueAsString(object)
        } catch (JsonProcessingException e) {
            Throwables.propagate(e)
            return null
        }
    }

    protected String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }


    protected String getUAAManagerAccessToken() throws Exception {
        MvcResult result = uaaManagerClientCredentials()
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))
                .andReturn()

        return getAccessToken(result)
    }

    protected String clientCredentialsAccessToken() throws Exception {
        MvcResult result = clientCredentials()
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))
                .andReturn()

        return JsonPath.read(
                result.getResponse().getContentAsString(),
                '$.access_token')
    }

    protected String getAccessToken(MvcResult result) throws UnsupportedEncodingException {
        return JsonPath.read(
                result.getResponse().getContentAsString(),
                '$.access_token')
    }

}
