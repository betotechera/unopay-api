package br.com.unopay.api.market.service

import java.util.Calendar

import br.com.unopay.api.UnopayApiScalaApplicationTest
import br.com.unopay.api.config.Queues
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.market.model.BonusSituation
import br.com.unopay.api.market.model.filter.BonusBillingFilter
import br.com.unopay.api.market.repository.ContractorBonusBillingRepository
import br.com.unopay.api.order.model.PaymentStatus
import br.com.unopay.bootcommons.exception.{NotFoundException, UnprocessableEntityException}
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.mockito.Mockito.verify
import org.scalatest.mockito._
import org.springframework.beans.factory.annotation.{Autowired, Value}

import scala.collection.JavaConverters._

class BonusBillingServiceTest extends UnopayApiScalaApplicationTest with MockitoSugar {

    @Autowired
    var service: BonusBillingService = _
    @Autowired
    var contractorBonusService: ContractorBonusService =_

    @Autowired
    var contractorBonusBillingRepository: ContractorBonusBillingRepository = _

    var mockNotifier : Notifier = _

    @Value("${unopay.boleto.deadline_in_days}")
    private var deadlineInDays :Int =_

    override def beforeEach(): Unit = {
        super.beforeEach()
        mockNotifier = mock[Notifier]
        service.notifier = mockNotifier
    }

    it should "save valid BonusBilling" in{
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
         val result = service.save(bonusBilling)
        result should be
    }

