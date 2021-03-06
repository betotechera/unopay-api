package br.com.unopay.api.network.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.market.model.AuthorizedMember
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.Product
import br.com.unopay.api.network.model.AccreditedNetwork
import br.com.unopay.api.network.model.Branch
import br.com.unopay.api.network.model.Establishment
import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AccreditedNetworkControllerTest extends AuthServerApplicationTests {
    private static final String ACCREDITED_NETWORK_ENDPOINT = '/accredited-networks?access_token={access_token}'
    private static final String ACCREDITED_NETWORK_ID_ENDPOINT = '/accredited-networks/{id}?access_token={access_token}'

    @Autowired
    FixtureCreator fixtureCreator

    void 'should create accreditedNetwork'() {
        given:
            String accessToken = getUserAccessToken()
        when:
            def result = this.mvc.perform(postAccreditedNetwork(accessToken, getAccreditedNetwork()))
        then:
            result.andExpect(status().isCreated()).andExpect(header().string("Location", is(notNullValue())))
    }

    MockHttpServletRequestBuilder postAccreditedNetwork(String accessToken, AccreditedNetwork accreditedNetwork) {
        post(ACCREDITED_NETWORK_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON)
                .content(toJson(accreditedNetwork))
    }

    void 'known accreditedNetwork should be deleted'() {
        given:
        String accessToken = getUserAccessToken()
        def network = fixtureCreator.createNetwork()
        def id = network.id
        when:
        def result = this.mvc.perform(delete(ACCREDITED_NETWORK_ID_ENDPOINT,id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known accreditedNetwork should be updated'() {
        given:
        String accessToken = getUserAccessToken()
        def network = fixtureCreator.createNetwork()
        def id = network.id
        when:
        def result = this.mvc.perform(put(ACCREDITED_NETWORK_ID_ENDPOINT,id, accessToken)
                .content(toJson(accreditedNetwork.with { person.name = 'updated';it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known accreditedNetwork should be found'() {
        given:
        String accessToken = getUserAccessToken()
        def network = fixtureCreator.createNetwork()
        def id = network.id

        when:
        def result = this.mvc.perform(get(ACCREDITED_NETWORK_ID_ENDPOINT,id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.name', is(equalTo(network.person.name))))
                .andExpect(MockMvcResultMatchers
                .jsonPath('$.person.document.number', is(equalTo(network.person.document.number))))
    }

    void 'known accreditedNetwork should be found when find all'() {
        given:
            String accessToken = getUserAccessToken()
            this.mvc.perform(postAccreditedNetwork(accessToken, getAccreditedNetwork()))

            this.mvc.perform(post(ACCREDITED_NETWORK_ENDPOINT, accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                    toJson(accreditedNetwork.with {
                        person.id = null
                        person.name = 'temp'
                        person.document.number = '1234576777'
                        it })))
        when:
            def result = this.mvc.perform(get("$ACCREDITED_NETWORK_ENDPOINT",getClientAccessToken())
                    .contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(greaterThan(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].person', is(notNullValue())))
    }

    void 'known me accreditedNetwork should be updated'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)

        when:
        def result = this.mvc.perform(put("/accredited-networks/me?access_token={access_token}", accessToken)
                .content(toJson(accreditedNetwork.with { person.name = 'updated';it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known me accreditedNetwork should be found'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)
        def person = accreditedNetworkUser.accreditedNetwork.person

        when:
        def result = this.mvc.perform(get("/accredited-networks/me?access_token={access_token}", accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.name', is(equalTo(person.name))))
                .andExpect(MockMvcResultMatchers
                .jsonPath('$.person.document.number', is(equalTo(person.document.number))))
    }

    void 'known establishments should be found'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)
        Establishment establishment = fixtureCreator.createEstablishment(accreditedNetworkUser.accreditedNetwork)

        def id = establishment.id
        when:
        def result = this.mvc.perform(
                get('/accredited-networks/me/establishments/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.fee', is(notNullValue())))
    }

    void 'all establishments should be found'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)
        fixtureCreator.createEstablishment(accreditedNetworkUser.accreditedNetwork)

        when:
        def result = this.mvc.perform(
                get('/accredited-networks/me/establishments?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].id', is(notNullValue())))
    }

    void 'valid branch to a logged network should be created'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)
        Establishment establishment = fixtureCreator.createEstablishment(accreditedNetworkUser.accreditedNetwork)
        Branch branch = Fixture.from(Branch.class).gimme("valid", new Rule(){{
            add("headOffice", establishment)
            add("services", [fixtureCreator.createService()])
        }})

        when:
        def result = this.mvc.perform(post('/accredited-networks/me/establishments/branches?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(branch)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known branch to a logged network should be updated'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)
        Establishment establishment = fixtureCreator.createEstablishment(accreditedNetworkUser.accreditedNetwork)
        Branch branch = Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("headOffice", establishment)
        }})
        def id = branch.id

        when:
        def result = this.mvc.perform(put('/accredited-networks/me/establishments/branches/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(branch.with { name = '155564'; fantasyName = '11114444555786'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known branches  to a logged network should be found'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)
        Establishment establishment = fixtureCreator.createEstablishment(accreditedNetworkUser.accreditedNetwork)
        Branch branch = Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("headOffice", establishment)
        }})
        def id = branch.id
        when:
        def result = this.mvc.perform(get('/accredited-networks/me/establishments/branches/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.id', is(notNullValue())))
    }

    void 'all branches  to a logged network should be found'() {
        given:
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser()
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)
        Establishment establishment = fixtureCreator.createEstablishment(accreditedNetworkUser.accreditedNetwork)
        Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("headOffice", establishment)
        }})

        when:
        def result = this.mvc.perform(get('/accredited-networks/me/establishments/branches?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].id', is(notNullValue())))
    }

    void "create a scheduling for accredited network"() {
        given:
        def contract = fixtureCreator.createPersistedContract()
        def network = contract.getProduct().accreditedNetwork
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser(network)
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)

        Scheduling scheduling = fixtureCreator.createSchedulingPersisted(contract)

        when:
        def result = this.mvc.perform(post('/accredited-networks/me/establishments/schedules')
                .param("access_token", accessToken)
                .content(toJson(scheduling))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isCreated())
    }

    void "find a scheduling for accredited network"() {
        given:
        def contract = fixtureCreator.createPersistedContract()
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser(contract.getProduct().accreditedNetwork)
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)

        Scheduling scheduling = fixtureCreator.createSchedulingPersisted(contract)

        when:
        def result = this.mvc.perform(get('/accredited-networks/me/establishments/schedules/{id}', scheduling.id)
                .param("access_token", accessToken))
        then:
        result.andExpect(status().isOk())
            .andExpect(jsonPath('$.token', notNullValue()))
            .andExpect(jsonPath('$.id', notNullValue()))
            .andExpect(jsonPath('$.date', notNullValue()))
    }

    void "update a scheduling for accredited network"() {
        given:
        def contract = fixtureCreator.createPersistedContract()

        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser(contract.getProduct().accreditedNetwork)

        Scheduling actualScheduling = fixtureCreator.createSchedulingPersisted(contract, accreditedNetworkUser)
        Scheduling otherScheduling = fixtureCreator.createSchedulingToPersist(contract, accreditedNetworkUser)

        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)

        when:
        def result = this.mvc.perform(put('/accredited-networks/me/establishments/schedules/{id}', actualScheduling.id)
                .param("access_token", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(otherScheduling)))

        then:
        result.andExpect(status().isNoContent())
    }

    void "cancel a scheduling for accredited network"() {
        given:
        def contract = fixtureCreator.createPersistedContract()

        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser(contract.getProduct().accreditedNetwork)
        Scheduling scheduling = fixtureCreator.createSchedulingPersisted(contract, accreditedNetworkUser)
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)

        when:
        def result = this.mvc.perform(delete('/accredited-networks/me/establishments/schedules/{id}', scheduling.id)
                .param("access_token", accessToken))

        then:
        result.andExpect(status().isNoContent())
    }

    void "search a scheduling for accredited network"() {
        given:
        def contract = fixtureCreator.createPersistedContract()

        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser(contract.getProduct().accreditedNetwork)
        Scheduling scheduling = fixtureCreator.createSchedulingPersisted(contract, accreditedNetworkUser)
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)

        when:
        def result = this.mvc.perform(get('/accredited-networks/me/establishments/schedules')
                .param("access_token", accessToken)
                .param("token", scheduling.token))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items[0].token', notNullValue()))
                .andExpect(jsonPath('$.items[0].id', notNullValue()))
                .andExpect(jsonPath('$.total', notNullValue()))
    }

    void "find a contractor for an accredited network"() {
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        def loggedNetwork = contract.getProduct().getAccreditedNetwork()
        def contractorId = contract.getContractor().getId()
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser(loggedNetwork)
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)

        when:
        def result = this.mvc.perform(get('/accredited-networks/me/contractors/{id}', contractorId)
                .param("access_token", accessToken))
        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.id', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.name',
                        is(equalTo(contract.contractor.person.name))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.document.number',
                        is(equalTo(contract.contractor.person.document.number))))
    }

    void "should not find a contractor with unknown id for an accredited network"() {
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        def loggedNetwork = contract.getProduct().getAccreditedNetwork()
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser(loggedNetwork)
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)

        when:
        def result = this.mvc.perform(get('/accredited-networks/me/contractors/{id}', "0110")
                .param("access_token", accessToken))
        then:
        result.andExpect(status().isNotFound())
    }

    void "should not find a contractor if he dont belongs to the logged network"() {
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        def loggedNetwork = fixtureCreator.createNetwork()
        def contractorId = contract.getContractor().getId()
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser(loggedNetwork)
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)

        when:
        def result = this.mvc.perform(get('/accredited-networks/me/contractors/{id}', contractorId)
                .param("access_token", accessToken))

        then:
        result.andExpect(status().isNotFound())
    }
    
    AccreditedNetwork getAccreditedNetwork() {
        Fixture.from(AccreditedNetwork.class).gimme("valid")
    }

    void 'should find contractors when find all for a logged network'() {
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        def loggedNetwork = contract.getProduct().getAccreditedNetwork()
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser(loggedNetwork)
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)

        when:
        def result = this.mvc.perform(get("/accredited-networks/me/contractors?access_token={access_token}",
                                          accessToken)
                                            .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(greaterThan(0))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].person', is(notNullValue())))
    }

    void 'should not find contractors for a unlogged network'() {
        when:
        def result = this.mvc.perform(get("/accredited-networks/me/contractors?access_token={access_token}",
                getClientAccessToken())
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isForbidden())
    }

    void 'should find payment instrument when find all for a logged network'() {
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        PaymentInstrument paymentinstrument = Fixture.from(PaymentInstrument.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("product", contract.getProduct())
        }})
        def loggedNetwork = paymentinstrument.getProduct().getAccreditedNetwork()
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser(loggedNetwork)
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)

        when:
        def result = this.mvc.perform(get("/accredited-networks/me/payment-instruments?access_token={access_token}",
                accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(greaterThan(0))))
    }

    void 'should not find payment instrument for a unlogged network'() {
        when:
        def result = this.mvc.perform(get("/accredited-networks/me/payment-instruments?access_token={access_token}",
                getClientAccessToken())
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isForbidden())
    }

    void 'should find contract that belongs to a contractor when find all for a logged network'() {
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        def id = contract.contractorId()
        def loggedNetwork = contract.productNetwork()
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser(loggedNetwork)
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)

        when:
        def result = this.mvc.perform(get("/accredited-networks/me/contractors/{id}/contracts?access_token={access_token}",
                        id, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(greaterThan(0))))
    }

    void 'should not find contract for a unknown contractor when find all for a logged network'() {
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        def loggedNetwork = contract.productNetwork()
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser(loggedNetwork)
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)

        when:
        def result = this.mvc.perform(get("/accredited-networks/me/contractors/{id}/contracts?access_token={access_token}",
                "00000", accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNotFound())
    }

    void 'should not find contract for a contractor when find all for a unlogged network'() {
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        def id = contract.contractorId()

        when:
        def result = this.mvc.perform(get("/accredited-networks/me/contractors/{id}/contracts?access_token={access_token}",
                id, getClientAccessToken())
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isForbidden())
    }

    void 'should find authorized member when find all for a logged network'() {
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        AuthorizedMember authorizedMember = Fixture.from(AuthorizedMember.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("contract", contract)
        }})

        def loggedNetwork = contract.productNetwork()
        def accreditedNetworkUser = fixtureCreator.createAccreditedNetworkUser(loggedNetwork)
        String accessToken = getUserAccessToken(accreditedNetworkUser.email, accreditedNetworkUser.password)

        when:
        def result = this.mvc.perform(get("/accredited-networks/me/authorized-members?access_token={access_token}",
                accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(greaterThan(0))))
    }

    void 'should not find authorized member when find all for a unlogged network'() {
        when:
        def result = this.mvc.perform(get("/accredited-networks/me/authorized-members?access_token={access_token}",
                getClientAccessToken())
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isForbidden())
    }

}
