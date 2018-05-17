package br.com.unopay.api.market.service

import java.time.Year
import java.util.Date

import br.com.unopay.api.market.model.filter.BonusBillingFilter
import br.com.unopay.api.{ScalaApplicationTest, util}
import br.com.unopay.bootcommons.exception.{NotFoundException, UnprocessableEntityException}
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConverters._

class BonusBillingServiceTest extends ScalaApplicationTest {

    @Autowired
    var service: BonusBillingService = _
    @Autowired
    var fixtureCreator: util.FixtureCreatorScala = _

    it should "save valid BonusBilling" in{
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
         val result = service.save(bonusBilling)
        result
    }

    "given valid BonusBilling" should "create it" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
        val created = service.create(bonusBilling)
        created
    }

    "given BonusBilling without issuer" should "return error" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.issuer = null

        val thrown = the[UnprocessableEntityException] thrownBy {
            service.create(bonusBilling)
        }

        thrown.getErrors.asScala.head.getLogref == "ISSUER_REQUIRED"
    }

    it should "AS create it" in {
        fixtureCreator.createPersistedContractorBonusForContractor()

        service.process()
    }

    "given BonusBilling with unknown person" should "return error" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.payer.setId("123")

        val thrown = the [NotFoundException] thrownBy {
            service.create(bonusBilling)
        }
        thrown.getErrors.asScala.head.getLogref == "PERSON_NOT_FOUND"
    }

     "given BonusBilling without person" should "return error" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.payer = null
         val thrown = the [UnprocessableEntityException] thrownBy {
             service.create(bonusBilling)
         }
         thrown.getErrors.asScala.head.getLogref == "PERSON_REQUIRED"
    }

    "given BonusBilling without total" should "return error" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.total = null
        val thrown = the [UnprocessableEntityException] thrownBy {
            service.create(bonusBilling)
        }
        thrown.getErrors.asScala.head.getLogref == "BONUS_BILLING_TOTAL_REQUIRED"
    }

    "given BonusBilling with invalid process date" should "return error" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()

        bonusBilling.processedAt = new Date(Year.now().getValue + 4, 12, 12)
        val thrown = the [UnprocessableEntityException] thrownBy {
            service.create(bonusBilling)
        }
        thrown.getErrors.asScala.head.getLogref == "INVALID_BONUS_BILLING_PROCESS_DATE"
    }

    it should "find known BonusBilling" in {
        val id = fixtureCreator.createPersistedBonusBilling().getId
        val found = service.findById(id)
        found
    }

    it should "not find unknown BonusBilling" in {
        val id = "123"
        val thrown = the [NotFoundException] thrownBy {
            service.findById(id)
        }
        thrown.getErrors.asScala.head.getLogref == "BONUS_BILLING_NOT_FOUND"
    }

     it should "not delete unknown BonusBilling" in {
        val id = "123"
         val thrown = the [NotFoundException] thrownBy {
             service.delete(id)
         }
         thrown.getErrors.asScala.head.getLogref == "BONUS_BILLING_NOT_FOUND"
    }

    it should "delete known BonusBilling" in {
        val id = fixtureCreator.createPersistedBonusBilling().id
        service.delete(id)
        val thrown = the [NotFoundException] thrownBy {
            service.findById(id)
        }
        thrown.getErrors.asScala.head.getLogref == "BONUS_BILLING_NOT_FOUND"
    }

    it should "find BonusBilling by filter" in {
        val document = fixtureCreator.createPersistedBonusBilling().payer.documentNumber()
        val page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        val filter = new BonusBillingFilter()
        filter.setDocument(document)
        val bonus = service.findByFilter(filter, page)
        bonus.asScala.head
    }
}
