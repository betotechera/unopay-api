package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.bacen.model.AccreditedNetworkIssuer
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.service.IssuerService
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.job.UnopayScheduler
import br.com.unopay.api.market.model.ContractorBonus
import br.com.unopay.api.market.model.filter.BonusBillingFilter
import br.com.unopay.api.market.service.BonusBillingService
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.Product
import br.com.unopay.api.uaa.AuthServerApplicationTests
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest

import static org.hamcrest.Matchers.equalTo
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

class IssuerControllerTest extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator

    @Autowired
    IssuerService issuerService

    UnopayScheduler schedulerMock = Mock(UnopayScheduler)

    @Autowired
    BonusBillingService bonusBillingService

    void setup(){
        issuerService.scheduler = schedulerMock
    }

    void 'valid issuer should be created'() {
        given:
        String accessToken = getUserAccessToken()
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        def result = this.mvc.perform(post('/issuers?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(issuer)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known issuer should be updated'() {
        given:
        String accessToken = getUserAccessToken()
        def issuer = fixtureCreator.createIssuer()
        def id = issuer.id

        when:
        def result = this.mvc.perform(put('/issuers/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(issuer.with { fee = 0.3d ; person.id = '1'; paymentAccount.id = '1'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known issuer should be deleted'() {
        given:
        String accessToken = getUserAccessToken()
        def issuer = fixtureCreator.createIssuer()
        def id = issuer.id

        when:
        def result = this.mvc.perform(delete('/issuers/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known issuers should be found'() {
        given:
        String accessToken = getUserAccessToken()
        def issuer = fixtureCreator.createIssuer()
        def id = issuer.id

        when:
        def result = this.mvc.perform(get('/issuers/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.fee', is(notNullValue())))
    }

    void 'all issuers should be found'() {
        given:
        String accessToken = getUserAccessToken()
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        this.mvc.perform(post('/issuers?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(issuer)))

        when:
        def result = this.mvc.perform(get('/issuers?access_token={access_token}', getClientAccessToken())
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].id', is(notNullValue())))
    }

    void 'known me issuer should be updated'() {
        given:
        def issuerUser = fixtureCreator.createIssuerUser()
        String accessToken = getUserAccessToken(issuerUser.email, issuerUser.password)

        when:
        def result = this.mvc.perform(put('/issuers/me?access_token={access_token}', accessToken)
                .content(toJson(issuerUser.issuer.with { fee = 0.3d ; person.id = '1'; paymentAccount.id = '1'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known me issuer should be found'() {
        given:
        def issuerUser = fixtureCreator.createIssuerUser()
        String accessToken = getUserAccessToken(issuerUser.email, issuerUser.password)

        when:
        def result = this.mvc.perform(get('/issuers/me?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.fee', is(notNullValue())))
    }

    void 'valid me product should be created'() {
        given:
        def issuerUser = fixtureCreator.createIssuerUser()
        String accessToken = getUserAccessToken(issuerUser.email, issuerUser.password)
        Product product = Fixture.from(Product.class).gimme("valid", new Rule() {{
                add("accreditedNetwork", fixtureCreator.createNetwork())
                add("paymentRuleGroup", fixtureCreator.createPaymentRuleGroup())
        }})

        when:
        def result = this.mvc.perform(post('/issuers/me/products?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(product)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known me product should be updated'() {
        given:
        def issuerUser = fixtureCreator.createIssuerUser()
        String accessToken = getUserAccessToken(issuerUser.email, issuerUser.password)
        Product product = fixtureCreator.createProductWithIssuer(issuerUser.issuer)
        def id = product.id
        when:
        def result = this.mvc.perform(put('/issuers/me/products/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(product.with { name = '56456'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known me product should be deleted'() {
        given:
        def issuerUser = fixtureCreator.createIssuerUser()
        String accessToken = getUserAccessToken(issuerUser.email, issuerUser.password)
        Product product = fixtureCreator.createProductWithIssuer(issuerUser.issuer)
        def id = product.id
        when:
        def result = this.mvc.perform(delete('/issuers/me/products/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'should process my contractor bonuses'() {
        given:
        def issuerUser = fixtureCreator.createIssuerUser()
        String accessToken = getUserAccessToken(issuerUser.email, issuerUser.password)

        when:
        def result = this.mvc.perform(put('/issuers/me/bonus-billings?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isNoContent())
    }

    void 'should find my bonus billing'() {
        given:
        def issuerUser = fixtureCreator.createIssuerUser()
        def contractor = fixtureCreator.createContractor()
        String accessToken = getUserAccessToken(issuerUser.email, issuerUser.password)
        def id = fixtureCreator.createPersistedBonusBilling(contractor.getPerson(), issuerUser.issuer).id

        when:
        def result = this.mvc.perform(get('/issuers/me/bonus-billings/{id}?access_token={access_token}', id,accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.id', is(notNullValue())))
    }

    void 'should find all my bonus billings'() {
        given:
        def issuerUser = fixtureCreator.createIssuerUser()
        def contractor = fixtureCreator.createContractor()
        fixtureCreator.createPersistedBonusBilling(contractor.getPerson(), issuerUser.issuer)
        String accessToken = getUserAccessToken(issuerUser.email, issuerUser.password)

        when:
        def result = this.mvc.perform(get('/issuers/me/bonus-billings?access_token={access_token}',accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].id', is(notNullValue())))
    }

    void 'known network should be enabled for me'(){
        given:
        def network = fixtureCreator.createNetwork()
        def issuerUser = fixtureCreator.createIssuerUser()
        String accessToken = getUserAccessToken(issuerUser.email, issuerUser.password)
        def networkIssuer = new AccreditedNetworkIssuer()
        networkIssuer.setAccreditedNetwork(network)
        when:
        def result = this.mvc.perform(post('/issuers/me/accredited-networks?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(networkIssuer)))
        then:
        result.andExpect(status().isCreated())
    }


    void 'should return all me networks'(){
        given:
        def issuerUser = fixtureCreator.createIssuerUser()
        Fixture.from(AccreditedNetworkIssuer).uses(jpaProcessor).gimme(2,"valid", new Rule(){{
            add("issuer", issuerUser.issuer)
        }})
        String accessToken = getUserAccessToken(issuerUser.email, issuerUser.password)

        when:
        def result = this.mvc.perform(get('/issuers/me/accredited-networks?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(2))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].id', is(notNullValue())))
    }

    void 'known me products should be found'() {
        given:
        def issuerUser = fixtureCreator.createIssuerUser()
        String accessToken = getUserAccessToken(issuerUser.email, issuerUser.password)
        Product product = fixtureCreator.createProductWithIssuer(issuerUser.issuer)
        def id = product.id
        when:
        def result = this.mvc.perform(get('/issuers/me/products/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.name', is(notNullValue())))
    }
    void 'known me contracts should be found'() {
        given:
        def issuerUser = fixtureCreator.createIssuerUser()
        String accessToken = getUserAccessToken(issuerUser.email, issuerUser.password)
        Product product = fixtureCreator.createProductWithIssuer(issuerUser.issuer)
        def contractor = fixtureCreator.createContractor()
        fixtureCreator.createPersistedContract(contractor, product)
        fixtureCreator.createPersistedContract(contractor, product)
        when:
        def result = this.mvc.perform(get('/issuers/me/contracts?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].id', is(notNullValue())))
    }

    void 'known given known issuer should find its contracts'() {
        given:
        def issuer = fixtureCreator.createIssuer()
        String accessToken = getUserAccessToken()
        Product product = fixtureCreator.createProductWithIssuer(issuer)
        def contractor = fixtureCreator.createContractor()
        fixtureCreator.createPersistedContract(contractor, product)
        fixtureCreator.createPersistedContract(contractor, product)
        when:
        def result = this.mvc.perform(get('/issuers/{id}/contracts?access_token={access_token}', issuer.id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].id', is(notNullValue())))
    }
}
