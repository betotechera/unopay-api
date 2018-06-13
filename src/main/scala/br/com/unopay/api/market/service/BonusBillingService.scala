package br.com.unopay.api.market.service

import java.math._
import java.util.Date
import javax.transaction.Transactional

import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.service.{ContractorService, IssuerService}
import br.com.unopay.api.config.Queues
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.market.model._
import br.com.unopay.api.market.model.filter.BonusBillingFilter
import br.com.unopay.api.market.repository.{BonusBillingRepository, ContractorBonusBillingRepository}
import br.com.unopay.api.model.Person
import br.com.unopay.api.order.model.PaymentStatus
import br.com.unopay.api.service.PersonService
import br.com.unopay.api.uaa.exception.Errors
import br.com.unopay.api.util.Logging
import br.com.unopay.bootcommons.exception.UnovationExceptions
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.data.domain.{Page, PageRequest}
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._

@Service
@Autowired
class BonusBillingService(repository: BonusBillingRepository,
                          personService: PersonService,
                          bonusService: ContractorBonusService,
                          var notifier: Notifier,
                          issuerService: IssuerService,
                          contractorService: ContractorService,
                          contractorBonusBillingRepository: ContractorBonusBillingRepository) extends Logging{

    @Value("${unopay.boleto.deadline_in_days}")
    private var deadlineInDays : Int =_

    def create(bonusBilling: BonusBilling): BonusBilling = {
        bonusBilling.validateMe()
        validateReferences(bonusBilling)
        defineNumber(bonusBilling)
        defineExpirationDate(bonusBilling)
        save(bonusBilling)
    }

    def getContractorBonusesByBonusBillingId(id: String): java.util.Set[ContractorBonus] = {
        val contractorBonuses = contractorBonusBillingRepository.findByBonusBillingId(id).asScala.map(_.contractorBonus)
        if (contractorBonuses.isEmpty) {
            throw UnovationExceptions.notFound.withErrors(Errors.CONTRACTOR_BONUS_NOT_FOUND)
        }
        contractorBonuses.toSet.asJava
    }

    private def defineNumber(bonusBilling: BonusBilling) {
        val lastNumber = repository.findFirstByOrderByCreatedDateTimeDesc().map[String](_.getNumber).orElse(null)
        bonusBilling.defineNumber(lastNumber)
    }

    private def defineExpirationDate(bonusBilling: BonusBilling) = {
        val date = new DateTime().plusDays(deadlineInDays).toDate
        bonusBilling.setExpiration(date)
    }

    def save(bonusBilling: BonusBilling): BonusBilling = {
        repository.save(bonusBilling)
    }

    def processForIssuer(id: String) = {
        issuerService.findById(id)
        def payers = bonusService.getPayersWithBonusToProcessForIssuer(id)
        payers.forEach(process(_))
    }

    def process() {
        def payers = bonusService.getPayersWithBonusToProcess
        payers.forEach(payer => process(payer))
    }

    @Transactional
    def process(payer: Person) {
        val bonuses = bonusService.getBonusesToProcessForPayer(payer.documentNumber).asScala
        bonuses.map(_.issuerId).distinct.map(issuerService.findById).foreach(issuer => {
            val bonusesByIssuer = bonuses.filter(_.hasIssuer(issuer))
            processIssuerBonuses(payer, issuer, bonusesByIssuer.toSet)
        })
    }

    private def processIssuerBonuses(payer: Person, issuer: Issuer, bonuses: Set[ContractorBonus]) {
        log.info("creating bonus billing to payer={}", payer.documentNumber())
        val earnedBonus = bonuses.map(_.getEarnedBonus).fold(BigDecimal.ZERO)(_.add(_))
        var bonusBilling = create(payer, issuer, earnedBonus)
        bonusBilling = save(bonusBilling)
        bonuses.foreach(bonus => createContractorBonusBilling(bonusBilling, bonus))
        bonuses.foreach(updateBonusStatus)
        notifier.notify(Queues.BONUS_BILLING_CREATED, bonusBilling)
    }

    private def createContractorBonusBilling(bonusBilling: BonusBilling, contractorBonus: ContractorBonus) = {
        val contractorBonusBilling = new ContractorBonusBilling
        contractorBonusBilling.bonusBilling = bonusBilling
        contractorBonusBilling.contractorBonus = contractorBonus
        contractorBonusBillingRepository.save(contractorBonusBilling)
    }

    private def create(payer: Person, issuer: Issuer, total: BigDecimal): BonusBilling = {
        val bonusBilling = new BonusBilling
        bonusBilling.setMeUp(payer, issuer, total)
        create(bonusBilling)
    }

    private def updateBonusStatus(bonus: ContractorBonus){
        bonus.setSituation(BonusSituation.TICKET_ISSUED)
        bonusService.update(bonus.getId, bonus)
    }

    private def validateReferences(bonusBilling: BonusBilling) {
        bonusBilling.setPayer(personService.findById(bonusBilling.personId()))
        bonusBilling.setIssuer(issuerService.findById(bonusBilling.issuerId()))
    }

    def processAsPaid(billingId: String) = {
        val current = findById(billingId)
        current.processedAt = new Date()
        current.setStatus(PaymentStatus.PAID)
        save(current)
    }

    def findByIdForIssuer(id: String, issuer: Issuer): BonusBilling = {
        val bonusBilling = repository.findByIdAndIssuerId(id, issuer.getId)
        bonusBilling.orElseThrow(() => UnovationExceptions.notFound().withErrors(
            Errors.BONUS_BILLING_NOT_FOUND))
    }

    def findById(id: String): BonusBilling = {
        val bonusBilling = repository.findById(id)
        bonusBilling.orElseThrow(() => UnovationExceptions.notFound().withErrors(
            Errors.BONUS_BILLING_NOT_FOUND))
    }

    def findByFilter(filter: BonusBillingFilter, pageable: UnovationPageRequest): Page[BonusBilling] = {
        repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()))
    }

    def delete(id: String) {
        findById(id)
        repository.delete(id)
    }
}
