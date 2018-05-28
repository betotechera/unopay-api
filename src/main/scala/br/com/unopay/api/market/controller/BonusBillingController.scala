package br.com.unopay.api.market.controller

import java.net.URI

import br.com.unopay.api.market.model.BonusBilling
import br.com.unopay.api.market.model.filter.BonusBillingFilter
import br.com.unopay.api.market.service.BonusBillingService
import br.com.unopay.api.model.validation.group.{Create, Views}
import br.com.unopay.api.util.Logging
import br.com.unopay.bootcommons.jsoncollections.{PageableResults, Results, UnovationPageRequest}
import br.com.unopay.bootcommons.stopwatch.annotation.Timed
import com.fasterxml.jackson.annotation.JsonView
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.http.HttpStatus.{CREATED, NO_CONTENT, OK}
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMethod.{DELETE, GET, POST, PUT}
import org.springframework.web.bind.annotation._

@RestController
@Timed(prefix = "api")
@Autowired
class BonusBillingController(service: BonusBillingService) extends Logging {

    @Value("${unopay.api}")
    var api: String =_

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_BONUS_BILLING')")
    @RequestMapping(value = Array("/contractors/{id}/bonus-billings"), method = Array(PUT))
    def process(@PathVariable id: String): Unit = {
        log.info("updating negotiation={}", id)
        service.processForContractor(id)
    }

    @JsonView(Array(classOf[Views.BonusBilling.Detail]))
    @ResponseStatus(OK)
    @PreAuthorize("hasRole('ROLE_LIST_BONUS_BILLING')")
    @RequestMapping(value = Array("/bonus-billings/{id}"), method = Array(GET))
    def get(@PathVariable id: String): BonusBilling = {
        log.info("get bonus billing={}", id)
        service.findById(id)
    }

    @JsonView(Array(classOf[Views.BonusBilling.Detail]))
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_BONUS_BILLING')")
    @RequestMapping(value = Array("/bonus-billings/{id}"), method = Array(DELETE))
    def delete(@PathVariable id: String) {
        log.info("get bonus billing={}", id)
        service.delete(id)
    }

    @JsonView(Array(classOf[Views.BonusBilling.List]))
    @ResponseStatus(OK)
    @PreAuthorize("hasRole('ROLE_LIST_BONUS_BILLING')")
    @RequestMapping(value = Array("/bonus-billings"), method = Array(RequestMethod.GET))
    def getByParams(filter: BonusBillingFilter,
                                             @Validated pageable: UnovationPageRequest): Results[BonusBilling] = {
        log.info("search bonus billing with filter={}", filter)
        val page =  service.findByFilter(filter, pageable)
        pageable.setTotal(page.getTotalElements())
        PageableResults.create(pageable, page.getContent, String.format("%s/bonus-billings", api))
    }
}
