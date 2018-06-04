package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.market.model.BonusSituation
import br.com.unopay.api.market.model.ContractorBonus
import br.com.unopay.api.model.ServiceAuthorize
import br.com.unopay.api.uaa.AuthServerApplicationTests
import br.com.unopay.api.uaa.model.UserDetail

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

import static org.hamcrest.core.IsNull.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class EstablishmentControllerTest extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator

    AccreditedNetwork networkUnderTest

    void setup(){
        networkUnderTest = fixtureCreator.createNetwork()
    }

    void 'valid establishment should be created'() {
        given:
        String accessToken = getUserAccessToken()
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
                                                                                .with { network = networkUnderTest; it }

        when:
        def result = this.mvc.perform(post('/establishments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(establishment)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known establishment should be updated'() {
        given:
        String accessToken = getUserAccessToken()
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
                                                                    .with { network = networkUnderTest; it }
        def mvcResult = this.mvc.perform(post('/establishments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(establishment))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put('/establishments/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(establishment.with {
            id= extractId(location);  fee = 0.3d ; person.id = '1'; person.document.number = '11114444555786'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known establishment should be deleted'() {
        given:
        String accessToken = getUserAccessToken()
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
                                                                            .with { network = networkUnderTest; it }
        def mvcResult = this.mvc.perform(post('/establishments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(establishment))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete('/establishments/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known establishments should be found'() {
        given:
        String accessToken = getUserAccessToken()
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
                                                                            .with { network = networkUnderTest; it }
        def mvcResult = this.mvc.perform(post('/establishments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(establishment)))
                .andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(get('/establishments/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.fee', is(notNullValue())))
    }

    void 'all establishments should be found'() {
        given:
        String accessToken = getUserAccessToken()
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
                                                                            .with { network = networkUnderTest; it }
        this.mvc.perform(post('/establishments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(establishment)))

        when:
        def result = this.mvc.perform(get('/establishments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].id', is(notNullValue())))
    }


    void 'valid service should be created'() {
        given:
        def establishmentUser = fixtureCreator.createEstablishmentUser()
        String accessToken = getUserAccessToken(establishmentUser.email, establishmentUser.password)
        ServiceAuthorize service = fixtureCreator
                .createServiceAuthorize(fixtureCreator.createContractorInstrumentCreditPersisted(),
                establishmentUser.establishment)
        fixtureCreator.createNegotiation(service.getContract().getHirer(), service.getContract().product)

        when:
        def result = this.mvc.perform(
                post('/establishments/me/service-authorizations?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(service.with {authorizeEvents.find().id = null; it })))
        then:
        result.andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers
                .jsonPath('$.establishment.id', is(equalTo(establishmentUser.establishment.id))))
    }


    void 'known services should be found'() {
        given:
        def establishmentUser = fixtureCreator.createEstablishmentUser()
        String accessToken = getUserAccessToken(establishmentUser.email, establishmentUser.password)
        ServiceAuthorize service = fixtureCreator
                .createServiceAuthorizePersisted(fixtureCreator.createContractorInstrumentCreditPersisted(),
                establishmentUser.establishment)
        def id = service.id

        when:
        def result = this.mvc.perform(
                get('/establishments/me/service-authorizations/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.authorizationNumber', is(notNullValue())))
    }

    void 'known Contractor Bonus should be found'() {

        given:
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        UserDetail establishmentUser = fixtureCreator.createEstablishmentUser(establishment)
        String accessToken = getUserAccessToken(establishmentUser.email, establishmentUser.password)
        ContractorBonus contractorBonus = fixtureCreator.createPersistedContractorBonusForPerson(establishment.person)
        String id = contractorBonus.id

        when:
        def result = this.mvc
                .perform(get('/establishments/me/contractor-bonuses/{id}?access_token={access_token}'
                ,id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                .jsonPath('$.earnedBonus', is(notNullValue())))

    }

    void 'should return all me Contractor Bonus'(){

        given:
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        UserDetail establishmentUser = fixtureCreator.createEstablishmentUser(establishment)
        String accessToken = getUserAccessToken(establishmentUser.email, establishmentUser.password)
        fixtureCreator.createPersistedContractorBonusForPerson(establishment.person)
        fixtureCreator.createPersistedContractorBonusForPerson(establishment.person)

        when:
        def result = this.mvc
                .perform(get('/establishments/me/contractor-bonuses?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(2))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].id', is(notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[1].id', is(notNullValue())))

    }

    void 'valid Contractor Bonus should be created'() {
        given:
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        UserDetail establishmentUser = fixtureCreator.createEstablishmentUser(establishment)
        String accessToken = getUserAccessToken(establishmentUser.email, establishmentUser.password)
        ContractorBonus contractorBonus = Fixture.from(ContractorBonus.class).gimme("valid", new Rule(){{
            add("contractor", fixtureCreator.createContractor())
            add("product", fixtureCreator.createProduct())
        }})

        when:
        def result = this.mvc.perform(
                post('/establishments/me/contractor-bonuses?access_token={access_token}', accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(contractorBonus)))

        then:
        result.andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers
                .jsonPath('$.payer.id', is(equalTo(establishmentUser.establishment.person.id))))
    }

    void 'known Contractor Bonus should be updated'() {
        given:
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        UserDetail establishmentUser = fixtureCreator.createEstablishmentUser(establishment)
        String accessToken = getUserAccessToken(establishmentUser.email, establishmentUser.password)
        ContractorBonus contractorBonus = fixtureCreator.createPersistedContractorBonusForPerson(establishment.person)
        String id = contractorBonus.id

        when:
        def result = this.mvc
                .perform(put('/establishments/me/contractor-bonuses/{id}?access_token={access_token}'
                ,id, accessToken)
                .content(toJson(contractorBonus.with { situation = BonusSituation.CANCELED; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'should process establishment bonus billings'() {
        given:
        String accessToken = getUserAccessToken()
        def id = fixtureCreator.createEstablishment().id

        when:
        def result = this.mvc.perform(put('/establishments/{id}/bonus-billings?access_token={access_token}', id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'should process my bonus billings'() {
        given:
        def establishmentUser = fixtureCreator.createEstablishmentUser()
        String accessToken = getUserAccessToken(establishmentUser.email, establishmentUser.password)

        when:
        def result = this.mvc.perform(put('/establishments/me/bonus-billings?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    String extractId(String location) {
        location.replaceAll('/establishments/', "")
    }

}