    "given valid BonusBilling" should "create it" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
        val created = service.create(bonusBilling)
        created should be
    }

    "given valid BonusBilling" should "define it's number" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.number = null
        val created = service.create(bonusBilling)

        val found = service.findById(created.id)

        found.number should be
    }

    "given valid BonusBilling" should "define it's expiration date" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.expiration = null
        val expectedDate = Calendar.getInstance()
        expectedDate.add(Calendar.DATE, deadlineInDays)

        val created = service.create(bonusBilling)

        created.expiration should be equals expectedDate.getTime
    }

    "given BonusBilling without issuer" should "return error" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.issuer = null

        val thrown = the[UnprocessableEntityException] thrownBy {
            service.create(bonusBilling)
        }

        thrown.getErrors.asScala.head.getLogref should be equals "ISSUER_REQUIRED"
    }

    "given valid Bonus to process" should "process bonus billing" in {
        fixtureCreator.createPersistedContractorBonusForContractor()

        service.process()

        verify(mockNotifier).notify(Queues.BONUS_BILLING_CREATED, _:Object)
    }

    "given issuer whose contractors has bonuses to process" should "create bonus billing" in {
        val issuer = fixtureCreator.createIssuer()
        val product = fixtureCreator.createProductWithIssuer(issuer)
        val documentNumber = fixtureCreator.createPersistedContractorBonusWithProduct(product)
            .getPayer.documentNumber()

        val filter = new BonusBillingFilter
        filter.setDocument(documentNumber)

        service.processForIssuer(issuer.getId)

        val found = service.findByFilter(filter, new UnovationPageRequest)
        found.getContent should not be empty
        found.getContent.asScala.head.issuer should be equals issuer
    }

    "given issuer whose contractors have no bonuses to process" should "not create bonus billing" in {
        val issuer = fixtureCreator.createIssuer()
        val product = fixtureCreator.createProductWithIssuer(issuer)
        val documentNumber = fixtureCreator.createPersistedContractorBonusWithProduct(product)
            .getContractor.getDocumentNumber

        val filter = new BonusBillingFilter
        filter.setDocument(documentNumber)

        service.processForIssuer(issuer.getId)

        val found = service.findByFilter(filter, new UnovationPageRequest)
        found.getContent shouldBe empty
    }

    "given unknown issuer to process bonus" should "return error" in {
        val thrown = the[NotFoundException] thrownBy {
            service.processForIssuer("213")
        }

        thrown.getErrors.asScala.head.getLogref shouldEqual "ISSUER_NOT_FOUND"
    }

    "given person without Bonus to process" should "not create BonusBilling" in {
        val contractor = fixtureCreator.createContractor()

        val filter = new BonusBillingFilter
        filter.setDocument(contractor.getDocumentNumber)

        service.process(contractor.getPerson)

        val found = service.findByFilter(filter, new UnovationPageRequest)

        found.getContent shouldBe empty
    }

    "when processing Bonus" should "set bonus status to TICKET_ISSUED" in {
        val bonus = fixtureCreator.createPersistedContractorBonusForContractor()
        val payer = bonus.getPayer

        service.process(payer)

        val found = contractorBonusService.findById(bonus.getId)

        found.getSituation shouldEqual BonusSituation.TICKET_ISSUED
    }

    "when processing Bonus" should "add it to bonusBilling" in {
        val bonus = fixtureCreator.createPersistedContractorBonusForContractor()
        val payer = bonus.getPayer
        fixtureCreator.createPersistedContractorBonusForContractor(bonus.getContractor)

        service.process(payer)

        val found = contractorBonusBillingRepository.findByContractorBonusId(bonus.getId)

        found should not be empty
        found should have size 1
        found.asScala.head.bonusBilling.payer.documentNumber shouldEqual payer.documentNumber
    }


    "given person with Bonus to process" should "create BonusBilling that belongs to it" in {
        val bonus = fixtureCreator.createPersistedContractorBonusForContractor()
        val payer = bonus.getPayer
        fixtureCreator.createPersistedContractorBonusForContractor(bonus.getContractor)
        val filter = new BonusBillingFilter
        filter.setDocument(payer.documentNumber())

        service.process(payer)

        val found = service.findByFilter(filter, new UnovationPageRequest)

        found.getContent should not be empty
        found.getContent.asScala.head.payer should be equals payer
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

        found.getContent should not be empty
        found.getContent.asScala.head.total should be equals total
    }

    "when processing bonus to process" should "create bonus billing" in {
        val contractor = fixtureCreator.createContractor()
        fixtureCreator.createPersistedContractorBonusForContractor(contractor)

        val filter = new BonusBillingFilter
        filter.setDocument(contractor.getDocumentNumber)

        service.process()

        val found = service.findByFilter(filter, new UnovationPageRequest)
        found should be
    }

    "given BonusBilling with unknown person" should "return error" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.payer.setId("123")

        val thrown = the [NotFoundException] thrownBy {
            service.create(bonusBilling)
        }
        thrown.getErrors.asScala.head.getLogref shouldEqual "PERSON_NOT_FOUND"
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
        thrown.getErrors.asScala.head.getLogref shouldEqual "BONUS_BILLING_TOTAL_REQUIRED"
    }

    "given BonusBilling with invalid process date" should "return error" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
        val date = Calendar.getInstance()
        date.add(Calendar.DATE, 1)
        bonusBilling.processedAt = date.getTime
        val thrown = the [UnprocessableEntityException] thrownBy {
            service.create(bonusBilling)
        }
        thrown.getErrors.asScala.head.getLogref shouldEqual "INVALID_BONUS_BILLING_PROCESS_DATE"
    }

    it should "find known BonusBilling" in {
        val id = fixtureCreator.createPersistedBonusBilling().getId
        val found = service.findById(id)
        found should be
    }

    "given known BonusBilling" should "process as paid" in {
        val bonusBilling = fixtureCreator.createBonusBillingToPersist()
        bonusBilling.processedAt = null
        bonusBilling.status = PaymentStatus.WAITING_PAYMENT
        val number = service.create(bonusBilling).number

        service.processAsPaid(number)
        val found = service.findByNumber(number)

        found.processedAt should be
        found.status should be(PaymentStatus.PAID)
    }

    it should "find issuer's BonusBilling" in {
        val contractor = fixtureCreator.createContractor()
        val issuer = fixtureCreator.createIssuer()
        val id = fixtureCreator.createPersistedBonusBilling(contractor.getPerson, issuer).getId

        val found = service.findByIdForIssuer(id, issuer)

        found should be
    }

    it should "not find unknown BonusBilling" in {
        val id = "123"
        val thrown = the [NotFoundException] thrownBy {
            service.findById(id)
        }
        thrown.getErrors.asScala.head.getLogref shouldEqual "BONUS_BILLING_NOT_FOUND"
    }

     it should "not delete unknown BonusBilling" in {
        val id = "123"
         val thrown = the [NotFoundException] thrownBy {
             service.delete(id)
         }
         thrown.getErrors.asScala.head.getLogref shouldEqual "BONUS_BILLING_NOT_FOUND"
    }

    it should "delete known BonusBilling" in {
        val id = fixtureCreator.createPersistedBonusBilling().id
        service.delete(id)
        val thrown = the [NotFoundException] thrownBy {
            service.findById(id)
        }
        thrown.getErrors.asScala.head.getLogref shouldEqual "BONUS_BILLING_NOT_FOUND"
    }

    it should "find BonusBilling by filter" in {
        val document = fixtureCreator.createPersistedBonusBilling().payer.documentNumber()
        val page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        val filter = new BonusBillingFilter()
        filter.setDocument(document)
        val bonus = service.findByFilter(filter, page)
        bonus.asScala.head should be
    }
}
