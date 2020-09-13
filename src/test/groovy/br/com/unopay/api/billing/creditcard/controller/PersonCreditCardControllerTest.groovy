package br.com.unopay.api.billing.creditcard.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.billing.creditcard.model.PersonCreditCard
import br.com.unopay.api.billing.creditcard.service.PersonCreditCardService
import br.com.unopay.api.uaa.AuthServerApplicationTests
import br.com.unopay.api.uaa.model.UserDetail
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PersonCreditCardControllerTest extends AuthServerApplicationTests {

    @Autowired
    private PersonCreditCardService userCreditCardService

    @Autowired
    private FixtureCreator fixtureCreator

    private UserDetail userDetail

    void setup(){
        userDetail = fixtureCreator.createContractorUser()
    }

    void 'all user credit cards should be found'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
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
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
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

    void 'know user credit card should be deleted'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
        }})
        PersonCreditCard created = userCreditCardService.create(userCreditCard)
        def id = created.id
        String accessToken = getUserAccessToken()

        when:
        def result = this.mvc.perform(delete('/credit-cards/{id}?access_token={access_token}', id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isNoContent())
    }
}
