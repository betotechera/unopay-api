package br.com.unopay.api.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.Product
import br.com.unopay.api.uaa.AuthServerApplicationTests
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

class ContractControllerTest extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator

    Hirer hirerUnderTest

    Contractor contractorUnderTest

    Product productUnderTest

    void setup(){
        hirerUnderTest = fixtureCreator.createHirer()
        productUnderTest = fixtureCreator.createProduct()
        contractorUnderTest = fixtureCreator.createContractor()
    }

    void 'valid contract should be created'() {
        given:
        String accessToken = getUserAccessToken()
        Contract contract = createContract()

        when:
        def result = this.mvc.perform(post('/contracts?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(contract)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known contract should be updated'() {
        given:
        String accessToken = getUserAccessToken()
        Contract contract = createContract()

        def mvcResult = this.mvc.perform(post('/contracts?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(contract))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put('/contracts/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(contract.with { id = extractId(location);  name = '56456'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known contract should be deleted'() {
        given:
        String accessToken = getUserAccessToken()
        Contract contract = createContract()

        def mvcResult = this.mvc.perform(post('/contracts?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(contract))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete('/contracts/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known contracts should be found'() {
        given:
        String accessToken = getUserAccessToken()
        Contract contract = createContract()

        def mvcResult = this.mvc.perform(post('/contracts?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(contract)))
                .andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(get('/contracts/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.name', is(notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath('$.contractInstallments', is(notNullValue())))
    }

    private String extractId(String location) {
        location.replaceAll('/contracts/', "")
    }


    private Contract createContract() {
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            contractor = contractorUnderTest
            hirer = hirerUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceTypes
            it
        }
        contract
    }
}
