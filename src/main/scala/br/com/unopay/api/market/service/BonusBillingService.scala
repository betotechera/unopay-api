package br.com.unopay.api.market.service

import br.com.unopay.api.bacen.service.IssuerService
import java.math._
import java.util.{Calendar, Date}

import br.com.unopay.api.billing.boleto.service.TicketService
import br.com.unopay.api.config.Queues
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.market.model.BonusBilling
import br.com.unopay.api.market.model.filter.BonusBillingFilter
import br.com.unopay.api.market.repository.BonusBillingRepository
import br.com.unopay.api.model.Person
import br.com.unopay.api.notification.service.NotificationService
import br.com.unopay.api.service.PersonService
import br.com.unopay.api.uaa.exception.Errors
import br.com.unopay.bootcommons.exception.UnovationExceptions
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.data.domain.{Page, PageRequest}
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._

@Service
@Autowired
class BonusBillingService(repository: BonusBillingRepository, personService: PersonService, bonusService: ContractorBonusService, var notifier: Notifier, issuerService: IssuerService) {

    @Value("${unopay.boleto.deadline_in_days}")
    private var deadlineInDays :Int =_

    def create(bonusBilling: BonusBilling): BonusBilling = {
        bonusBilling.validateMe()
        validateReferences(bonusBilling)
        defineNumber(bonusBilling)
        defineExpirationDate(bonusBilling)
        save(bonusBilling)
    }

    private def defineNumber(bonusBilling: BonusBilling) {
        val found = repository.findFirstByOrderByCreatedDateTimeDesc().orElse(null)
        val lastNumber = if (found != null) found.number else null
        bonusBilling.defineNumber(lastNumber)
    }

    private def defineExpirationDate(bonusBilling: BonusBilling): Unit = {
        val date = Calendar.getInstance()
        date.add(Calendar.DATE, deadlineInDays)
        bonusBilling.setExpiration(date.getTime)
    }

    def save(bonusBilling: BonusBilling): BonusBilling = {
        repository.save(bonusBilling)
    }

    def process() {
        def payers = bonusService.getPayersWithBonusToProcess

        payers.forEach(payer => process(payer))
    }

    def process(payer: Person) {
        val bonuses = bonusService.getBonusesToProcessForPayer(payer.documentNumber).asScala

        bonuses.map(_.issuerId).distinct.map(issuerService.findById(_)).foreach(issuer => {
            val bonusesByIssuer = bonuses.filter(_.issuerId().equals(issuer.getId))
            val earnedBonus = bonusesByIssuer.map(_.getEarnedBonus).fold(BigDecimal.ZERO)(_.add(_))
            var bonusBilling = new BonusBilling
            bonusBilling.setMeUp(payer, issuer, earnedBonus.doubleValue())
            bonusBilling = create(bonusBilling)
            notifier.notify(Queues.BONUS_BILLING_CREATED, bonusBilling)
        })
    }

    private def validateReferences(bonusBilling: BonusBilling) {
        bonusBilling.setPayer(personService.findById(bonusBilling.personId()))
        bonusBilling.setIssuer(issuerService.findById(bonusBilling.issuerId()))
    }

    def findById(id: String): BonusBilling = {
        val BonusBilling = repository.findById(id)
        BonusBilling.orElseThrow(() => UnovationExceptions.notFound().withErrors(
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
