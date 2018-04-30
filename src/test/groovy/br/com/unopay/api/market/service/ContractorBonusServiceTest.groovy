package br.com.unopay.api.market.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.market.model.ContractorBonus
import br.com.unopay.api.model.Person
import br.com.unopay.api.model.Product
import org.springframework.beans.factory.annotation.Autowired

class ContractorBonusServiceTest extends SpockApplicationTests {

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

    def 'given a valid Contractor Bonus should be saved'() {

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        contractorBonus.validateMe()

        when:
        ContractorBonus saved = contractorBonusService.save(contractorBonus)
        ContractorBonus found = contractorBonusService.findById(saved.id)

        then:
        found

    }

    private ContractorBonus createContractorBonus() {
        ContractorBonus contractorBonus = Fixture.from(ContractorBonus.class).gimme("valid")
        contractorBonus = contractorBonus.with {
            product = productUnderTest
            contractor = contractorUnderTest
            person = personUnderTest
            it
        }
        contractorBonus
    }
}
