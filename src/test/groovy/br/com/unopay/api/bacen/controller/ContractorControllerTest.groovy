package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.six2six.fixturefactory.function.impl.RegexFunction
import br.com.unopay.api.bacen.model.AuthorizedMember
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.billing.boleto.model.Ticket
import br.com.unopay.api.billing.creditcard.model.Transaction
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.Person
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.uaa.AuthServerApplicationTests
import br.com.unopay.api.uaa.model.UserDetail
import org.springframework.security.crypto.password.PasswordEncoder

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ContractorControllerTest extends AuthServerApplicationTests {
    private static final String CONTRACTOR_ENDPOINT = '/contractors?access_token={access_token}'
    private static final String CONTRACTOR_ID_ENDPOINT = '/contractors/{id}?access_token={access_token}'


    @Autowired
    FixtureCreator fixtureCreator

    @Autowired
    ContractorInstrumentCreditService contractorInstrumentCreditService

    @Autowired
    PasswordEncoder passwordEncoder

    void 'should create contractor'() {
        given:
            String accessToken = getUserAccessToken()
        when:
            def result = this.mvc.perform(postHired(accessToken, getContractor()))
        then:
            result.andExpect(status().isCreated()).andExpect(header().string("Location", is(notNullValue())))
    }

    MockHttpServletRequestBuilder postHired(String accessToken, Contractor contractor) {
        post(CONTRACTOR_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON).content(toJson(contractor))
    }

    void 'known contractor should be deleted'() {
        given:
        String accessToken = getUserAccessToken()
        def mvcResult = this.mvc.perform(postHired(accessToken, getContractor())).andReturn()
        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete(CONTRACTOR_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known contractor should be updated'() {
        given:
        String accessToken = getUserAccessToken()
        def mvcResult = this.mvc.perform(postHired(accessToken, getContractor())).andReturn()
        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put(CONTRACTOR_ID_ENDPOINT,id, accessToken)
                .content(toJson(contractor.with { id= extractId(location);person.name = 'updated';it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    String extractId(String location) {
        location.replaceAll('/contractors/', "")
    }

    String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }

    void 'known contractor should be found'() {
        given:
            String accessToken = getUserAccessToken()
            Contractor contractor = getContractor()
            def mvcResult = this.mvc.perform(postHired(accessToken, contractor)).andReturn()
            def location = getLocationHeader(mvcResult)
            def id = extractId(location)
        when:
            def result = this.mvc.perform(get(CONTRACTOR_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.name', is(equalTo(contractor.person.name))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.person.document.number', is(equalTo(contractor.person.document.number))))
    }

    void 'known contractor should be found when find all'() {
        given:
            String accessToken = getUserAccessToken()
            this.mvc.perform(postHired(accessToken, getContractor()))

            this.mvc.perform(post(CONTRACTOR_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(contractor.with { person.id = null; person.name = 'temp';person.document.number = '1234576777';it })))
        when:
            def result = this.mvc.perform(get("$CONTRACTOR_ENDPOINT",accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(greaterThan(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].person', is(notNullValue())))
    }

    void 'known contractor credits should be found when find credits'() {
        given:
        String accessToken = getUserAccessToken()
        def instrumentCredit = fixtureCreator.instrumentCredit()
        contractorInstrumentCreditService.insert(instrumentCredit.paymentInstrumentId, instrumentCredit)
        def document = instrumentCredit.contract.contractor.documentNumber
        def contractId = instrumentCredit.contract.id

        when:
        def result = this.mvc.perform(get("/contractors/{document}/credits?contractId={contractId}&access_token={access_token}",document,contractId, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].paymentInstrument', is(notNullValue())))
    }

    void 'known transactions should be returned'() {
        given:
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        def user = fixtureCreator.createUser(person.physicalPersonDetail.email)
        String accessToken = getUserAccessToken(user.email, user.password)

        Order order = fixtureCreator.createPersistedAdhesionOrder(person)

        Fixture.from(Transaction.class).uses(jpaProcessor).gimme(2, "valid", new Rule(){{
            add("orderId", order.id)
        }})

        when:
        def result = this.mvc.perform(get('/contractors/me/transactions?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].orderId', is(notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[1].orderId', is(notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(2))))
    }

    void 'should return my transaction by orderId'() {
        given:
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")

        def user = fixtureCreator.createUser(person.physicalPersonDetail.email)
        String accessToken = getUserAccessToken(user.email, user.password)

        Order orderA = fixtureCreator.createPersistedAdhesionOrder(person)
        Order orderB = fixtureCreator.createPersistedAdhesionOrder(person)

        Fixture.from(Transaction.class).uses(jpaProcessor).gimme(2, "valid", new Rule(){{
            add("orderId", uniqueRandom(orderA.id, orderB.id))
        }})

        def orderId = orderA.id

        when:
        def result = this.mvc.perform(get('/contractors/me/transactions?access_token={access_token}&orderId={orderId}',accessToken, orderId)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].orderId', is(equalTo(orderId))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
    }

    void 'known boletos should be returned'() {
        given:
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def user = fixtureCreator.createUser(person.physicalPersonDetail.email)
        String accessToken = getUserAccessToken(user.email, user.password)

        Order order = fixtureCreator.createPersistedAdhesionOrder(person)

        Fixture.from(Ticket.class).uses(jpaProcessor).gimme(2, "valid", new Rule(){{
            add("sourceId", order.id)
        }})

        when:
        def result = this.mvc.perform(get('/contractors/me/boletos?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].value', is(notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[1].value', is(notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(2))))
    }

    void 'should return my boleto by orderId'() {
        given:
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def user = fixtureCreator.createUser(person.physicalPersonDetail.email)
        String accessToken = getUserAccessToken(user.email, user.password)

        Order orderA = fixtureCreator.createPersistedAdhesionOrder(person)
        Order orderB = fixtureCreator.createPersistedAdhesionOrder(person)

        Fixture.from(Ticket.class).uses(jpaProcessor).gimme(2, "valid", new Rule(){{
            add("sourceId", uniqueRandom(orderA.id,orderB.id))
        }})

        def sourceId = orderA.id

        when:
        def result = this.mvc.perform(get('/contractors/me/boletos?access_token={access_token}&orderId={sourceId}',accessToken, sourceId)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].value', is(notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
    }

    void 'should not return boleto with unkown sourceId'() {
        given:
        Person person = Fixture.from(Person.class).uses(jpaProcessor).gimme("physical")
        def user = fixtureCreator.createUser(person.physicalPersonDetail.email)
        String accessToken = getUserAccessToken(user.email, user.password)

        Order orderA = fixtureCreator.createPersistedAdhesionOrder(person)
        Order orderB = fixtureCreator.createPersistedAdhesionOrder(person)

        Fixture.from(Ticket.class).uses(jpaProcessor).gimme(2, "valid", new Rule(){{
            add("sourceId", uniqueRandom(orderA.id,orderB.id))
        }})

        def sourceId = 'unknown'

        when:
        def result = this.mvc.perform(get('/contractors/me/boletos?access_token={access_token}&orderId={sourceId}',accessToken, sourceId)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.items', is(notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(0))))
    }

    void 'known contractor instruments should be found when find instruments'() {
        given:
        String accessToken = getUserAccessToken()
        PaymentInstrument instrumentCredit =  Fixture.from(PaymentInstrument.class).uses(jpaProcessor).gimme("valid")
        def document = instrumentCredit.contractor.documentNumber

        when:
        def result = this.mvc.perform(get("/contractors/{document}/payment-instruments?&access_token={access_token}",document, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].product', is(notNullValue())))
    }

    void 'should find my authorizedMember'() {
        given:
        def contractorUser = fixtureCreator.createContractorUser()
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember(contractorUser.contractor)
        def id = authorizedMember.id
        String accessToken = getUserAccessToken(contractorUser.email, contractorUser.password)
        when:
        def result = this.mvc.perform(get("/contractors/me/authorized-members/{id}?access_token={access_token}",id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.name', is(equalTo(authorizedMember.name))))
    }

    void 'should update my authorizedMember'() {
        given:
        def contractorUser = fixtureCreator.createContractorUser()
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember(contractorUser.contractor)
        def id = authorizedMember.id
        authorizedMember.name = "new name"
        String accessToken = getUserAccessToken(contractorUser.email, contractorUser.password)
        when:
        def result = this.mvc.perform(put("/contractors/me/authorized-members/{id}?access_token={access_token}",id, accessToken)
                .content(toJson(authorizedMember))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    Contractor getContractor() {
        Fixture.from(Contractor.class).gimme("valid")
    }
}
