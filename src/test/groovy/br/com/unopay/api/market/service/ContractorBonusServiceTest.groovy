package br.com.unopay.api.market.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.network.model.Establishment
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.market.model.BonusSituation
import br.com.unopay.api.market.model.ContractorBonus
import br.com.unopay.api.model.Person
import br.com.unopay.api.model.Product
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class ContractorBonusServiceTest extends SpockApplicationTests {

    @Autowired
    private ContractorBonusService contractorBonusService;

    @Autowired
    private FixtureCreator fixtureCreator

    private Contractor contractorUnderTest
    private Person personUnderTest
    private Product productUnderTest
    private Establishment establishmentUnderTest

    private static BonusSituation FOR_PROCESSING = BonusSituation.FOR_PROCESSING
    private static BonusSituation PROCESSED = BonusSituation.PROCESSED
    private static BonusSituation CANCELED = BonusSituation.CANCELED

    void setup() {
        contractorUnderTest = fixtureCreator.createContractor()
        personUnderTest = contractorUnderTest.person
        productUnderTest = fixtureCreator.createProduct()
        establishmentUnderTest = fixtureCreator.createEstablishment()
    }

    def 'given a valid Contractor Bonus should be saved'() {

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        contractorBonus.setupMyCreate()
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

    def 'should find payers with Contractor Bonuses for Processing by Issuer'(){

        given:
        def issuer = fixtureCreator.createIssuer()
        def product = fixtureCreator.createProductWithIssuer(issuer)
        ContractorBonus contractorBonus = createContractorBonus(product)
        contractorBonusService.create(contractorBonus)

        when:
        def found = contractorBonusService.getPayersWithBonusToProcessForIssuer(issuer.id)

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
        found.first().situation == BonusSituation.FOR_PROCESSING
    }

    def "should not find Contractor Bonuses for Processing for payers if there ain't no one"(){
        given:
        ContractorBonus contractorBonus = Fixture
                .from(ContractorBonus.class).uses(jpaProcessor).gimme("valid", new Rule(){{
                add("product",productUnderTest)
                add("contractor",contractorUnderTest)
                add("payer",personUnderTest)
                add("situation", BonusSituation.TICKET_ISSUED)
        }})
        def document = contractorBonus.payer.documentNumber()

        when:
        def found = contractorBonusService.getBonusesToProcessForPayer(document)

        then:
        found.isEmpty()
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

    def 'when create bonus situation should be set as For Processing'() {

        given:
        ContractorBonus contractorBonus = createContractorBonus()

        when:
        ContractorBonus created = contractorBonusService.create(contractorBonus)
        ContractorBonus found = contractorBonusService.findById(created.id)

        then:
        found.situation == FOR_PROCESSING

    }

    def 'when create bonus processedAt should be set as Null'() {

        given:
        ContractorBonus contractorBonus = createContractorBonus()

        when:
        ContractorBonus created = contractorBonusService.create(contractorBonus)
        ContractorBonus found = contractorBonusService.findById(created.id)

        then:
        found.processedAt == null

    }

    def 'when create for Establishment situation should be set as For Processing'() {

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        Establishment establishment = establishmentUnderTest

        when:
        ContractorBonus created = contractorBonusService.createForEstablishment(establishment, contractorBonus)
        ContractorBonus found = contractorBonusService.findById(created.id)

        then:
        found.situation == FOR_PROCESSING

    }

    def 'when create for Establishment processedAt should be set as Null'() {

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        Establishment establishment = establishmentUnderTest
        contractorBonus.processedAt = new Date()

        when:
        ContractorBonus created = contractorBonusService.createForEstablishment(establishment, contractorBonus)
        ContractorBonus found = contractorBonusService.findById(created.id)

        then:
        found.processedAt == null

    }

        def 'when create bonus the known Establishment should be Contractor Bonus payer'() {

        given:
        ContractorBonus contractorBonus = createContractorBonus()
        contractorBonus.payer = null
        Establishment establishment = establishmentUnderTest

        when:
        ContractorBonus created = contractorBonusService.createForEstablishment(establishment, contractorBonus)
        ContractorBonus found = contractorBonusService.findByIdForPerson(created.id, establishment.person)

        then:
        found.payer == establishment.person

    }

    def 'known Contractor Bonus should be updated for Establishment person'(){

        given:
        Establishment establishment = fixtureCreator.createEstablishment()
        ContractorBonus contractorBonus = createContractorBonus()
        contractorBonus.payer = establishment.person
        ContractorBonus created = contractorBonusService.create(contractorBonus)
        BonusSituation situation = CANCELED
        contractorBonus.situation = situation

        when:
        ContractorBonus result = contractorBonusService.updateForEstablishment(created.id, establishment, contractorBonus)

        then:
        result.situation == situation
    }

    @Unroll
    def 'Update for Establishment must be For Processing to Canceled'() {

        given:
        Establishment establishment = fixtureCreator.createEstablishment()
        ContractorBonus contractorBonus = createContractorBonus()
        contractorBonus.payer = establishment.person
        ContractorBonus current = contractorBonusService.create(contractorBonus)
        contractorBonus.situation = situation
        contractorBonus.processedAt = processedAt
        current.updateOnly(contractorBonus, "situation", "processedAt")
        contractorBonusService.save(current)
        ContractorBonus bonus = createContractorBonus()
        bonus.situation = updateSituation
        bonus.processedAt = newProcessedAt

        when:
        contractorBonusService.updateForEstablishment(current.id, establishment, bonus)

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'INVALID_BONUS_SITUATION'

        where:
        situation      | updateSituation | processedAt | newProcessedAt
        FOR_PROCESSING | PROCESSED       | null        | new Date()
        PROCESSED      | FOR_PROCESSING  | new Date()  | null
        PROCESSED      | CANCELED        | new Date()  | null
        CANCELED       | FOR_PROCESSING  | null        | null
        CANCELED       | PROCESSED       | null        | new Date()

    }

    def 'create Contractor Bonus without earnedBonus should set it up'() {
        given:
        ContractorBonus contractorBonus = createContractorBonus()
        contractorBonus.earnedBonus = null
        Product product = Fixture.from(Product).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("bonusPercentage", Math.random())
        }})
        contractorBonus.product = product

        when:
        ContractorBonus create = contractorBonusService.create(contractorBonus)
        ContractorBonus found = contractorBonusService.findById(create.id)

        then:
        found.earnedBonus
    }

        private ContractorBonus createContractorBonus(issuerProduct = productUnderTest) {
        return Fixture.from(ContractorBonus.class).gimme("valid", new Rule(){{
            add("product",issuerProduct)
            add("contractor",contractorUnderTest)
            add("payer",personUnderTest)
        }})
    }
}
