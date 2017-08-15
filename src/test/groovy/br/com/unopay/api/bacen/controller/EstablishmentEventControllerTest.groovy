package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.EstablishmentEvent
import br.com.unopay.api.bacen.model.ServiceType
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.uaa.AuthServerApplicationTests
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.uaa.service.GroupService
import br.com.unopay.api.uaa.service.UserDetailService
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class EstablishmentEventControllerTest extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator
    @Autowired
    UserDetailService userDetailService
    @Autowired
    GroupService groupService

    AccreditedNetwork networkUnderTest

    Establishment userEstablishment

    UserDetail user


    void setup(){
        flyway.clean()
        flyway.migrate()
        networkUnderTest = fixtureCreator.createNetwork()
        user = fixtureCreator.createEstablishmentUser()
        groupService.associateUserWithGroups(user.id, ['1'] as Set)
        userEstablishment = user.establishment
    }

    void 'valid establishment should be created'() {
        given:
        String accessToken = getUserAccessToken()
        def event = fixtureCreator.createEvent(ServiceType.FUEL_ALLOWANCE)
        def establishment = fixtureCreator.createEstablishment()
        EstablishmentEvent establishmentEvent = Fixture.from(EstablishmentEvent.class)
                                                                        .gimme("withoutReferences", new Rule(){{
            add("event", event)
        }})
        def id = establishment.id
        when:
        def result = this.mvc.perform(post('/establishments/{id}/event-fees?access_token={access_token}',id,accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(establishmentEvent)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known establishment should be updated'() {
        given:
        String accessToken = getUserAccessToken()
        def establishmentEvent = fixtureCreator.createEstablishmentEvent()
        def establishmentId = establishmentEvent.establishment.id
        def id = establishmentEvent.id

        when:
        def result = this.mvc.perform(
                put('/establishments/{establishmentId}/event-fees/{id}?access_token={access_token}',
                                                            establishmentId, id, accessToken)
                .content(toJson(establishmentEvent.with { value = 0.3d ; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known establishment should be deleted'() {
        given:
        String accessToken = getUserAccessToken()
        def establishmentEvent = fixtureCreator.createEstablishmentEvent()
        def establishmentId = establishmentEvent.establishment.id
        def id = establishmentEvent.id

        when:
        def result = this.mvc.perform(
                delete('/establishments/{establishmentId}/event-fees/{id}?access_token={access_token}',
                establishmentId, id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known establishments should be found'() {
        given:
        String accessToken = getUserAccessToken()
        def establishmentEvent = fixtureCreator.createEstablishmentEvent()
        def establishmentId = establishmentEvent.establishment.id
        def id = establishmentEvent.id

        when:
        def result = this.mvc
                .perform(get('/establishments/{establishmentId}/event-fees/{id}?access_token={access_token}',
                establishmentId, id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.value', is(notNullValue())))
    }

    void 'all establishments should be found'() {
        given:
        String accessToken = getUserAccessToken()
        def establishmentEvent = fixtureCreator.createEstablishmentEvent()
        def establishmentId = establishmentEvent.establishment.id

        when:
        def result = this.mvc.perform(get('/establishments/{establishmentId}/event-fees/?access_token={access_token}',
                establishmentId, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].value', is(notNullValue())))
    }

    void 'me event should be created'() {
        given:

        String accessToken = getUserAccessToken(user.email, user.password)
        def event = fixtureCreator.createEvent()
        EstablishmentEvent establishmentEvent = Fixture.from(EstablishmentEvent.class)
                .gimme("withoutReferences", new Rule(){{
            add("event", event)
        }})
        when:
        def result = this.mvc.perform(post('/establishments/me/event-fees?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(establishmentEvent)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'me event should be updated'() {
        given:
        String accessToken = getUserAccessToken(user.email, user.password)
        def establishmentEvent = fixtureCreator.createEstablishmentEvent(userEstablishment)
        def id = establishmentEvent.id

        when:
        def result = this.mvc.perform(
                put('/establishments/me/event-fees/{id}?access_token={access_token}', id, accessToken)
                        .content(toJson(establishmentEvent.with { value = 0.3d ; it }))
                        .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'me event should be deleted'() {
        given:
        String accessToken = getUserAccessToken(user.email, user.password)
        def establishmentEvent = fixtureCreator.createEstablishmentEvent(userEstablishment)
        def id = establishmentEvent.id

        when:
        def result = this.mvc.perform(
                delete('/establishments/me/event-fees/{id}?access_token={access_token}', id, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'me event should be found'() {
        given:
        String accessToken = getUserAccessToken(user.email, user.password)
        def establishmentEvent = fixtureCreator.createEstablishmentEvent(userEstablishment)
        def id = establishmentEvent.id

        when:
        def result = this.mvc
                .perform(get('/establishments/me/event-fees/{id}/?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.value', is(notNullValue())))
    }

    void 'all may event-fees should be found'() {
        given:
        String accessToken = getUserAccessToken(user.email, user.password)
        fixtureCreator.createEstablishmentEvent(userEstablishment)

        when:
        def result = this.mvc.perform(get('/establishments/me/event-fees?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].value', is(notNullValue())))
    }

}
