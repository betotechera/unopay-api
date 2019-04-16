package br.com.unopay.api

import java.io.UnsupportedEncodingException

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import org.hamcrest.core.Is._
import org.hamcrest.core.IsNull._
import org.springframework.http.MediaType
import org.springframework.security.crypto.codec.Base64
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders._
import org.springframework.test.web.servlet.result.MockMvcResultMatchers._
import org.springframework.test.web.servlet.{MvcResult, ResultActions}

trait AuthServerApplicationTests extends UnopayApiScalaApplicationTest {

    protected def getClientAccessToken(): String = {
        val result = clientCredentials()
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andReturn()
        getAccessToken(result)
    }

    protected def getUserAccessToken(user: String = "test@test.com",  pwd: String = "test"): String = {
        val result = passwordFlow(user, pwd)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andReturn()
        getAccessToken(result)
    }


    protected def clientCredentials(): ResultActions = {
        this.mvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "client_credentials")
                .param("client_id", "client")
                .param("client_secret", "secret"))
    }


    protected def passwordFlow(username: String,  password: String): ResultActions = {
        return this.mvc.perform(post("/oauth/token")
                .contentType(
                MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "password")
                .param("client_id", "client")
                .param("client_secret", "secret")
                .param("username", username)
                .param("password", password))
    }


    protected def uaaManagerClientCredentials(): ResultActions ={
        return this.mvc.perform(post("/oauth/token")
                .contentType(
                MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "client_credentials")
                .param("client_id", "manager")
                .param("client_secret", "secret"))
    }

    protected def  wrongClientCredentials(): ResultActions =  {
        this.mvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "client_credentials")
                .param("client_id", "wrong")
                .param("client_secret", "wrong"))
    }

    protected def getAuthorizationHeader(clientId: String, clientSecret: String): String=  {
        val creds = String.format("%s:%s", clientId, clientSecret)
        try {
            "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")))
        } catch {
            case e: UnsupportedEncodingException => throw new IllegalStateException("Could not convert String")
        }
    }

    override def toJson(obj: Object): String = {
        try {
            val objectMapper = new ObjectMapper()
            objectMapper.writeValueAsString(obj)
        } catch {
            case e: JsonProcessingException => throw  e
        }
    }

    protected def toJsonFromView(obj: Object,  view: Class[_]): String = {
        try {
            val objectMapper = new ObjectMapper()
            return objectMapper.writerWithView(view).writeValueAsString(obj)
        } catch {
            case e: JsonProcessingException => throw  e
        }
    }

    protected def getLocationHeader(mvcResult: MvcResult): String = {
        mvcResult.getResponse.getHeader("Location")
    }


    protected def  getUAAManagerAccessToken(): String =  {
        val result = uaaManagerClientCredentials()
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andReturn()
        getAccessToken(result)
    }

    protected def clientCredentialsAccessToken(): String = {
        val result = clientCredentials()
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andReturn()

        JsonPath.read[String](result.getResponse.getContentAsString, "$.access_token")
    }

    protected def getAccessToken(result: MvcResult): String = {
        JsonPath.read[String](
                result.getResponse.getContentAsString,
            "$.access_token")
    }

}
