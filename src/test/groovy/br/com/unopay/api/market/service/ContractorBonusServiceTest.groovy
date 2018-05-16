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
        ContractorBonus created = contractorBonusService.create(contractorBonus)

        BigDecimal newEarnedBonus = 1.99
        contractorBonus.earnedBonus = newEarnedBonus

        when:
        contractorBonusService.update(created.id, contractorBonus)
        ContractorBonus updated = contractorBonusService.findById(created.id)

        then:
        updated.earnedBonus == newEarnedBonus

    }

    def 'known Contractor Bonus should be deleted'() {

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        ContractorBonus created = contractorBonusService.create(contractorBonus)

        when:
        contractorBonusService.delete(created.id)
        contractorBonusService.findById(created.id)

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

    def 'given a Contractor Bonus without creation date should be created'() {

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        contractorBonus.createdDateTime = null

        when:
        ContractorBonus created = contractorBonusService.create(contractorBonus)
        ContractorBonus found = contractorBonusService.findById(created.id)

        then:
        timeComparator.compare(found.createdDateTime, new Date()) == 0

    }

    def 'known Contractor Bonus should be found for its Contractor'(){

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        Contractor contractor = contractorBonus.contractor
        ContractorBonus created = contractorBonusService.create(contractorBonus)

        when:
        ContractorBonus found = contractorBonusService.findByIdForContractor(created.id, contractor)

        then:
        created.id == found.id

    }

    def 'known Contractor Bonus for a different Contractor should return error'() {

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        Contractor contractor = contractorBonus.contractor
        Contractor differentContractor = fixtureCreator.createContractor()
        ContractorBonus created = contractorBonusService.create(contractorBonus)

        when:
        contractorBonusService.findByIdForContractor(created.id, differentContractor)

        then:
        contractor != differentContractor
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACTOR_BONUS_NOT_FOUND'

    }

    def 'known Contractor Bonus should be found for its Person'(){

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        Person person = contractorBonus.payer
        ContractorBonus created = contractorBonusService.create(contractorBonus)

        when:
        ContractorBonus found = contractorBonusService.findByIdForPerson(created.id, person)

        then:
        created.id == found.id

    }

    def 'should find payers with Contractor Bonuses for Processing'(){

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        contractorBonusService.create(contractorBonus)

        when:
        def found = contractorBonusService.getPayersWithBonusToProcess()

        then:
        !found.isEmpty()

    }

    def 'should find Contractor Bonuses for Processing for payers'(){

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        def document = contractorBonus.payer.documentNumber()
        contractorBonusService.create(contractorBonus)

        when:
        def found = contractorBonusService.getBonusesToProcessForPayer(document)

        then:
        !found.isEmpty()

    }

    def 'known Contractor Bonus for a different Person should return error'() {

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        Person person = contractorBonus.payer
        Person differentPerson = fixtureCreator.createContractor().person
        ContractorBonus created = contractorBonusService.create(contractorBonus)

        when:
        contractorBonusService.findByIdForPerson(created.id, differentPerson)

        then:
        person != differentPerson
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
