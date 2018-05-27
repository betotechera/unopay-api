package br.com.unopay.api.market.service

import java.time.Year
import java.util.Date

import br.com.unopay.api.config.Queues
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.market.model.ContractorBonus
import br.com.unopay.api.market.model.filter.BonusBillingFilter
import br.com.unopay.api.{ScalaApplicationTest, util}
import br.com.unopay.bootcommons.exception.{NotFoundException, UnprocessableEntityException}
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.scalatest.mockito._
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConverters._

class BonusBillingServiceTest extends ScalaApplicationTest with MockitoSugar {

    @Autowired
    var service: BonusBillingService = _
    @Autowired
    var fixtureCreator: util.FixtureCreatorScala = _
    var mockNotifier : Notifier = _

    override def beforeEach(): Unit = {
        super.beforeEach()
        mockNotifier = mock[Notifier]
        service.notifier = mockNotifier
    }

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

    "given valid BonusBilling" should "define it's number" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.number = null
        val created = service.create(bonusBilling)
        created.number
    }

    "given valid BonusBilling" should "define it's expiration date" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.expiration = null
        val created = service.create(bonusBilling)
        created.expiration
    }

    "given BonusBilling without issuer" should "return error" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.issuer = null

        val thrown = the[UnprocessableEntityException] thrownBy {
            service.create(bonusBilling)
        }

        thrown.getErrors.asScala.head.getLogref == "ISSUER_REQUIRED"
    }

    "given valid Bonus to process" should "process bonus billing" in {
        fixtureCreator.createPersistedContractorBonusForContractor()

        service.process()

        verify(mockNotifier).notify(Queues.BONUS_BILLING_CREATED, _:Object)
    }

    "given person without Bonus to process" should "not create BonusBilling" in {
        val contractor = fixtureCreator.createContractor()

        val filter = new BonusBillingFilter
        filter.setDocument(contractor.getDocumentNumber)

        service.process(contractor.getPerson)

        val found = service.findByFilter(filter, new UnovationPageRequest)

        found.getSize == 0
    }

    "given person with Bonus to process" should "create BonusBilling that belongs to it" in {
        val bonus = fixtureCreator.createPersistedContractorBonusForContractor()
        val payer = bonus.getPayer
        fixtureCreator.createPersistedContractorBonusForContractor(bonus.getContractor)
        val filter = new BonusBillingFilter
        filter.setDocument(payer.documentNumber())

        service.process(payer)

        val found = service.findByFilter(filter, new UnovationPageRequest)

        found.getSize == 1
        found.getContent.get(0).payer.equals(payer)
    }

    "given person with Bonuses to process" should "create BonusBilling with total amount of Bonuses values" in {
        val bonus = fixtureCreator.createPersistedContractorBonusForContractor()
        val total = bonus.getEarnedBonus
        val payer = bonus.getPayer
        total.add(fixtureCreator.createPersistedContractorBonusForContractor(bonus.getContractor).getEarnedBonus)
        val filter = new BonusBillingFilter
        filter.setDocument(payer.documentNumber())

        service.process(payer)

        val found = service.findByFilter(filter, new UnovationPageRequest)

        found.getSize == 1
        found.getContent.get(0).total.equals(total.doubleValue())
    }

    "given contractor with Bonuses to process" should "create BonusBilling with total amount of Bonuses values" in {
        val contractor = fixtureCreator.createContractor()
        val bonus = fixtureCreator.createPersistedContractorBonusForContractor(contractor)
        val total = bonus.getEarnedBonus
        total.add(fixtureCreator.createPersistedContractorBonusForContractor(bonus.getContractor).getEarnedBonus)
        val filter = new BonusBillingFilter
        filter.setDocument(contractor.getPerson.documentNumber())

        service.processForContractor(contractor.getId)

        val found = service.findByFilter(filter, new UnovationPageRequest)

        found.getSize == 1
        found.getContent.get(0).total.equals(total.doubleValue())
    }

    "given unknown contractor" should "not process BonusBillings" in {
        val contractorId = "123"

        val thrown = the [NotFoundException] thrownBy {
            service.processForContractor(contractorId)
        }
        thrown.getErrors.asScala.head.getLogref == "CONTRACTOR_NOT_FOUND"
    }

    "when processing bonus to process" should "create bonus billing" in {
        val contractor = fixtureCreator.createContractor()
        fixtureCreator.createPersistedContractorBonusForContractor(contractor)

        val filter = new BonusBillingFilter
        filter.setDocument(contractor.getDocumentNumber)

        service.process()

        val found = service.findByFilter(filter, new UnovationPageRequest)
        found
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
