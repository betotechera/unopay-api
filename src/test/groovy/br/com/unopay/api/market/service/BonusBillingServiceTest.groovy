package br.com.unopay.api.market.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired

import java.time.Year

class BonusBillingServiceTest extends SpockApplicationTests {

    @Autowired
    private BonusBillingService service
    @Autowired
    private FixtureCreator fixtureCreator

    void 'should save valid BonusBilling'(){
        given:
        def bonusBilling = fixtureCreator.createBonusBillingToPersist()
        when:
        def result = service.save(bonusBilling)
        then:
        result
    }

    void 'given valid BonusBilling should create it'(){
        given:
        def bonusBilling = fixtureCreator.createBonusBillingToPersist()
        when:
        def created = service.create(bonusBilling)
        then:
        created
    }

    void 'given BonusBilling with unknown person should return error'(){
        given:
        def bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.person.id = '123'
        when:
        service.create(bonusBilling)
        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'PERSON_NOT_FOUND'
    }

    void 'given BonusBilling without person should return error'(){
        given:
        def bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.person = null
        when:
        service.create(bonusBilling)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'PERSON_REQUIRED'
    }

    void 'given BonusBilling without total should return error'(){
        given:
        def bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.total = null
        when:
        service.create(bonusBilling)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'BONUS_BILLING_TOTAL_REQUIRED'
    }

    void 'given BonusBilling with invalid process date should return error'(){
        given:
        def bonusBilling = fixtureCreator.createBonusBillingToPersist()

        bonusBilling.processedAt = new Date(Year.now().value + 4, 12, 12)
        when:
        service.create(bonusBilling)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'INVALID_BONUS_BILLING_PROCESS_DATE'
    }

    void 'given BonusBilling with invalid bonus expiration date should return error'(){
        given:
        def bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.expiration = new Date('12/12/2015')
        when:
        service.create(bonusBilling)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'INVALID_BONUS_BILLING_EXPIRATION_DATE'
    }
}
