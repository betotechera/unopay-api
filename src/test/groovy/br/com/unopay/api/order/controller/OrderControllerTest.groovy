package br.com.unopay.api.order.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class OrderControllerTest extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator

    void 'valid order should be created'() {
        given:
        String accessToken = getUserAccessToken()
        def product = fixtureCreator.createProduct()
        Order order = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("product", product)
        }})

        when:
        def result = this.mvc.perform(post('/orders?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(order)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known orders should be found'() {
        given:
        String accessToken = getUserAccessToken()
        Order order = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid")
        def id = order.id

        when:
        def result = this.mvc.perform(get('/orders/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.number', is(notNullValue())))
    }
}
