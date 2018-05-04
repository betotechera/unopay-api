package br.com.unopay.api.market.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.market.model.ContractorBonus
import br.com.unopay.api.model.Person
import br.com.unopay.api.model.Product
import br.com.unopay.bootcommons.exception.NotFoundException
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

    def 'known Contractor Bonus should be updated'() {

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        contractorBonus.earnedBonus = 99.99
        ContractorBonus saved = contractorBonusService.save(contractorBonus)

        BigDecimal newEarnedBonus = 1.99
        contractorBonus.earnedBonus = newEarnedBonus

        when:
        contractorBonusService.update(saved.id, contractorBonus)
        ContractorBonus updated = contractorBonusService.findById(saved.id)

        then:
        updated.earnedBonus == newEarnedBonus

    }

    def 'known Contractor Bonus should be deleted'() {

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        ContractorBonus saved = contractorBonusService.save(contractorBonus)

        when:
        contractorBonusService.delete(saved.id)
        contractorBonusService.findById(saved.id)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACTOR_BONUS_NOT_FOUND'
    }

    def 'unknown Contractor Bonus should not be deleted'(){

        when:
        contractorBonusService.delete('')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACTOR_BONUS_NOT_FOUND'
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
