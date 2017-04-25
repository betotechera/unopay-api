package br.com.unopay.api.controller

import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.Product
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ProductControllerTest extends AuthServerApplicationTests {

    @Autowired
    SetupCreator setupCreator

    void 'valid product should be created'() {
        given:
        String accessToken = getClientAccessToken()
        Product product = setupCreator.createProduct()

        when:
        def result = this.mvc.perform(post('/products?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(product)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known product should be updated'() {
        given:
        String accessToken = getClientAccessToken()
        Product product = setupCreator.createProduct()

        def mvcResult = this.mvc.perform(post('/products?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(product))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put('/products/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(product.with { id = extractId(location);  name = '56456'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known product should be deleted'() {
        given:
        String accessToken = getClientAccessToken()
        Product product = setupCreator.createProduct()

        def mvcResult = this.mvc.perform(post('/products?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(product))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete('/products/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known products should be found'() {
        given:
        String accessToken = getClientAccessToken()
        Product product = setupCreator.createProduct()

        def mvcResult = this.mvc.perform(post('/products?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(product)))
                .andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(get('/products/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.name', is(notNullValue())))
    }

    private String extractId(String location) {
        location.replaceAll('/products/', "")
    }

    private String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }
}
