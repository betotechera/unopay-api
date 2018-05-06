package br.com.unopay.api.market.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.market.model.ContractorBonus
import br.com.unopay.api.market.service.ContractorBonusService
import br.com.unopay.api.model.Person
import br.com.unopay.api.model.Product
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ContractorBonusControllerTest extends AuthServerApplicationTests {


    @Autowired
    private ContractorBonusService contractorBonusService;

    @Autowired
    private FixtureCreator fixtureCreator

    private Contractor contractorUnderTest
    private Person personUnderTest
    private Product productUnderTest

    void setup() {
        productUnderTest
        contractorUnderTest = fixtureCreator.createContractor()
        personUnderTest = contractorUnderTest.person
        productUnderTest = fixtureCreator.createProduct()
    }

    void 'all Contractor Bonus should be found'() {

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        contractorBonusService.save(contractorBonus)
        String accessToken = getUserAccessToken()

        when:
        def result = this.mvc.perform(get('/contractor-bonuses?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].id', is(notNullValue())))

    }

    void 'known Contractor Bonus should be found'(){

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        contractorBonusService.save(contractorBonus)
        String accessToken = getUserAccessToken()
        def id = contractorBonus.id

        when:
        def result = this.mvc.perform(get('/contractor-bonuses/{id}?access_token={access_token}', id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.contractor', is(notNullValue())))
    }

    private ContractorBonus createContractorBonus() {
        ContractorBonus contractorBonus = Fixture.from(ContractorBonus.class).gimme("valid")
        contractorBonus = contractorBonus.with {
            product = productUnderTest
            contractor = contractorUnderTest
            payer = personUnderTest
            it
        }
        contractorBonus
    }
}
