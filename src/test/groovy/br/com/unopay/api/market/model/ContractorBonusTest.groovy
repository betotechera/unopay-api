package br.com.unopay.api.market.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.util.Rounder
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import spock.lang.Unroll

class ContractorBonusTest extends FixtureApplicationTest {

    def 'should be equals'() {

        given:
        ContractorBonus a = Fixture.from(ContractorBonus.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(ContractorBonus.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }

    @Unroll
    def 'when calling validateProcessedAtWhenSituationProcessed with value "#blankOrNull" should return error'() {

        given:
        Date invalidDate = blankOrNull
        ContractorBonus contractorBonus = Fixture.from(ContractorBonus.class).gimme("valid", new Rule(){{
            add("situation", BonusSituation.PROCESSED)
            add("processedAt", invalidDate)
        }})

        when:
        contractorBonus.validateProcessedAtWhenSituationProcessed()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_PROCESSED_AT'

        where:
        _ | blankOrNull
        _ | null
    }

    @Unroll
    def 'when calling validateSituationWhenProcessedAtNotNull with value "#nullOrNotProcessed" should return error'() {

        given:
        BonusSituation invalidSituation = nullOrNotProcessed
        ContractorBonus contractorBonus = Fixture.from(ContractorBonus.class).gimme("valid", new Rule(){{
            add("situation", invalidSituation)
            add("processedAt", new Date())
        }})

        when:
        contractorBonus.validateSituationWhenProcessedAtNotNull()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_BONUS_SITUATION'

        where:
        _ | nullOrNotProcessed
        _ | null
        _ | BonusSituation.CANCELED
        _ | BonusSituation.FOR_PROCESSING
    }

    def 'when calling validateServiceValue without serviceValue should return error'() {

        given:
        ContractorBonus contractorBonus = Fixture.from(ContractorBonus).gimme("valid", new Rule(){{
            add("serviceValue", null)
        }})

        when:
        contractorBonus.validateServiceValue()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.find()?.logref == 'INVALID_SERVICE_VALUE'
    }

    def 'when calling setupEarnedBonusIfNull without earnedBonus should set it up'() {

        given:
        ContractorBonus contractorBonus = Fixture.from(ContractorBonus).gimme("withoutBonus")
        contractorBonus.product.bonusPercentage = Math.random()

        when:
        contractorBonus.setupEarnedBonus()

        then:
        contractorBonus.earnedBonus == Rounder.round(contractorBonus.product.returnBonusPercentage()
                .multiply(contractorBonus.serviceValue))
    }
}