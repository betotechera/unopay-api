package br.com.unopay.api.billing.creditcard.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.billing.creditcard.model.UserCreditCard
import br.com.unopay.api.billing.creditcard.service.UserCreditCardService
import br.com.unopay.api.uaa.AuthServerApplicationTests
import br.com.unopay.api.uaa.model.UserDetail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserCreditCardControllerTest extends AuthServerApplicationTests {

    @Autowired
    private UserCreditCardService userCreditCardService

    @Autowired
    private FixtureCreator fixtureCreator

    private UserDetail userDetail

    void setup(){
        userDetail = fixtureCreator.createUser()
    }

    void 'all user credit cards should be found'(){

        given:
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", userDetail)
        }})
        userCreditCardService.create(userCreditCard)
        String accessToken = getUserAccessToken()

        when:
        def result = this.mvc.perform(get('/credit-cards?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].id', is(notNullValue())))

    }

    void 'known user credit card should be found'(){

        given:
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", userDetail)
        }})
        userCreditCardService.create(userCreditCard)
        String accessToken = getUserAccessToken()
        def id = userCreditCard.id

        when:
        def result = this.mvc.perform(get('/credit-cards/{id}?access_token={access_token}', id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.expirationYear', is(notNullValue())))
    }
}
