package br.com.unopay.api.scheduling.controller

import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.service.SchedulingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.{PostMapping, RequestMapping, RestController}
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping("/schedules")
class SchedulingController(var sechedulingService: SchedulingService) {

    @PostMapping
    def create(scheduling: Scheduling): ResponseEntity[Scheduling] = {
        val uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand()
            .toUri
        val schedulingCreated = sechedulingService.create(scheduling)
        ResponseEntity.created(uri).body(schedulingCreated)
    }


}
