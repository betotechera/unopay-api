package br.com.unopay.api.scheduling.controller

import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.service.SchedulingService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.{PostMapping, RequestBody, RequestMapping, RestController}
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping(Array("/schedules"))
class SchedulingController(var schedulingService: SchedulingService) {

    @PostMapping
    @PreAuthorize("hasRole('ROLE_MANAGE_SCHEDULING')")
    def create(@RequestBody scheduling: Scheduling): ResponseEntity[Scheduling] = {
        val schedulingCreated = schedulingService.create(scheduling)
        val uri = buildUriLocation(schedulingCreated.id)
        ResponseEntity.created(uri).body(schedulingCreated)
    }

    private def buildUriLocation(id: String) = {
        ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri
    }
}
