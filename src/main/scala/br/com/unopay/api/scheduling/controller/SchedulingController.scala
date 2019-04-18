package br.com.unopay.api.scheduling.controller

import br.com.unopay.api.model.validation.group.{Create, Update}
import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.model.filter.SchedulingFilter
import br.com.unopay.api.scheduling.service.SchedulingService
import br.com.unopay.api.util.Logging
import br.com.unopay.bootcommons.jsoncollections.{PageableResults, Results, UnovationPageRequest}
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.{DeleteMapping, GetMapping, PathVariable, PostMapping, PutMapping, RequestBody, RequestMapping, ResponseStatus, RestController}
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequestUri

@RestController
@RequestMapping(Array("/schedules"))
class SchedulingController(var schedulingService: SchedulingService) extends Logging{

    @PostMapping
    @PreAuthorize("hasRole('ROLE_MANAGE_SCHEDULING')")
    def create(@RequestBody @Validated(Array(classOf[Create])) scheduling: Scheduling): ResponseEntity[Scheduling] = {
        log.info("creating Scheduling={}", scheduling)
        val schedulingCreated = schedulingService.create(scheduling)
        val uri = buildUriLocation(schedulingCreated.id)
        ResponseEntity.created(uri).body(schedulingCreated)
    }

    @PutMapping(Array("/{id}"))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_SCHEDULING')")
    def update (@PathVariable id: String, @RequestBody @Validated(Array(classOf[Update])) scheduling: Scheduling): Unit = {
        log.info("updating Scheduling with id={}", id)
        schedulingService.update(id, scheduling)
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_LIST_SCHEDULING')")
    def findAll(schedulingFilter: SchedulingFilter, @Validated pageable: UnovationPageRequest): Results[Scheduling] = {
        log.info("filtering schedules. ScheduleFilter={}", schedulingFilter)
        val schedulesPage = schedulingService.findAll(schedulingFilter, pageable)
        pageable.setTotal(schedulesPage.getTotalElements())
        PageableResults.create(pageable, schedulesPage.getContent, fromCurrentRequestUri().toUriString)

    }

    @GetMapping(Array("/{id}"))
    @PreAuthorize("hasRole('ROLE_LIST_SCHEDULING')")
    def findById(@PathVariable id: String): Scheduling = {
        log.info("finding scheduling with id={}", id)
        schedulingService.findById(id)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(Array("/{id}"))
    @PreAuthorize("hasRole('ROLE_MANAGE_SCHEDULING')")
    def deleteById(@PathVariable id: String): Unit = {
        log.info("deleting scheduling with id={}", id)
        schedulingService.deleteById(id)
    }

    private def buildUriLocation(id: String) = {
        fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri
    }
}
