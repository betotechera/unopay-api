package br.com.unopay.api.market.controller

import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class BonusBillingControllerTest  extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator

    def 'given valid bonusBilling should create it'() {
        given:
        def accessToken = getUserAccessToken()
        def bonusBilling = fixtureCreator.createBonusBillingToPersist()

        when:
        def result = this.mvc.perform(post('/bonus-billings?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(bonusBilling)))

        then:
        result.andExpect(status().isCreated())
    }

    def 'known bonusBilling should be found'() {
        given:
        def accessToken = getUserAccessToken()
        def id = fixtureCreator.createPersistedBonusBilling().id
        when:
        def result = this.mvc.perform(get('/bonus-billings/{id}?access_token={access_token}', id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk()).andExpect(jsonPath('$.total', notNullValue()))
    }

    def 'known BonusBilling should be updated'() {
        given:
        def accessToken = getUserAccessToken()
        def bonusBilling = fixtureCreator.createPersistedBonusBilling()
        bonusBilling.number = "123"
        def id = bonusBilling.id
        when:
        def result = this.mvc.perform(put('/bonus-billings/{id}?access_token={access_token}', id, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(bonusBilling)))

        def found = this.mvc.perform(get('/bonus-billings/{id}?access_token={access_token}', id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
        found.andExpect(status().isOk()).andExpect(jsonPath('$.number', equalTo("123")))
    }

    def 'all BonusBillings should be found'() {
        given:
        def accessToken = getUserAccessToken()
        fixtureCreator.createPersistedBonusBilling()
        fixtureCreator.createPersistedBonusBilling()
        when:
        def result = this.mvc.perform(get('/bonus-billings?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))


        then:
        result.andExpect(status().isOk()).andExpect(jsonPath('$.items[0].id', notNullValue()))
    }

    def 'known BonusBillings should be found by filter'() {
        given:
        def accessToken = getUserAccessToken()
        def document = fixtureCreator.createPersistedBonusBilling().person.documentNumber()
        when:
        def result = this.mvc.perform(get('/bonus-billings?document={document}&access_token={access_token}', document, accessToken)
                .contentType(MediaType.APPLICATION_JSON))


        then:
        result.andExpect(status().isOk()).andExpect(jsonPath('$.items[0].id', notNullValue()))
    }

    def 'known BonusBilling should be deleted'() {
        given:
        def accessToken = getUserAccessToken()
        def id = fixtureCreator.createPersistedBonusBilling().id
        when:
        def result = this.mvc.perform(delete('/bonus-billings/{id}?access_token={access_token}', id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))


        then:
        result.andExpect(status().isNoContent())
    }
}