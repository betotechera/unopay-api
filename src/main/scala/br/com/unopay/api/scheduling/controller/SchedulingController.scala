package br.com.unopay.api.scheduling.controller

import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.service.SchedulingService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.{PostMapping, RequestBody, RequestMapping, RestController}
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping(Array("/schedules"))
class SchedulingController(var sechedulingService: SchedulingService) {

    @RequestMapping
    @PreAuthorize("hasRole('ROLE_MANAGE_SCHEDULES')")
    def create(@RequestBody scheduling: Scheduling): ResponseEntity[Scheduling] = {
        val schedulingCreated = sechedulingService.create(scheduling)
        val uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(schedulingCreated.id)
            .toUri

        ResponseEntity.created(uri).body(schedulingCreated)
    }

}
